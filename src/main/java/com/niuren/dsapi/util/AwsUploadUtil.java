package com.niuren.dsapi.util;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.niuren.dsapi.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * AWS上传工具类
 */

public class AwsUploadUtil {
    private static final Logger log = LoggerFactory.getLogger(AwsUploadUtil.class);
    protected static AmazonS3 client;
    //Access Key（访问密钥
    protected static String AWS_ACCESS_KEY = "AKIAJMHSB77BWZWWPQMQ"; // 【你的 access_key】
    //Secret Key（秘密密钥）
    protected static String AWS_SECRET_KEY = "hbYXf4JCPbDf4akPKkPsllQYJHMrYbkqZBPbH7dt"; // 【你的 aws_secret_key】

    protected static String bucketName = "dsdata01"; // 【你 bucket 的名字】 # 首先需要保证 s3 上已经存在该存储桶

    static {
        //首先创建一个s3的客户端操作对象（需要amazon提供的密钥）
        log.info("****AwsUploadUtil-init client begin****");
        try {
             client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .build();
           // client = new AmazonS3Client(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));
           // client.setRegion(Region.getRegion(Regions.US_WEST_2)); // 此处根据自己的 s3 地区位置改变
        } catch (Exception e) {
            log.error("AwsUploadUtil-init error=" + e);
            throw new ServiceException("AwsUploadUtil-init-fail");
        }
        log.info("****AwsUploadUtil-init client end****");
    }

    /**
     * 上传数据
     *
     * @param tempFile
     * @param remoteFileName
     * @return
     * @throws IOException
     */
    public static String uploadToS3(File tempFile, String remoteFileName, String filePath) {
        try {
            String bucketPath = bucketName + filePath;
            client.putObject(new PutObjectRequest(bucketPath, remoteFileName, tempFile)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, remoteFileName);
            URL url = client.generatePresignedUrl(urlRequest);
            log.info("****AwsUploadUtil-uploadToS3 url****" + url);
            return url.toString();
        } catch (Exception ase) {
            log.error("****AwsUploadUtil-uploadToS3 error****", ase);
            throw new ServiceException("uploadToS3-fail");
        }
    }

    /**
     * 上传数据
     *
     * @param inputStream 文件流
     * @param namekey     上传后文件名称
     * @return
     * @throws IOException
     */
    @SuppressWarnings({"deprecation", "unused"})
    public static String uploadToS3(InputStream inputStream, String namekey, String filePath) {
        log.info("uploadToS3======key: " + namekey);
        try {
            //验证名称为bucketName的bucket是否存在，不存在则创建
//          if (!checkBucketExists(s3, bucket_name)) {
//              s3.createBucket(bucket_name);
//          }
            //   ObjectMetadata omd = new ObjectMetadata();
//            omd.setContentType(fileData.get(0).getContentType());
//            omd.setContentLength(fileData.get(0).getSize());
//            omd.setHeader("filename", fileData.get(0).getName());
            //上传文件
            S3Object s3Object = new S3Object();
            s3Object.setObjectContent(inputStream);
            //上传目录
            String bucketPath = bucketName + filePath;
            client.putObject(new PutObjectRequest(bucketPath, namekey, inputStream, null));
            s3Object.close();
//            client.putObject(bucketName, namekey, inputStream, null);
//              s3Object = client.putObject(new PutObjectRequest(bucketName, namekey));
//            s3Object.close();
            //获取一个request
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, namekey);
            //生成公用的url
            URL url = client.generatePresignedUrl(urlRequest);
            log.info("uploadToS3=========URL=================" + url);
            return url.toString();
        } catch (Exception ase) {
            log.error("****AwsUploadUtil-uploadToS3 error****", ase);
            throw new ServiceException("uploadToS3-fail");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("****AwsUploadUtil-uploadToS3 stream close error****");
                }
            }
        }
    }


    /**
     * amazonS3文件下载
     *
     * @param filePath 下载某个存储桶的数据
     * @param fileName 下载文件的key
     * @param response 下载文件保存地址
     */
    public static void awsDownloading(String filePath, String fileName, HttpServletResponse response) {
        String bucketPath = bucketName + filePath;
        S3Object object = client.getObject(new GetObjectRequest(bucketPath, fileName));
        if (object != null) {
            System.out.println("Content-Type: " + object.getObjectMetadata().getContentType());
            InputStream input = null;
            // FileOutputStream fileOutputStream = null;
            OutputStream out = null;
            byte[] data = null;
            try {
                //获取文件流
                //信息头，相当于新建的名字
                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                input = object.getObjectContent();
                data = new byte[input.available()];
                int len = 0;
                out = response.getOutputStream();
                //fileOutputStream = new FileOutputStream(targetFilePath);
                while ((len = input.read(data)) != -1) {
                    out.write(data, 0, len);
                }
                object.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //关闭输入输出流
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        log.error("****AwsUploadUtil-amazonS3Downloading error****", e);
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        log.error("****AwsUploadUtil-amazonS3Downloading error****", e);
                    }
                }

            }
        }
    }


}
