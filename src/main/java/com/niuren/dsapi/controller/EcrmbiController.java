package com.niuren.dsapi.controller;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.service.impl.RestApiResult;
import com.niuren.dsapi.util.AwsUploadUtil;
import com.niuren.dsapi.util.EsResfulHttpUtil;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户量趋势查询接口
 */

@RestController
@RequestMapping("/ecrmbi/data")
public class EcrmbiController {
    private static final Logger log = LoggerFactory.getLogger(EcrmbiController.class);
    @Value("${aws.filepath}")
    private String filePath;

    @Value("${ecrm.url}")
    private String ecrmUrl;

    public String getEcrmUrl() {
        return ecrmUrl;
    }

    public void setEcrmUrl(String ecrmUrl) {
        this.ecrmUrl = ecrmUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 上传数据到AWS-S3服务器
     *
     * @param type push ,mail
     * @param file
     * @return
     */
    @PostMapping("/uploadData")
    public RestApiResult uploadDataAws(@RequestParam("fileName") MultipartFile file, String type,String brand) {
        log.info("EcrmbiController-uploadDataAws begin type=" + type);
        String fileName = "";
        //1：非空校验
        if (file.isEmpty()) {
            log.warn("file is empty:" + Constant.EROR_MSG_NOFIND);
            return new RestApiResult(Constant.ERROR_NORULE, "file is empty", new ArrayList(), null);
        }
        //2：大小校验
        else if (file.getSize() > Constant.AWS_MAXUPLOAD_SIZE) {
            fileName = file.getOriginalFilename();
            String msg = "请确保文件小于:" + (Constant.AWS_MAXUPLOAD_SIZE / 1024 / 1024) + " MB ";
            log.warn("fileName=" + fileName + msg);
            return new RestApiResult(Constant.ERROR_NORULE, msg, new ArrayList(), null);
        }

        //3提取后缀验证
        String suffix = "";
        try {
            fileName = file.getOriginalFilename();
            suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            if (!".csv".equals(suffix.toLowerCase())) {
                log.info("error suffix==== " + fileName);
                return new RestApiResult(Constant.ERROR_NORULE, "文件格式错误，请上传csv文件!", new ArrayList(), null);
            }
        } catch (Exception e) {
            log.warn("文件后缀格式获取取异常" + file.getName(), e);
            return new RestApiResult(Constant.ERROR_NORULE, "文件格式错误，请上传csv文件!", new ArrayList(), null);
        }


        try {
            String fn = "push";
            //重命名文件名时间戳
            if ("email".equals(type)) {//根据参数下载不同模板
                fn = "email";
            } else if ("test".equals(type)) {
                fn = "test";
            }
            fileName = "bmupload_" + fn + "_" + System.currentTimeMillis();
            log.info("fileName=" + fileName + ".csv");
            //1：上传文件
            AwsUploadUtil.uploadToS3(file.getInputStream(), fileName + ".csv", filePath);
            //2:通知上传成功
            Map<String, Object> params = new HashMap<>();
            params.put("filename", fileName);
            params.put("brand", brand);
            String url = ecrmUrl + "/api/BMSchdCrowd";
            EsResfulHttpUtil.get(url, params, null);

        } catch (Exception e) {
            log.error("EcrmbiController-uploadDataAws error", e);
            return new RestApiResult(Constant.ERROR_OTHER, "上传出错，其他错误!", new ArrayList(), null);
        }
        return new RestApiResult(fileName);
    }


    /**
     * 模板下载
     *
     * @param type     push ,mail
     * @param response
     */
    @RequestMapping(value = "/templeDown", method = RequestMethod.GET)
    public void templeDown(HttpServletResponse response, String type, String name) {
        String fileName = "push_temple.csv";
        if ("email".equals(type)) {//根据参数下载不同模板
            fileName = "email_temple.csv";
        }
        if (StringUtil.isNotEmpty(name)) {
            fileName = name + ".csv";
        }

        AwsUploadUtil.awsDownloading(filePath, fileName, response);
        log.info("tempDownload-success" + fileName);
    }


    /**
     * 模板下载
     *
     * @param request
     */
    @RequestMapping(value = "/api/bmSchdAbt", method = RequestMethod.POST)
    public RestApiResult bmSchdAbt(@RequestBody Object request) {
        log.info("EcrmbiController-bmSchdAbt-begin" + JSON.toJSONString(request));
        //透传数据
        String result = "";
        try {
            if (request == null) {
                return new RestApiResult(Constant.ERROR_OTHER, "param is empty", new ArrayList(), null);
            }
            result = EsResfulHttpUtil.postOrPutOrDelete(ecrmUrl + "/api/BMSchdAbt", JSON.toJSONString(request), null, EsResfulHttpUtil.OkHttpMethod.POST);
            Map map = JSON.parseObject(result, Map.class);
            if (map != null && "succeed".equals(map.get("msg"))) {
                return new RestApiResult("ok");
            }
        } catch (Exception e) {
            return new RestApiResult(Constant.ERROR_OTHER, e.getMessage(), new ArrayList(), null);
        }
        log.info("EcrmbiController-bmSchdAbt-end result=" + result);
        return new RestApiResult(Constant.ERROR_OTHER, "其他错误", new ArrayList(), null);
    }

}
