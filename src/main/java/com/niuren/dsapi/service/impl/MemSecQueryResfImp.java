package com.niuren.dsapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.DataSourceEsConfig;
import com.niuren.dsapi.model.Entity.PushTaskHisEntity;
import com.niuren.dsapi.model.dao.MemDataMapper;
import com.niuren.dsapi.model.dao.PushTaskHisEntityMapper;
import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.MemSecQueryResfApi;
import com.niuren.dsapi.util.DateUtil;
import com.niuren.dsapi.util.EsQueryJsonRequest;
import com.niuren.dsapi.util.EsResfulHttpUtil;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */

@Component
public class MemSecQueryResfImp implements MemSecQueryResfApi {

    private static final Logger log = LoggerFactory.getLogger(MemSecQueryResfImp.class);
    //查询所有
    private static final String ALL_TYPE = "-1";

    @Autowired
    private MemDataMapper memDataMapper;

    @Autowired
    private PushTaskHisEntityMapper pushTaskHisEntityMapper;

    @Autowired
    DataSourceEsConfig esConfig;

    @Value("${ecrm.url}")
    private String pushUrl;

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public RestApiResult queryNumtrendRgst(NumtrendRgstDto numtrendDto) {
        log.info("MemSecQueryImp-queryNumtrendRgst begin numtrendDto=" + JSON.toJSONString(numtrendDto));
        List<NumtrendRgstResultDto> dataList = new ArrayList<NumtrendRgstResultDto>();
        try {
            //1:拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("brand", numtrendDto.getBrand()).endObject().endObject();
            builder.startObject().startObject("term").field("date_type", numtrendDto.getDate_type()).
                    endObject().endObject();
            //  //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(numtrendDto.getMarket_id()) && !StringUtils.isEmpty(numtrendDto.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", numtrendDto.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(numtrendDto.getSite_uids()) && !numtrendDto.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (numtrendDto.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uids", numtrendDto.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uids", numtrendDto.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(numtrendDto.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(numtrendDto.getTerminal_type()) && !StringUtils.isEmpty(numtrendDto.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", numtrendDto.getTerminal_type()).endObject().endObject();
            }

            if ("0".equals(numtrendDto.getDate_type())) {
                builder.startObject().startObject("term").field("stat_month", numtrendDto.getStat_date()).endObject().endObject();
            } else {
                builder.startObject().startObject("term").field("stat_year", numtrendDto.getStat_date()).endObject().endObject();
            }

            builder.endArray().endObject().endObject();
            //增加聚合查询
            builder.startObject("aggs").startObject("time");
            builder.startObject("terms").field("field", "stat_date").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("sum_user_growth").startObject("sum").field("field", "user_growth").endObject().endObject();
            builder.startObject("sum_pre_user_growth").startObject("sum").field("field", "pre_user_growth").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();
            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());

            //组装请求
            String rsURL = esConfig.getPostUrl() + "/memberrgst/memberrgst/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("data result=" + resultJson);
            //解析json
            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("time").getJSONArray("buckets");
                NumtrendRgstResultDto dto = null;
                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        dto = new NumtrendRgstResultDto();
                        dto.setTime(dataObject.getString("key"));
                        JSONObject userGrowth = dataObject.getJSONObject("sum_user_growth");
                        JSONObject preUserGrowth = dataObject.getJSONObject("sum_pre_user_growth");
                        dto.setUser_growth(DateUtil.getZRound(userGrowth.getDoubleValue("value")));

                        double grow_value = (preUserGrowth.getDoubleValue("value") < 1) ? 1.0 : preUserGrowth.getDoubleValue("value");
                        double resultGrowth = ((userGrowth.getDoubleValue("value") - preUserGrowth.getDoubleValue("value")) / grow_value);
                        //四舍五入并保留小数点两位数
                        //  double user_growth = Math.round(resultGrowth * 100) * 0.01;
                        dto.setAnnulus_growth(DateUtil.getRound(resultGrowth));
                        dataList.add(dto);
                    }

                }
            }
        } catch (Exception e) {
            log.error("query info error ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);
        }

        if (!CollectionUtils.isEmpty(dataList)) {
            if ("1".equals(numtrendDto.getDate_type())) { //如果是按年查询过剔除按天的数据 保留YYYY-MM
                dataList = dataList.stream().filter(dto -> (dto.getTime()).length() == 7).collect(Collectors.toList());
            }
            if ("0".equals(numtrendDto.getDate_type())) { //如果是按月查询过剔除按月的数据 保留YYYY-MM-DD
                dataList = dataList.stream().filter(dto -> (dto.getTime()).length() == 10).collect(Collectors.toList());
            }

            //执行按时间排序

            dataList.sort((dto1, dto2) -> dto1.getTime().compareTo(dto2.getTime()));
            log.info("MemSecQueryImp-queryNumtrendRgst end listSize=" + dataList.size());
        }
        return new RestApiResult(dataList);
    }


    public RestApiResult queryFrstorder(FrstorderDto frstorderDto) {  //-----ok------
        log.info("MemSecQueryImp-queryFrstorder begin queryFrstorder=" + JSON.toJSONString(frstorderDto));
        //返回结果集
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            //拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("brand", frstorderDto.getBrand()).endObject().endObject();
            builder.startObject().startObject("term").field("stat_date", DateUtil.getYesterday(new Date())).
                    endObject().endObject();
            //  //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(frstorderDto.getMarket_id()) && !StringUtils.isEmpty(frstorderDto.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", frstorderDto.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(frstorderDto.getSite_uids()) && !frstorderDto.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (frstorderDto.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uids", frstorderDto.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uids", frstorderDto.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(frstorderDto.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(frstorderDto.getTerminal_type()) && !StringUtils.isEmpty(frstorderDto.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", frstorderDto.getTerminal_type()).endObject().endObject();
            }

            builder.endArray().endObject().endObject();
            //增加聚合查询
            builder.startObject("aggs").startObject("time");
            builder.startObject("terms").field("field", "stat_date").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("sum_yes_rgst_pay_cnt").startObject("sum").field("field", "yes_rgst_pay_cnt").endObject().endObject();

            builder.startObject("sum_yes_rgst_cnt").startObject("sum").field("field", "yes_rgst_cnt").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();
            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());

            //组装请求
            String rsURL = esConfig.getPostUrl() + "/conversion_frstorder/conversion_frstorder/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("data result=" + resultJson);
            //解析json
            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("time").getJSONArray("buckets");
                Map<String, Object> map = null;
                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        map = new HashMap<>();
                        JSONObject sumYesPayCnt = dataObject.getJSONObject("sum_yes_rgst_pay_cnt");
                        JSONObject sumYesCnt = dataObject.getJSONObject("sum_yes_rgst_cnt");
                        double sumyesCntValue = (sumYesCnt.getDoubleValue("value") < 1) ? 1.0 : sumYesCnt.getDoubleValue("value");
                        //计算首单转化率
                        double resultGrowth = sumYesPayCnt.getDoubleValue("value") / sumyesCntValue;  // Math.round((sumYesPayCnt.getDoubleValue("value") / sumyesCntValue) * 100) * 0.01;
                        map.put("first_order_conversion", DateUtil.getRound(resultGrowth));
                        dataList.add(map);
                    }

                }
            }

        } catch (Exception e) {
            log.error("query info error ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);
        }

        log.info("MemSecQueryImp-queryFrstorder end listSize=" + dataList.size());
        return new RestApiResult(dataList);
    }


    public RestApiResult queryNumtrendFrstorder(NumtrendRgstDto numtrendDto) {
        log.info("MemSecQueryImp-queryNumtrendFrstorder begin numtrendDto=" + JSON.toJSONString(numtrendDto));

        //返回结果集
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            //拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("brand", numtrendDto.getBrand()).endObject().endObject();

            //  //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(numtrendDto.getMarket_id()) && !StringUtils.isEmpty(numtrendDto.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", numtrendDto.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(numtrendDto.getSite_uids()) && !numtrendDto.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (numtrendDto.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uids", numtrendDto.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uids", numtrendDto.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(numtrendDto.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(numtrendDto.getTerminal_type()) && !StringUtils.isEmpty(numtrendDto.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", numtrendDto.getTerminal_type()).endObject().endObject();
            }
            // 获取数据
            if (StringUtil.isNotEmpty(numtrendDto.getDate_type())) {
                builder.startObject().startObject("term").field("date_type", numtrendDto.getDate_type()).endObject().endObject();
            }

            if ("0".equals(numtrendDto.getDate_type())) {//按月查询
                builder.startObject().startObject("term").field("stat_month", numtrendDto.getStat_date()).endObject().endObject();
            } else {//按年查询
                builder.startObject().startObject("term").field("stat_year", numtrendDto.getStat_date()).endObject().endObject();
            }

            builder.endArray().endObject().endObject();
            //增加聚合查询
            builder.startObject("aggs").startObject("time");
            builder.startObject("terms").field("field", "stat_date").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("frst_order_cnt").startObject("sum").field("field", "frst_order_cnt").endObject().endObject();

            builder.endObject();
            builder.endObject().endObject();
            builder.field("size", 0);
            builder.endObject();

            log.info("request json=" + builder.string());
            //组装请求
            String rsURL = esConfig.getPostUrl() + "/memberrgst_frstorder/memberrgst_frstorder/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("data result=" + resultJson);

            //解析json并计算
            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("time").getJSONArray("buckets");
                Map<String, Object> map = null;
                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        map = new HashMap<>();
                        JSONObject userGrowth = dataObject.getJSONObject("frst_order_cnt");
                        map.put("first_order_nums", DateUtil.getZRound(userGrowth.getDoubleValue("value")));
                        map.put("time", dataObject.getString("key"));
                        dataList.add(map);
                    }
                }
            }

        } catch (Exception e) {
            log.error("query info error ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);
        }

        if (!CollectionUtils.isEmpty(dataList)) {
            if ("1".equals(numtrendDto.getDate_type())) { //如果是按年查询过剔除按天的数据 保留YYYY-MM
                dataList = dataList.stream().filter(map -> ((String) map.get("time")).length() == 7).collect(Collectors.toList());
            }
            if ("0".equals(numtrendDto.getDate_type())) { //如果是按月查询过剔除按月的数据 保留YYYY-MM-DD
                dataList = dataList.stream().filter(map -> ((String) map.get("time")).length() == 10).collect(Collectors.toList());
            }
            //按日期排序

            dataList.sort((map1, map2) -> ((String) map1.get("time")).compareTo((String) map2.get("time")));
        }
        log.info("MemSecQueryImp-queryNumtrendFrstorder end listSize=" + dataList.size());
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult queryNumtrendPayuser(NumtrendRgstDto payuser) {
        log.info("MemSecQueryImp-queryNumtrendPayuser begin numtrendDto=" + JSON.toJSONString(payuser));
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        try {
            //1:拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("brand", payuser.getBrand()).endObject().endObject();
            builder.startObject().startObject("term").field("date_type", payuser.getDate_type()).
                    endObject().endObject();
            //  //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(payuser.getMarket_id()) && !StringUtils.isEmpty(payuser.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", payuser.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(payuser.getSite_uids()) && !payuser.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (payuser.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uids", payuser.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uids", payuser.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(payuser.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(payuser.getTerminal_type()) && !StringUtils.isEmpty(payuser.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", payuser.getTerminal_type()).endObject().endObject();
            }

            if ("0".equals(payuser.getDate_type())) {
                builder.startObject().startObject("term").field("stat_month", payuser.getStat_date()).endObject().endObject();
            } else {
                builder.startObject().startObject("term").field("stat_year", payuser.getStat_date()).endObject().endObject();
            }

            builder.endArray().endObject().endObject();
            //增加聚合查询
            builder.startObject("aggs").startObject("time");
            builder.startObject("terms").field("field", "stat_date").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("transaction_order_nums").startObject("sum").field("field", "transaction_order_nums").endObject().endObject();
            builder.startObject("transaction_user_nums").startObject("sum").field("field", "transaction_user_nums").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();
            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());

            //组装请求
            String rsURL = esConfig.getPostUrl() + "/pay_user/pay_user/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("data result=" + resultJson);
            //解析json
            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("time").getJSONArray("buckets");
                Map<String, Object> dataMap = null;
                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        try {
                            dataMap = new HashMap<>();
                            dataMap.put("time", dataObject.getString("key"));
                            JSONObject orderNums = dataObject.getJSONObject("transaction_order_nums");
                            JSONObject userNums = dataObject.getJSONObject("transaction_user_nums");

                            double orderNumsValue = orderNums.getDoubleValue("value");
                            double userNumsValue = userNums.getDoubleValue("value");
                            //四舍五入并保留小数点两位数
                            dataMap.put("transaction_user_nums", DateUtil.getZRound(userNumsValue));
                            dataMap.put("transaction_order_nums", DateUtil.getZRound(orderNumsValue));
                            dataList.add(dataMap);
                        } catch (Exception e) {
                            log.error("one rsult data error info=", e);
                        }
                    }

                }
            }
        } catch (Exception e) {
            log.error("query info error ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);

        }


        if ("1".equals(payuser.getDate_type())) { //如果是按年查询过剔除按天的数据 保留YYYY-MM
            dataList = dataList.stream().filter(map -> ((String) map.get("time")).length() == 7).collect(Collectors.toList());
        }
        if ("0".equals(payuser.getDate_type())) { //如果是按月查询过剔除按月的数据 保留YYYY-MM-DD
            dataList = dataList.stream().filter(map -> ((String) map.get("time")).length() == 10).collect(Collectors.toList());
        }


        //执行按时间排序
        if (!CollectionUtils.isEmpty(dataList)) {
            dataList.sort((map1, map2) -> ((String) map1.get("time")).compareTo((String) map2.get("time")));
            log.info("MemSecQueryImp-queryNumtrendPayuser end listSize=" + dataList.size());
        }
        log.info("MemSecQueryImp-queryNumtrendPayuser end ");
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult queryAvgPrice(FrstorderDto avgPrice) {
        log.info("MemSecQueryImp-queryAvgPrice begin avgPrice=" + JSON.toJSONString(avgPrice));
        //返回结果集
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            //拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("brand", avgPrice.getBrand()).endObject().endObject();
            builder.startObject().startObject("term").field("stat_date", DateUtil.getYesterday(new Date())).
                    endObject().endObject();
            //  //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(avgPrice.getMarket_id()) && !StringUtils.isEmpty(avgPrice.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", avgPrice.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(avgPrice.getSite_uids()) && !avgPrice.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (avgPrice.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uids", avgPrice.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uids", avgPrice.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(avgPrice.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(avgPrice.getTerminal_type()) && !StringUtils.isEmpty(avgPrice.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", avgPrice.getTerminal_type()).endObject().endObject();
            }

            builder.endArray().endObject().endObject();
            //增加聚合查询
            builder.startObject("aggs").startObject("time");
            builder.startObject("terms").field("field", "stat_date").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("pay_user_cnt").startObject("sum").field("field", "pay_user_cnt").endObject().endObject();
            builder.startObject("pay_amt").startObject("sum").field("field", "pay_amt").endObject().endObject();
            builder.startObject("pay_order_cnt").startObject("sum").field("field", "pay_order_cnt").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();
            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());

            //组装请求
            String rsURL = esConfig.getPostUrl() + "/avg_price/avg_price/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("data result=" + resultJson);
            //解析json
            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("time").getJSONArray("buckets");
                Map<String, Object> map = null;
                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        map = new HashMap<>();
                        JSONObject payAmt = dataObject.getJSONObject("pay_amt");
                        JSONObject payUserCnt = dataObject.getJSONObject("pay_user_cnt");
                        JSONObject payOrderCnt = dataObject.getJSONObject("pay_order_cnt");
                        double payOrderCntValue = (payOrderCnt.getDoubleValue("value") < 1) ? 1.0 : payOrderCnt.getDoubleValue("value");
                        double payUserCntValue = (payUserCnt.getDoubleValue("value") < 1) ? 1.0 : payUserCnt.getDoubleValue("value");
                        //计算首单转化率

                        //客单价
                        double payOrderCntData = payAmt.getDoubleValue("value") / payOrderCntValue;// Math.round((payAmt.getDoubleValue("value") / payOrderCntValue) * 100) * 0.01;
                        //订单价
                        double payUserCntData = payAmt.getDoubleValue("value") / payUserCntValue;//Math.round((payAmt.getDoubleValue("value") / payUserCntValue) * 100) * 0.01;
                        map.put("order_price", DateUtil.getRound(payUserCntData));
                        map.put("per_customer_transaction", DateUtil.getRound(payOrderCntData));
                        dataList.add(map);
                    }
                }
            }

        } catch (Exception e) {
            log.error("query info error ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);

        }

        log.info("MemSecQueryImp-queryAvgPrice end listSize=");
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult querySiteuid(CrowdSiteUidReqDto sitDto) {
        log.info("MemSecQueryResfImp-querySiteuid begin sitDto=" + JSON.toJSONString(sitDto));
        List<CrowdSiteidDto> dataList = new ArrayList<>();
        //封装返回对象
        CrowdSiteidResultDto resultDto = new CrowdSiteidResultDto();
        List<String> list = sitDto.getSite_uid();
        try {
            //拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            if (!StringUtils.isEmpty(sitDto.getBrand())) {
                builder.startObject().startObject("term").field("brand", sitDto.getBrand()).endObject().endObject();
            }
            //增加站点查询
            if (!list.contains("-1")) {
                if (list.size() > 1)
                    builder.startObject().startObject("terms").array("site_uid", list.toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uid", list.get(0)).endObject().endObject();
                }
            } else { // -1 表示全部查询
                builder.startObject().startObject("match_all").endObject().endObject();
            }


            builder.endArray().endObject().endObject();
            //增加聚合查询
            builder.startObject("aggs").startObject("crowd");
            builder.startObject("terms").field("field", "crowd_id").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("sum_num").startObject("sum").field("field", "total_num").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();
            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());
            //组装请求
            String rsURL = esConfig.getPostUrl() + "/crowdsite/crowdsite/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);

            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("crowd").getJSONArray("buckets");
                CrowdSiteidDto dto = null;
                List<Double> crowdList = new ArrayList<>();

                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        dto = new CrowdSiteidDto();
                        dto.setCrowd_id(dataObject.getString("key"));
                        JSONObject sumNum = dataObject.getJSONObject("sum_num");
                        dto.setTotal_num(DateUtil.getZRound(sumNum.getDoubleValue("value")));
                        crowdList.add(dataObject.getDouble("key"));
                        dataList.add(dto);
                    }
                }

                resultDto.setCrowd_list(crowdList);
                resultDto.setData(dataList);
            }

        } catch (Exception e) {
            log.error("query info error ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);

        }


        log.info("MemSecQueryResfImp-querySiteuid  end");
        return new RestApiResult(resultDto);

    }

    @Override
    public RestApiResult queryCrowd(MemMarketDto markDto) {
        log.info("MemSecQueryResfImp-queryCrowd begin markDto=" + JSON.toJSONString(markDto));
        List<CrowQueryDto> dataList = new ArrayList<>();
        try {
            //拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            if (!StringUtils.isEmpty(markDto.getBrand())) {
                builder.startObject().startObject("term").field("brand", markDto.getBrand()).endObject().endObject();
            }
            builder.startObject().startObject("term").field("stat_date", DateUtil.getYesterday(new Date())).
                    endObject().endObject();

            //  //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(markDto.getMarket_id()) && !StringUtils.isEmpty(markDto.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", markDto.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(markDto.getSite_uids()) && !markDto.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (markDto.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uid", markDto.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uid", markDto.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(markDto.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(markDto.getTerminal_type()) && !StringUtils.isEmpty(markDto.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", markDto.getTerminal_type()).endObject().endObject();
            }
            //人群ID
            if (!CollectionUtils.isEmpty(markDto.getFollow_list())) {
                List<String> croList = new ArrayList<>();
                for (PeopleGroupDto croDto : markDto.getFollow_list()) {
                    croList.add(croDto.getCrowd_id());
                }
                builder.startObject().startObject("terms").array("crowd_id", croList.toArray()).endObject().endObject();
            }

            builder.endArray().endObject().endObject();
            //增加聚合查询-begin

            builder.startObject("aggs").startObject("crowd");
            builder.startObject("terms").field("field", "crowd_id").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("total_num").startObject("sum").field("field", "total_num").endObject().endObject();
            builder.startObject("yesterday_visitors_num").startObject("sum").field("field", "yesterday_visitors_num").endObject().endObject();
            builder.startObject("yesterday_transactions_num").startObject("sum").field("field", "yesterday_transactions_num").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();
            //增加聚合查询-end
            builder.field("size", 0);

            builder.endObject();
            log.info("request json=" + builder.string());

            String rsURL = esConfig.getPostUrl() + "/crowdquery/crowdquery/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);

            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("crowd").getJSONArray("buckets");

                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        CrowQueryDto queryDto = new CrowQueryDto();
                        CrowdExportResultDto dto = new CrowdExportResultDto();
                        dto.setTotal_num(DateUtil.getZRound(dataObject.getJSONObject("total_num").getDoubleValue("value")));
                        dto.setYesterday_visitors_num(DateUtil.getZRound(dataObject.getJSONObject("yesterday_visitors_num").getDoubleValue("value")));
                        dto.setYesterday_transactions_num(DateUtil.getRound(dataObject.getJSONObject("yesterday_transactions_num").getDoubleValue("value")));
                        queryDto.setCrowd_id(dataObject.getString("key"));
                        queryDto.setData(dto);
                        dataList.add(queryDto);
                    }
                }
                // 针对es中空人群ID处理
                checkCrowdIdList(dataList, markDto);
            }

        } catch (Exception e) {
            log.error("query info fail ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);

        }

        log.info("MemSecQueryResfImp-query  end");
        return new RestApiResult(dataList);
    }

    /**
     * 检查crowid是否抽取到es中
     */
    public void checkCrowdIdList(List<CrowQueryDto> dataList, MemMarketDto markDto) {
        if (CollectionUtils.isEmpty(markDto.getFollow_list())) {
            log.info("checkCrowdIdList crowList is empty!");
            return;
        }
        //1:检查crowid是否在该任务中->1查询已经有数据crowdid-> 2crowdid参数列表剔除1中已经存在的-> 3.而且存在es库中
        //A:查询es 昨天已经跑的crowid列表
        List<String> crowdList = getCrowdIdList(markDto);
        //*******入参列表不存在的已经返回的结果集中*******
        List<String> noInCrowList = new ArrayList<>();
        //已经查询出的crowdid
        Map<String, String> dataCrowdMap = new HashMap<>();
        for (CrowQueryDto queryDto : dataList) {
            if (!StringUtils.isEmpty(queryDto.getCrowd_id())) {
                dataCrowdMap.put(queryDto.getCrowd_id(), queryDto.getCrowd_id());
            }
        }

        log.info("1-checkCrowdIdList-crowdList size=" + crowdList.size());
        log.info("1-checkCrowdIdList-dataCrowdMap size=" + dataCrowdMap.size());


        //当其他条件不会空->人群ID 此处必传不做校验
        if (!StringUtils.isEmpty(markDto.getBrand()) || !StringUtils.isEmpty(markDto.getMarket_id()) || !StringUtils.isEmpty(markDto.getTerminal_type()) || !StringUtils.isEmpty(markDto.getTerminal_type()) || !CollectionUtils.isEmpty(markDto.getSite_uids())) {
            //B:检查查询的Crowid是否在返回列表中，如果不存在则去es任务中查询是否存在如果存在则默认默认其他统计为0，如果不存在不予处理说明任务没有跑

            List<PeopleGroupDto> pepList = markDto.getFollow_list();
            for (PeopleGroupDto peopleGroupDto : pepList) {
                if (StringUtil.isNotEmpty(peopleGroupDto.getCrowd_id()) && !dataCrowdMap.containsKey(peopleGroupDto.getCrowd_id())) {
                    noInCrowList.add(peopleGroupDto.getCrowd_id());
                }
            }


            List<String> needAddList = new ArrayList<>();
            //B2:把 不存在的 去除 查询是否在es 中
            log.info("2-checkCrowdIdList-noInCrowList size=" + noInCrowList.size());
            if (!CollectionUtils.isEmpty(noInCrowList)) {
                for (String crowId : noInCrowList) {
                    if (crowdList.contains(crowId)) {
                        needAddList.add(crowId);
                    }
                }
            }
            //B3把存在es的crowid手动放入到返回列表中
            log.info("3-checkCrowdIdList-needAddList size=" + needAddList.size());
            if (!CollectionUtils.isEmpty(needAddList)) {
                for (String crowId : needAddList) {
                    CrowQueryDto queryDto = new CrowQueryDto();
                    CrowdExportResultDto dto = new CrowdExportResultDto();
                    dto.setTotal_num(DateUtil.getZRound(0d));
                    dto.setYesterday_visitors_num(DateUtil.getZRound(0d));
                    dto.setYesterday_transactions_num(DateUtil.getRound(0d));
                    queryDto.setCrowd_id(crowId);
                    queryDto.setData(dto);
                    dataList.add(queryDto);
                }
            }
            log.info("4-checkCrowdIdList-checkCrowdIdList end");

        }

    }

    /**
     * 获取昨天任务的CrowdId列表
     *
     * @return
     */
    public List<String> getCrowdIdList(MemMarketDto markDto) {
        List<String> crowdList = new ArrayList<>();
        try {
            //拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("stat_date", DateUtil.getYesterday(new Date())).
                    endObject().endObject();

            builder.endArray().endObject().endObject();
            //增加聚合查询-begin

            builder.startObject("aggs").startObject("crowd");
            builder.startObject("terms").field("field", "crowd_id").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("total_num").startObject("sum").field("field", "total_num").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();
            //增加聚合查询-end
            builder.field("size", 0);

            builder.endObject();
            log.info("request json=" + builder.string());

            String rsURL = esConfig.getPostUrl() + "/crowdquery/crowdquery/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);

            if (!StringUtils.isEmpty(resultJson)) {
                JSONObject jsonObject = JSON.parseObject(resultJson);
                JSONObject aggregations = jsonObject.getJSONObject("aggregations");
                JSONArray bucketList = aggregations.getJSONObject("crowd").getJSONArray("buckets");

                for (int i = 0; i < bucketList.size(); i++) {
                    JSONObject dataObject = bucketList.getJSONObject(i);
                    if (dataObject != null) {
                        crowdList.add(dataObject.getString("key"));
                    }
                }
            }

        } catch (Exception e) {
            log.error("getCrowdIdList-query info fail ", e);
        }

        return crowdList;
    }


    @Override
    public RestApiResult crowdExport(CrowdExportRequestDto exportDto) {
        log.info("MemSecQueryResfImp-crowdExport begin markDto=" + JSON.toJSONString(exportDto));
        if (!StringUtils.isEmpty(exportDto.getMarket_id())) {//如果市场ID存在返回报文结构如下:
            return doHasMarket(exportDto);
        } else {
            return doHasNoMarket(exportDto);
        }
    }

    /**
     * 如果市场ID存在返回报文结构如下:
     *
     * @param exportDto
     * @return
     */
    public RestApiResult doHasMarket(CrowdExportRequestDto exportDto) {
        log.info("MemSecQueryResfImp-doHasMarket begin ");
        List<CrowdExportResultDto> dataList = new ArrayList<>();
        try {
            //拼接查询ES
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            if (!StringUtils.isEmpty(exportDto.getBrand())) {
                builder.startObject().startObject("term").field("brand", exportDto.getBrand()).endObject().endObject();
            }
            if (!StringUtils.isEmpty(exportDto.getCrowd_id())) {//人群ID
                builder.startObject().startObject("term").field("crowd_id", exportDto.getCrowd_id()).endObject().endObject();
            }
            // 开始结束时间查询
            if (!StringUtils.isEmpty(exportDto.getStart_time()) && !StringUtils.isEmpty(exportDto.getEnd_time())) {
                builder.startObject().startObject("range").startObject("stat_date").field("gte", exportDto.getStart_time()).field("lte", exportDto.getEnd_time()).
                        endObject().endObject().endObject();
            }

            //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(exportDto.getMarket_id()) && !StringUtils.isEmpty(exportDto.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", exportDto.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(exportDto.getSite_uids()) && !exportDto.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (exportDto.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uid", exportDto.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uid", exportDto.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(exportDto.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(exportDto.getTerminal_type()) && !StringUtils.isEmpty(exportDto.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", exportDto.getTerminal_type()).endObject().endObject();
            }


            builder.endArray().endObject().endObject();

            builder.startObject("aggs").startObject("crowd");
            builder.startObject("terms").field("field", "stat_date").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("total_num").startObject("sum").field("field", "total_num").endObject().endObject();
            builder.startObject("yesterday_transactions_num").startObject("sum").field("field", "yesterday_transactions_num").endObject().endObject();
            builder.startObject("yesterday_visitors_num").startObject("sum").field("field", "yesterday_visitors_num").endObject().endObject();
            builder.endObject();
            builder.endObject().endObject();

            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());

            String rsURL = esConfig.getPostUrl() + "/crowdquery/crowdquery/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);


            JSONObject jsonObject = JSON.parseObject(resultJson);
            JSONObject aggregations = jsonObject.getJSONObject("aggregations");
            JSONArray bucketList = aggregations.getJSONObject("crowd").getJSONArray("buckets");
            CrowdExportResultDto exportResDto = null;

            //如果市场ID存在返回报文结构如下:
            for (int i = 0; i < bucketList.size(); i++) {
                JSONObject dataObject = bucketList.getJSONObject(i);
                if (dataObject != null) {
                    exportResDto = new CrowdExportResultDto();
                    exportResDto.setTime(dataObject.getString("key"));
                    exportResDto.setTotal_num(DateUtil.getZRound(dataObject.getJSONObject("total_num").getDoubleValue("value")));
                    exportResDto.setYesterday_visitors_num(DateUtil.getZRound(dataObject.getJSONObject("yesterday_visitors_num").getDoubleValue("value")));
                    exportResDto.setYesterday_transactions_num(DateUtil.getRound(dataObject.getJSONObject("yesterday_transactions_num").getDoubleValue("value")));
                    dataList.add(exportResDto);
                }
            }


        } catch (Exception e) {
            log.error("query info fail ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);
        }
        if (!CollectionUtils.isEmpty(dataList)) {
            dataList.sort((dto1, dto2) -> dto1.getTime().compareTo(dto2.getTime()));
            log.info("MemSecQueryResfImp-doHasMarket  size=" + dataList.size());
        }

        log.info("MemSecQueryResfImp-doHasMarket  end");
        return new RestApiResult(dataList);
    }

    /**
     * 市场ID不存在的情况下
     *
     * @param exportDto
     * @return
     */
    public RestApiResult doHasNoMarket(CrowdExportRequestDto exportDto) {
        log.info("MemSecQueryResfImp-doHasNoMarket  begin");
        //功能：先按日期分组->统计日期当天总数量（人群总数，昨日访问总数量等），在日期分组基础上按终端类型分组， 统计该日期下相应终端的 （人群总数，昨日访问总数量等）
        List<CrowdExportResDto> dataList = new ArrayList<>();
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            if (!StringUtils.isEmpty(exportDto.getBrand())) {
                builder.startObject().startObject("term").field("brand", exportDto.getBrand()).endObject().endObject();
            }
            if (!StringUtils.isEmpty(exportDto.getCrowd_id())) {//人群ID
                builder.startObject().startObject("term").field("crowd_id", exportDto.getCrowd_id()).endObject().endObject();
            }
            // 开始结束时间查询
            if (!StringUtils.isEmpty(exportDto.getStart_time()) && !StringUtils.isEmpty(exportDto.getEnd_time())) {
                builder.startObject().startObject("range").startObject("stat_date").field("gte", exportDto.getStart_time()).field("lte", exportDto.getEnd_time()).
                        endObject().endObject().endObject();
            }

            //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(exportDto.getMarket_id()) && !StringUtils.isEmpty(exportDto.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", exportDto.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!CollectionUtils.isEmpty(exportDto.getSite_uids()) && !exportDto.getSite_uids().contains(ALL_TYPE)) {//非全部查询，切列表大于1
                if (exportDto.getSite_uids().size() > 1)
                    builder.startObject().startObject("terms").array("site_uid", exportDto.getSite_uids().toArray()).endObject().endObject();
                else {//单个查询时
                    builder.startObject().startObject("term").field("site_uid", exportDto.getSite_uids().get(0)).endObject().endObject();
                }
            }
            //增加 终端查询
            if ("APP".equals(exportDto.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
            } else if (!ALL_TYPE.equals(exportDto.getTerminal_type()) && !StringUtils.isEmpty(exportDto.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", exportDto.getTerminal_type()).endObject().endObject();
            }
            builder.endArray().endObject().endObject();

            //**************************aggs 执行-begin**************************
            //先按日期分组统计，在日期基础上按设备类型分组统计
            builder.startObject("aggs").startObject("totals");
            builder.startObject("terms").field("field", "stat_date").field("collect_mode", "breadth_first").endObject();

            builder.startObject("aggs");//1-aggs
            builder.startObject("terminal_type");
            builder.startObject("terms").field("field", "terminal").endObject();
            builder.startObject("aggs");//2-aggs

            // 按站点分组
            builder.startObject("site_uids");
            builder.startObject("terms").field("field", "site_uid").endObject();
            builder.endObject();

            builder.startObject("yesterday_transactions_num").startObject("sum").field("field", "yesterday_transactions_num").endObject().endObject();
            builder.startObject("yesterday_visitors_num").startObject("sum").field("field", "yesterday_visitors_num").endObject().endObject();
            builder.startObject("total_num").startObject("sum").field("field", "total_num").endObject().endObject();


            builder.endObject();//2-aggs
            builder.endObject();//terminal_type

            builder.startObject("transactions_num").startObject("sum").field("field", "yesterday_transactions_num").endObject().endObject();
            builder.startObject("visitors_num").startObject("sum").field("field", "yesterday_visitors_num").endObject().endObject();
            builder.startObject("total_num").startObject("sum").field("field", "total_num").endObject().endObject();

            builder.endObject();//1-aggs

            builder.endObject().endObject();

            //**************************aggs-end***************************

            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());
            //2：执行数据解析-->根据站点为全部时 -->二级站点为空
            String rsURL = esConfig.getPostUrl() + "/crowdquery/crowdquery/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);
            //3：执行数据解析
            dataList = doOptCrowdExportData(resultJson, exportDto);

        } catch (Exception e) {
            log.error("query info fail ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);

        }

        log.info("MemSecQueryResfImp-doHasNoMarket  end");
        return new RestApiResult(dataList);
    }

    /**
     * 处理解析返回的json
     *
     * @return
     */
    private List<CrowdExportResDto> doOptCrowdExportData(String resultJson, CrowdExportRequestDto exportDto) {
        boolean flag = true;//是否需要显示查询二级站点-> -1 表示全部无需查询显示二级站点
        if (exportDto != null && !CollectionUtils.isEmpty(exportDto.getSite_uids())) {
            if (exportDto.getSite_uids().contains(ALL_TYPE)) {
                flag = false;
            }
        }

        if (StringUtil.isEmpty(resultJson)) {
            log.warn("doOptCrowdExportData-resultJson is empty !");
            return new ArrayList<>();
        }
        log.info("doOptCrowdExportData-  resultJson  begin flag=" + flag);

        JSONObject jsonObject = JSON.parseObject(resultJson);
        JSONArray bucketsArray = jsonObject.getJSONObject("aggregations").getJSONObject("totals").getJSONArray("buckets");
        List<CrowdExportResDto> dataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bucketsArray)) {
            CrowdExportResDto resDto = null;
            List<CrowdExportResultDto> detailList = null;
            for (int i = 0; i < bucketsArray.size(); i++) {
                JSONObject bucketObject = bucketsArray.getJSONObject(i);
                detailList = new ArrayList<>();
                resDto = new CrowdExportResDto();
                CrowdExportResultDto resultDto = new CrowdExportResultDto();
                //1：按日期分组统计 获取相应数据总数量
                resDto.setTime(bucketObject.getString("key"));
                resultDto.setSite_uid("");
                resultDto.setTerminal("全部");
                resultDto.setTotal_num(DateUtil.getZRound(bucketObject.getJSONObject("total_num").getDoubleValue("value")));
                resultDto.setYesterday_visitors_num(DateUtil.getZRound(bucketObject.getJSONObject("visitors_num").getDoubleValue("value")));
                resultDto.setYesterday_transactions_num(DateUtil.getRound(bucketObject.getJSONObject("transactions_num").getDoubleValue("value")));
                detailList.add(resultDto);//记录每天的Total 并放入首页

                //2：每天数据中的按设备类型分类
                JSONArray sonBucketArray = bucketObject.getJSONObject("terminal_type").getJSONArray("buckets");
                if (!CollectionUtils.isEmpty(sonBucketArray)) {
                    for (int j = 0; j < sonBucketArray.size(); j++) {
                        // 记录每天中按设备分组数量
                        CrowdExportResultDto sonResultDto = new CrowdExportResultDto();
                        JSONObject sonbucketObject = sonBucketArray.getJSONObject(j);
                        //站点不为全部时候，需要查询每个终端对应的二级站点
                        String sites = "";//可能出现一个终端对应多个站点
                        if (flag) {
                            JSONArray siteArray = sonbucketObject.getJSONObject("site_uids").getJSONArray("buckets");
                            if (!CollectionUtils.isEmpty(sonBucketArray)) {
                                for (int s = 0; s < siteArray.size(); s++) {
                                    try {
                                        JSONObject siteObject = siteArray.getJSONObject(s);
                                        String siteKey = siteObject.getString("key");
                                        if (StringUtil.isNotEmpty(siteKey)) {
                                            sites += siteKey + " ";
                                        }
                                    } catch (Exception e) {
                                        log.warn("JSONObject get siteKey  fail ", e);
                                    }
                                }
                            }
                        }
                        sonResultDto.setSite_uid(sites);
                        sonResultDto.setTerminal(sonbucketObject.getString("key"));
                        sonResultDto.setTotal_num(DateUtil.getZRound(sonbucketObject.getJSONObject("total_num").getDoubleValue("value")));
                        sonResultDto.setYesterday_visitors_num(DateUtil.getZRound(sonbucketObject.getJSONObject("yesterday_visitors_num").getDoubleValue("value")));
                        sonResultDto.setYesterday_transactions_num(DateUtil.getRound(sonbucketObject.getJSONObject("yesterday_transactions_num").getDoubleValue("value")));
                        detailList.add(sonResultDto);
                    }
                }
                resDto.setDetail(detailList);
                dataList.add(resDto);
            }
        }
        if (!CollectionUtils.isEmpty(dataList)) {
            dataList.sort((dto1, dto2) -> dto1.getTime().compareTo(dto2.getTime()));
            log.info("MemSecQueryResfImp-queryMemList  end doOptCrowdExportData=" + dataList);
        }
        return dataList;
    }


    @Override
    public RestApiResult queryMemList(MemListRequestDto reuqestDto) {
        log.info("MemSecQueryResfImp-queryMemList  begin");
        int totalSize = 0;
        List<Map<String, Object>> dataList = new ArrayList<>();
        int pageSize = (reuqestDto.getPageSize() == null) ? 20 : reuqestDto.getPageSize();//每页查询数据大小
        int pageNumber = (reuqestDto.getPageNumber() == null) ? 1 : reuqestDto.getPageNumber();//第几页
        int startIndex = reuqestDto.getPageSize() * (pageNumber - 1);
        MemListQueryDto memQueryDto = reuqestDto.getCondition();

        log.info("MemSecQueryResfImp-queryMemList  startIndex=" + startIndex + ",pageSize=" + pageSize + ",pageNumber=" + pageNumber);
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");

            if (memQueryDto.getBuyCnt() != null && !StringUtils.isEmpty(memQueryDto.getBuyCnt().getFloorValue()) && !StringUtils.isEmpty(memQueryDto.getBuyCnt().getCeilValue())) {
                builder.startObject().startObject("range").startObject("buyCnt".toLowerCase()).field("gte", memQueryDto.getBuyCnt().getFloorValue()).field("lte", memQueryDto.getBuyCnt().getCeilValue()).
                        endObject().endObject().endObject();
            }

            if (memQueryDto.getBirthday() != null && !StringUtils.isEmpty(memQueryDto.getBirthday().getFloorValue()) && !StringUtils.isEmpty(memQueryDto.getBirthday().getCeilValue())) {
                builder.startObject().startObject("range").startObject("birthday").field("gte", memQueryDto.getBirthday().getFloorValue()).field("lte", memQueryDto.getBirthday().getCeilValue()).
                        endObject().endObject().endObject();

            }
            if (memQueryDto.getRegisterTime() != null && !StringUtils.isEmpty(memQueryDto.getRegisterTime().getFloorValue()) && !StringUtils.isEmpty(memQueryDto.getRegisterTime().getCeilValue())) {
                builder.startObject().startObject("range").startObject("registertime").field("gte", memQueryDto.getRegisterTime().getFloorValue()).field("lte", memQueryDto.getRegisterTime().getCeilValue()).
                        endObject().endObject().endObject();
            }
            //集合类型
            if (!CollectionUtils.isEmpty(memQueryDto.getAccountType())) {
                if (memQueryDto.getAccountType().size() > 1) {
                    builder.startObject().startObject("terms").field("accounttype", memQueryDto.getAccountType().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("accounttype", memQueryDto.getAccountType().get(0)).endObject().endObject();
                }
            }

            if (!CollectionUtils.isEmpty(memQueryDto.getLevel())) {
                if (memQueryDto.getLevel().size() > 1) {
                    builder.startObject().startObject("terms").field("level", memQueryDto.getLevel().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("level", memQueryDto.getLevel().get(0)).endObject().endObject();
                }
            }

            if (!CollectionUtils.isEmpty(memQueryDto.getEmailSuffix())) {
                if (memQueryDto.getEmailSuffix().size() > 1) {
                    builder.startObject().startObject("terms").field("emailsuffix", memQueryDto.getEmailSuffix().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("emailsuffix", memQueryDto.getEmailSuffix().get(0)).endObject().endObject();
                }
            }

            if (!CollectionUtils.isEmpty(memQueryDto.getSiteUid())) {
                if (memQueryDto.getSiteUid().size() > 1) {
                    builder.startObject().startObject("terms").field("siteuid", memQueryDto.getSiteUid().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("siteuid", memQueryDto.getSiteUid().get(0)).endObject().endObject();
                }
            }


            if (!CollectionUtils.isEmpty(memQueryDto.getShppPhoneNo())) {
                if (memQueryDto.getShppPhoneNo().size() > 1) {
                    builder.startObject().startObject("terms").field("shppphoneno", memQueryDto.getShppPhoneNo().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("shppphoneno", memQueryDto.getShppPhoneNo().get(0)).endObject().endObject();
                }
            }

            if (!CollectionUtils.isEmpty(memQueryDto.getEmail())) {
                if (memQueryDto.getEmail().size() > 1) {
                    builder.startObject().startObject("terms").field("email", memQueryDto.getEmail().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("email", memQueryDto.getEmail().get(0)).endObject().endObject();
                }
            }

            if (!CollectionUtils.isEmpty(memQueryDto.getNickName())) {
                if (memQueryDto.getNickName().size() > 1) {
                    builder.startObject().startObject("terms").field("nickname", memQueryDto.getNickName().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("nickname", memQueryDto.getNickName().get(0)).endObject().endObject();
                }
            }

            if (!CollectionUtils.isEmpty(memQueryDto.getMarketId())) {
                if (memQueryDto.getMarketId().size() > 1) {
                    builder.startObject().startObject("terms").field("marketid", memQueryDto.getMarketId().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("marketid", memQueryDto.getMarketId().get(0)).endObject().endObject();
                }
            }

            if (!CollectionUtils.isEmpty(memQueryDto.getShppCountryId())) {
                if (memQueryDto.getShppCountryId().size() > 1) {
                    builder.startObject().startObject("terms").field("shppcountryid", memQueryDto.getShppCountryId().toArray()).endObject().endObject();
                } else {
                    builder.startObject().startObject("term").field("shppcountryid", memQueryDto.getShppCountryId().get(0)).endObject().endObject();
                }
            }

            if (memQueryDto.getEmailBlacklistFlag() != null) {
                builder.startObject().startObject("term").field("emailblacklistflag", memQueryDto.getEmailBlacklistFlag()).endObject().endObject();
            }
            if (memQueryDto.getEmailVerifyFlag() != null) {
                builder.startObject().startObject("term").field("emailverifyflag", memQueryDto.getEmailVerifyFlag()).endObject().endObject();
            }
            if (memQueryDto.getSubscribeFlag() != null) {
                builder.startObject().startObject("term").field("subscribeflag", memQueryDto.getSubscribeFlag()).endObject().endObject();
            }

            if (memQueryDto.getBlackFlag() != null) {
                builder.startObject().startObject("term").field("blackflag", memQueryDto.getBlackFlag()).endObject().endObject();
            }
            builder.endArray().endObject().endObject();

            builder.field("from", startIndex);
            builder.field("size", pageSize);
            builder.endObject();

            log.info("request-info=" + builder.string());

            //2：执行数据解析-->根据站点为全部时 -->二级站点为空
            String rsURL = esConfig.getPostUrl() + "/memberlabel/memberlabel/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);

            //2:数据查询解析
            if (StringUtil.isNotEmpty(resultJson)) {
                //获取命中数据组
                JSONArray hitsArray = JSON.parseObject(resultJson).getJSONObject("hits").getJSONArray("hits");
                totalSize = JSON.parseObject(resultJson).getJSONObject("hits").getInteger("total");
                for (int i = 0; i < hitsArray.size(); i++) {
                    JSONObject objcet = hitsArray.getJSONObject(i);
                    if (objcet != null) {
                        Map<String, Object> valueMap = null;
                        JSONObject sourceObject = objcet.getJSONObject("_source");
                        if (sourceObject != null) {
                            valueMap = new HashMap<>();
                            valueMap.put("memberId", sourceObject.get("memberid"));
                            valueMap.put("email", sourceObject.get("email"));
                            valueMap.put("level", sourceObject.get("level"));
                            valueMap.put("buyAmt", DateUtil.getRound(sourceObject.getDoubleValue("buyamt")));
                            valueMap.put("buyCnt", sourceObject.get("buycnt"));
                            valueMap.put("avgAmt", DateUtil.getRound(sourceObject.getDoubleValue("avgamt")));
                            valueMap.put("lastOrderTime", sourceObject.get("lastordertime"));
                            valueMap.put("blackFlag", sourceObject.get("blackflag"));
                            dataList.add(valueMap);
                        }
                    }

                }
            }

        } catch (Exception e) {
            log.error("query info fail ", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);

        }
        int size = 0;
        if (!CollectionUtils.isEmpty(dataList)) {
            size = dataList.size();
        }
        log.info("MemSecQueryResfImp-queryMemList  end dataList=" + size);

        return new RestApiResult(dataList, totalSize);
    }

    @Override
    public RestApiResult queryStatus(PeopleGroupDto peopleGroupDto) {
        log.info("MemSecQueryResfImp-queryStatus  begin=" + JSON.toJSONString(peopleGroupDto));
        //执行sql 查询Oracle 数据库
        String hiveTaskId = "bm_" + peopleGroupDto.getCrowd_id();
        try {
            //字符串日期格式
            String dateDt = peopleGroupDto.getDt();
            // 执行查询ORACLE操作逻辑如下
            //当结果大于等于1执ok 否则返回异常提醒
            Map<String, String> map = new HashMap<String, String>();
            map.put("dt", DateUtil.getYYYYMMDDYesterday(dateDt));
            map.put("taskId", hiveTaskId);
            log.info("queryStatus-jsonMap=" + JSON.toJSONString(map));
            Integer flag = memDataMapper.findeJobCount(map);
            log.info("MemSecQueryResfImp-queryStatus result-flag=" + flag);
            if (flag >= 1) {
                return new RestApiResult(new ArrayList());
            } else {
                return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, new ArrayList());
            }

        } catch (Exception e) {
            log.warn("MemSecQueryResfImp-queryStatus message=", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, null, new ArrayList());
        } finally {
            log.info("MemSecQueryResfImp-queryStatus  end");
        }

    }

    @Override
    public RestApiResult conversion(ConversionDto conver) {
        log.info("MemSecQueryResfImp-conversion begin=");
        List<CrowdConversionResultDto> dataList = new ArrayList<>();
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            if (!StringUtils.isEmpty(conver.getPlanId())) {
                builder.startObject().startObject("term").field("planid", conver.getPlanId()).endObject().endObject();
            }
            builder.endArray().endObject().endObject();
            builder.field("size", 5000);
            builder.endObject();
            log.info("request-info=" + builder.string());

            //2：执行数据解析
            String rsURL = esConfig.getPostUrl() + "/crowdconversion/crowdconversion/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);

            if (StringUtil.isNotEmpty(resultJson)) {
                //获取命中数据组
                Map<String, List<CrowdConversionDto>> listMap = new HashMap<>();
                JSONArray hitsArray = JSON.parseObject(resultJson).getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < hitsArray.size(); i++) {
                    JSONObject objcet = hitsArray.getJSONObject(i);
                    if (objcet != null) {
                        CrowdConversionDto conDto = null;
                        JSONObject sourceObject = objcet.getJSONObject("_source");
                        if (sourceObject != null) {
                            conDto = new CrowdConversionDto();
                            conDto.setBranch_name(sourceObject.getString("branch_name"));
                            conDto.setBuyMoney(DateUtil.getRound(sourceObject.getDoubleValue("buymoney")));
                            conDto.setMemberCnt(sourceObject.getString("membercnt"));
                            conDto.setBuyCnt(sourceObject.getString("buycnt"));
                            conDto.setLastUpdateTime(sourceObject.getString("last_update_time") + " 23:59:59");
                            if (StringUtil.isNotEmpty(sourceObject.getString("senddate"))) {
                                List<CrowdConversionDto> crowdList = null;
                                if (listMap.containsKey(sourceObject.getString("senddate"))) {
                                    crowdList = listMap.get(sourceObject.getString("senddate"));
                                    crowdList.add(conDto);
                                } else {
                                    crowdList = new ArrayList<>();
                                    crowdList.add(conDto);
                                }
                                listMap.put(sourceObject.getString("senddate"), crowdList);
                            }
                        }
                    }

                }
                //3：解析Map组装报文
                if (listMap != null) {
                    CrowdConversionResultDto resultDto = null;
                    for (Map.Entry entry : listMap.entrySet()) {
                        resultDto = new CrowdConversionResultDto();
                        List<CrowdConversionDto> daList = (List) entry.getValue();
                        if (!CollectionUtils.isEmpty(daList)) {
                            daList.sort((dto1, dto2) -> (dto1.getBranch_name()).compareTo((dto1.getBranch_name())));
                        }
                        resultDto.setData(daList);
                        resultDto.setSendDate((String) entry.getKey());
                        dataList.add(resultDto);
                    }
                }

            }
            //4:执行排序
            if (!CollectionUtils.isEmpty(dataList)) {
                dataList.sort((dto1, dto2) -> ((String) dto1.getSendDate()).compareTo(((String) dto1.getSendDate())));
                log.info("MemSecQueryResfImp-conversion end=" + dataList.size());
            }

        } catch (Exception e) {
            log.warn("MemSecQueryResfImp-conversion message=", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, null, new ArrayList());
        }

        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult contribution(ContributionDto contribution) {
        //1: 查询
        log.info("MemSecQueryResfImp-contribution begin=");
        Map<String, Object> resultMap = new HashMap<>();
        List<ContributionResultdto> dataList = new ArrayList<ContributionResultdto>();
        try {
            int leavls[] = {0, 1, 2, 3, 4, 5};
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("terms").array("level", leavls).endObject().endObject();
            builder.startObject().startObject("range").startObject("stat_dt").field("gte", contribution.getStart_time()).field("lte", contribution.getEnd_time()).
                    endObject().endObject().endObject();
            builder.startObject().startObject("term").field("market_id", contribution.getMarket_id()).endObject().endObject();
            builder.endArray().endObject().endObject();
            builder.field("size", 5000);
            builder.startObject("sort").startObject("level").field("order", "asc").endObject().endObject();
            builder.endObject();
            log.info("request-info=" + builder.string());

            //2：执行数据请求
            String rsURL = esConfig.getPostUrl() + "/membercontribution/membercontribution/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);
            //3：执行数据解析
            if (StringUtil.isNotEmpty(resultJson)) {
                //获取命中数据组
                Map<String, List<CrowdConversionDto>> listMap = new HashMap<>();
                JSONArray hitsArray = JSON.parseObject(resultJson).getJSONObject("hits").getJSONArray("hits");

                ContributionResultdto resulDto = null;
                //用于合并汇总相同日期下数据
                Map<String, Object> mapVlue = new HashMap();

                for (int i = 0; i < hitsArray.size(); i++) {
                    JSONObject objcet = hitsArray.getJSONObject(i);
                    if (objcet != null) {
                        JSONObject sourceObject = objcet.getJSONObject("_source");
                        if (sourceObject != null) {
                            if (mapVlue.containsKey(sourceObject.getString("stat_dt"))) {
                                resulDto = (ContributionResultdto) mapVlue.get(sourceObject.getString("stat_dt"));
                            } else {
                                resulDto = new ContributionResultdto();
                            }

                            resulDto.setTime(sourceObject.getString("stat_dt"));
                            List<ContributionEnDto> listDto = resulDto.getData();
                            if (CollectionUtils.isEmpty(listDto)) {
                                listDto = new ArrayList();
                            }
                            //组装对象
                            ContributionEnDto enDto = new ContributionEnDto();
                            enDto.setAvgOffer(sourceObject.getString("avgoffer"));
                            enDto.setLevel(sourceObject.getString("level"));
                            listDto.add(enDto);
                            resulDto.setData(listDto);
                            //放入仓库
                            mapVlue.put(sourceObject.getString("stat_dt"), resulDto);
                        }
                    }
                }
                //执行数据合并
                for (Map.Entry<String, Object> entry : mapVlue.entrySet()) {
                    if (entry.getValue() != null) {
                        dataList.add((ContributionResultdto) entry.getValue());
                    }
                }
            }


            //按日期排序
            dataList.sort((dto1, dto2) -> dto1.getTime().compareTo(dto2.getTime()));
            resultMap.put("collection", dataList);
            //4:获取综合 getTotalContribution
            String avgoffer = getTotalContribution(contribution);
            resultMap.put("yesterdayAvgOffer", avgoffer);
            //5：执行追加 并排序

        } catch (Exception e) {
            log.error("request fail = ", e);
        }
        return new RestApiResult(resultMap);

    }

    /**
     * 获取聚合中的值
     *
     * @return
     */
    public String getTotalContribution(ContributionDto contribution) {
        //1: 查询
        log.info("MemSecQueryResfImp-getTotalContribution begin=");
        String avgoffer = "0";
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("level", -1).endObject().endObject();
            builder.startObject().startObject("term").field("stat_dt", DateUtil.getYesterday(new Date())).endObject().endObject();
            builder.startObject().startObject("term").field("market_id", contribution.getMarket_id()).endObject().endObject();
            builder.endArray().endObject().endObject();
            builder.field("size", 5000);
            builder.endObject();
            log.info("request-info=" + builder.string());

            //2：执行数据请求
            String rsURL = esConfig.getPostUrl() + "/membercontribution/membercontribution/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);
            //3：执行数据解析
            JSONArray hitsArray = JSON.parseObject(resultJson).getJSONObject("hits").getJSONArray("hits");
            for (int i = 0; i < hitsArray.size(); i++) {
                JSONObject objcet = hitsArray.getJSONObject(i);
                if (objcet != null) {
                    JSONObject sourceObject = objcet.getJSONObject("_source");
                    if (sourceObject != null) {
                        avgoffer = DateUtil.getRound(sourceObject.getLongValue("avgoffer"));
                    }
                }
            }

        } catch (Exception e) {
            log.error("request fail ", e);
        }
        log.info("MemSecQueryResfImp-getTotalContribution begin=" + avgoffer);

        return avgoffer;
    }

    @Override
    public RestApiResult querylevel(MemMarketDto markDto) {
        //1: 查询
        log.info("MemSecQueryResfImp-querylevel  begin");
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("market_id", markDto.getMarket_id()).endObject().endObject();

            builder.endArray().endObject().endObject();
            builder.field("size", 5000);
            builder.endObject();
            log.info("request-info=" + builder.string());

            //2：执行数据请求
            String rsURL = esConfig.getPostUrl() + "/memberlevel/memberlevel/_search";
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, builder.string(), null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("result json=" + resultJson);

            JSONArray hitsArray = JSON.parseObject(resultJson).getJSONObject("hits").getJSONArray("hits");
            Map<String, Object> valueMap = null;
            for (int i = 0; i < hitsArray.size(); i++) {
                JSONObject object = hitsArray.getJSONObject(i);
                if (object != null) {
                    JSONObject sourceObject = object.getJSONObject("_source");
                    if (sourceObject != null) {
                        valueMap = new HashMap<>();
                        valueMap.put("level", sourceObject.getString("level"));
                        valueMap.put("total", DateUtil.getRound(sourceObject.getLongValue("total")));
                        valueMap.put("percent", DateUtil.getRound(sourceObject.getLongValue("percent")));
                        dataList.add(valueMap);
                    }
                }
            }

        } catch (Exception e) {
            log.error("request fail ", e);
        }

        log.info("MemSecQueryResfImp-querylevel  end" + dataList.size());
        return new RestApiResult(dataList);

    }

    @Override
    public RestApiResult doPushTaskCheck(PushTaskRequestDto requstDto) {
        //1：校验任务是否存在
        log.info("MemSecQueryResfImp-doPushTaskCheck requstDto  Info=" + JSON.toJSONString(requstDto));
        PushTaskHisEntity taskHisEntity = pushTaskHisEntityMapper.selectByTaskId(requstDto.getTask_id());
        if (taskHisEntity != null) {
            log.info("MemGroupOptImp-doPushTaskCheck  taskId has exist Info=" + JSON.toJSONString(taskHisEntity));
            return new RestApiResult(Constant.ERROR_OTHER, "taskId has exist", null, new HashMap());
        }
        //数据执行sql
        try {
            //如果不存在 执行 do somthings
            String rsURL = pushUrl + "/api/BMSchdPostData";
            String request = JSON.toJSONString(requstDto);
            log.info("doPushTaskCheck-request-info=" + request);
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, request, null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("doPushTaskCheck-result-info=" + resultJson);
            if (StringUtil.isNotEmpty(resultJson)) {
                Map resMap = JSON.parseObject(resultJson, Map.class);
                if (StringUtil.isNotEmpty(resMap.get("msg")) && "OK".equals((String) resMap.get("msg"))) {
                    // 执行
                    PushTaskHisEntity record = new PushTaskHisEntity();
                    record.setTaskId(requstDto.getTask_id());
                    pushTaskHisEntityMapper.insertSelective(record);
                    log.info("doPushTaskCheck-db-info=" + requstDto.getTask_id());
                    return new RestApiResult(new HashMap());
                }
            }
        } catch (Exception e) {
            log.error("MemSecQueryResfImp-doPushTaskCheck-fail ", e);
            return new RestApiResult(Constant.ERROR_OTHER, e.getMessage(), null, new HashMap());
        }
        return new RestApiResult(Constant.ERROR_OTHER, "send  push fail", null, new HashMap());
    }

    @Override
    public RestApiResult queryPushPlan(PushPlanRequestDto requstDto) {
        log.warn("MemSecQueryResfImp-queryPushPlan-begin " + JSON.toJSONString(requstDto));
        //构建DSL 查询
        EsQueryJsonRequest queryJosn = new EsQueryJsonRequest();
        try {
            queryJosn.getBuilderStart();

            //1:bool查询开始
            queryJosn.getBoolQueryStart();
            queryJosn.setTerm("plan_id", requstDto.getPlanId());
            //TODO something
            queryJosn.getBoolQueryEnd();
            //汇总
            if ("01".equals(requstDto.getFlag())) {
                //2:聚合查询开始
                queryJosn.getModeAggsStart("planaggs", "plan_id", 5000);
                queryJosn.setAggsSum("sum_pv", "pv");
                queryJosn.setAggsSum("sum_order_cnt", "order_cnt");
                queryJosn.setAggsSum("sum_click_cnt", "click_cnt");
                queryJosn.setAggsSum("sum_order_amt", "order_amt");
                queryJosn.getModeAggsEnd();
                //结束
                queryJosn.getBuilderEnd(1);
            } else {//明细
                queryJosn.getBuilderEnd(5000);
            }

            //2:返回Json结果并查询
            String request = queryJosn.getJsonString();
            String rsURL = esConfig.getPostUrl() + "/crowdconversionpush/crowdconversionpush/_search";

            log.info("request-info=" + request);
            String resultJson = EsResfulHttpUtil.postOrPutOrDelete(rsURL, request, null, EsResfulHttpUtil.OkHttpMethod.POST);
            log.info("request-result=" + resultJson);

            //执行解析
            if (StringUtil.isNotEmpty(resultJson)) {
                EsDataResultDto resultDto = JSON.parseObject(resultJson, EsDataResultDto.class);
                //汇总
                if ("01".equals(requstDto.getFlag())) {
                    PushPlanResultDto dto = new PushPlanResultDto();
                    if (resultDto != null) {
                        JSONArray jsonArray = resultDto.getBuckets("planaggs");
                        if (!CollectionUtils.isEmpty(jsonArray)) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject != null) {
                                dto.setPv(DateUtil.getZRound(jsonObject.getJSONObject("sum_pv").getDoubleValue("value")));
                                dto.setOrderCnt(DateUtil.getZRound(jsonObject.getJSONObject("sum_order_cnt").getDoubleValue("value")));
                                dto.setOrderAmt(DateUtil.getRound(jsonObject.getJSONObject("sum_order_amt").getDoubleValue("value")));
                                dto.setClickCnt(DateUtil.getZRound(jsonObject.getJSONObject("sum_click_cnt").getDoubleValue("value")));
                            }
                        }
                        //获取汇总时间
                        if (resultDto.getHits() != null && !CollectionUtils.isEmpty(resultDto.getHits().getHits())) {
                            HitsDto hitDto = resultDto.getHits().getHits().get(0);
                            if (hitDto != null) {
                                JSONObject jsonObject = hitDto.get_source();
                                if (jsonObject != null) {
                                    dto.setLastUpdateTime(jsonObject.getString("last_update_time") + " 23:59:59");
                                }
                            }

                        }

                    }
                    return new RestApiResult(dto);
                } else {//明细02
                    List<PushPlanResultDto> dataList = new ArrayList<>();
                    if (resultDto != null) {
                        PushPlanResultDto dto = new PushPlanResultDto();
                        List<HitsDto> hitList = resultDto.getHits().getHits();
                        for (HitsDto hitDto : hitList) {
                            JSONObject jsonObject = hitDto.get_source();
                            if (jsonObject != null) {
                                dto = new PushPlanResultDto();
                                dto.setPv(DateUtil.getZRound(jsonObject.getDoubleValue("pv")));
                                dto.setOrderCnt(DateUtil.getZRound(jsonObject.getDoubleValue("order_cnt")));
                                dto.setClickCnt(DateUtil.getZRound(jsonObject.getDoubleValue("click_cnt")));
                                dto.setOrderAmt(DateUtil.getRound(jsonObject.getDoubleValue("order_amt")));
                                dto.setLastUpdateTime(jsonObject.getString("last_update_time") + " 23:59:59");
                                dto.setBranchName(jsonObject.getString("branch_name"));
                                dataList.add(dto);
                            }
                        }
                    }
                    return new RestApiResult(dataList);
                }
            }
        } catch (Exception e) {
            log.error("MemSecQueryResfImp-queryPushPlan-fail ", e);
            return new RestApiResult(Constant.ERROR_OTHER, e.getMessage(), null, new HashMap());
        }
        log.info("MemSecQueryResfImp-queryPushPlan-end ");
        return new RestApiResult(new HashMap());
    }

}
