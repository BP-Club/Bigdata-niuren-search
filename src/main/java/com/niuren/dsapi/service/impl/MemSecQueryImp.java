package com.niuren.dsapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.MemSecQueryApi;
import com.niuren.dsapi.util.DateUtil;
import com.niuren.dsapi.util.EsResfulHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;


@Component
public class MemSecQueryImp implements MemSecQueryApi {

    private static final Logger log = LoggerFactory.getLogger(MemSecQueryImp.class);
    //查询所有
    private static final String ALL_TYPE = "-1";

    @Override
    public RestApiResult queryNumtrendRgst(NumtrendRgstDto numtrendDto) {
        log.info("MemSecQueryImp-queryNumtrendRgst begin numtrendDto=" + JSON.toJSONString(numtrendDto));
        //1：must普通条件查询
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.termQuery("brand", numtrendDto.getBrand()));
        boolQuery.must(QueryBuilders.termQuery("date_type", numtrendDto.getDate_type()));
        //如果是查询所有则默认不加入查询条件
        if (!ALL_TYPE.equals(numtrendDto.getMarket_id())) {
            boolQuery.must(QueryBuilders.termQuery("market_id", numtrendDto.getMarket_id()));
        }
        //增加站点查询
        if (!ALL_TYPE.equals(numtrendDto.getSite_uids()) && !CollectionUtils.isEmpty(numtrendDto.getSite_uids())) {//非全部查询，切列表大于1
            if (numtrendDto.getSite_uids().size() > 1)
                boolQuery.must(QueryBuilders.termsQuery("site_uid", numtrendDto.getSite_uids().toArray()));
            else {//单个查询时
                boolQuery.must(QueryBuilders.termQuery("site_uid", numtrendDto.getSite_uids().get(0)));
            }
        }
        //增加 终端查询
        if ("APP".equals(numtrendDto.getTerminal_type())) {
            boolQuery.must(QueryBuilders.termsQuery("terminal", new String[]{"IOS", "ANDROID"}));
        } else if (!ALL_TYPE.equals(numtrendDto.getTerminal_type()) && !StringUtils.isEmpty(numtrendDto.getTerminal_type())) {
            boolQuery.must(QueryBuilders.termQuery("terminal", numtrendDto.getTerminal_type()));
        }
        //增加日期查询：根据年查询，根据月查询
        if ("0".equals(numtrendDto.getDate_type())) {
            boolQuery.must(QueryBuilders.termQuery("stat_month", numtrendDto.getStat_date()));
        } else {
            boolQuery.must(QueryBuilders.termQuery("stat_year", numtrendDto.getStat_date()));
        }
        log.info("queryinfo =" + boolQuery.toString());
        //2：增加聚合查询
        AggregationBuilder aggsBuilder = AggregationBuilders.terms("time")
                .field("stat_date")
                .size(5000)
                .subAggregation(AggregationBuilders.sum("sum_user_growth").field("user_growth"))
                .subAggregation(AggregationBuilders.sum("sum_pre_user_growth").field("pre_user_growth"));

        log.info("queryinfo-aggs =" + aggsBuilder.toString());
        SearchResponse response = null;//clientUtil.getClient().prepareSearch("memberrgst").setTypes("memberrgst")
               // .setQuery(boolQuery).addAggregation(aggsBuilder).setSize(0).get();

        //3:执行并调用数据返回
        NumtrendRgstResultDto dto = null;
        List<NumtrendRgstResultDto> dataList = new ArrayList<>();
        Terms terms = response.getAggregations().get("time");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            dto = new NumtrendRgstResultDto();
            dto.setTime(bucket.getKeyAsString());

            //用户增长量
            Sum userGrowth = (Sum) bucket.getAggregations().getAsMap().get("sum_user_growth");
            dto.setUser_growth(userGrowth.getValueAsString());

            //计算环比增长率
            Sum preUserGrowth = (Sum) bucket.getAggregations().getAsMap().get("sum_pre_user_growth");
            double grow_value = (preUserGrowth.getValue() < 1) ? 1.0 : preUserGrowth.getValue();
            double resultGrowth = ((userGrowth.getValue() - preUserGrowth.getValue()) / grow_value);
            //四舍五入并保留小数点两位数
            double user_growth = Math.round(resultGrowth * 100) * 0.01;
            dto.setUser_growth(Double.toString(user_growth));
            dataList.add(dto);
        }
        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        //执行按时间排序
        dataList.sort((dto1, dto2) -> dto1.getTime().compareTo(dto2.getTime()));
        log.info("MemSecQueryImp-queryNumtrendRgst end listSize=" + dataList.size());
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult queryFrstorder(FrstorderDto frstorderDto) {
        log.info("MemSecQueryImp-queryFrstorder begin queryFrstorder=" + JSON.toJSONString(frstorderDto));
        //1：must普通条件查询
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.termQuery("brand", frstorderDto.getBrand()));
        boolQuery.must(QueryBuilders.termQuery("stat_date", DateUtil.getYesterday(new Date())));
        //如果是查询所有则默认不加入查询条件
        if (!ALL_TYPE.equals(frstorderDto.getMarket_id())) {
            boolQuery.must(QueryBuilders.termQuery("market_id", frstorderDto.getMarket_id()));
        }
        //增加站点查询
        if (!ALL_TYPE.equals(frstorderDto.getSite_uids()) && !CollectionUtils.isEmpty(frstorderDto.getSite_uids())) {//非全部查询，切列表大于1
            if (frstorderDto.getSite_uids().size() > 1)
                boolQuery.must(QueryBuilders.termsQuery("site_uid", frstorderDto.getSite_uids().toArray()));
            else {//单个查询时
                boolQuery.must(QueryBuilders.termQuery("site_uid", frstorderDto.getSite_uids().get(0)));
            }
        }
        //增加 终端查询
        if ("APP".equals(frstorderDto.getTerminal_type())) {
            boolQuery.must(QueryBuilders.termsQuery("terminal", new String[]{"IOS", "ANDROID"}));
        } else if (!ALL_TYPE.equals(frstorderDto.getTerminal_type()) && !StringUtils.isEmpty(frstorderDto.getTerminal_type())) {
            boolQuery.must(QueryBuilders.termQuery("terminal", frstorderDto.getTerminal_type()));
        }
        log.info("queryinfo-boolQuery =" + boolQuery.toString());
        //聚合查询
        AggregationBuilder aggsBuilder = AggregationBuilders.terms("time")
                .field("stat_date")
                .size(5000)
                .subAggregation(AggregationBuilders.sum("sum_yes_rgst_pay_cnt").field("yes_rgst_pay_cnt"))
                .subAggregation(AggregationBuilders.sum("sum_yes_rgst_cnt").field("yes_rgst_cnt"));

        log.info("queryinfo-aggs =" + aggsBuilder.toString());
        SearchResponse response = null;//clientUtil.getClient().prepareSearch("conversion_frstorder").setTypes("conversion_frstorder")
               // .setQuery(boolQuery).addAggregation(aggsBuilder).setSize(0).get();
        Terms terms = response.getAggregations().get("time");

        Map<String, Object> map = null;
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            map = new HashMap<>();
            Sum sumYesPayCnt = (Sum) bucket.getAggregations().getAsMap().get("sum_yes_rgst_pay_cnt");
            Sum sumYesCnt = (Sum) bucket.getAggregations().getAsMap().get("sum_yes_rgst_cnt");
            double sumyesCntValue = (sumYesCnt.getValue() < 1) ? 1.0 : sumYesCnt.getValue();
            //计算首单转化率
            double resultGrowth = Math.round((sumYesCnt.getValue() / sumyesCntValue) * 100) * 0.01;

            map.put("first_order_conversion", Double.toString(resultGrowth));
            dataList.add(map);
        }
        if (CollectionUtils.isEmpty(dataList)) {
            log.info("MemSecQueryImp-queryFrstorder end empty");
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        log.info("MemSecQueryImp-queryFrstorder end listSize=" + dataList.size());
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult queryNumtrendFrstorder(NumtrendRgstDto numtrendDto) {
        log.info("MemSecQueryImp-queryNumtrendFrstorder begin numtrendDto=" + JSON.toJSONString(numtrendDto));
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {

            //1：must普通条件查询
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("query").startObject("bool").startArray("must");
            builder.startObject().startObject("term").field("brand", numtrendDto.getBrand()).endObject().endObject();
            builder.startObject().startObject("term").field("date_type", numtrendDto.getDate_type()).endObject().endObject();

            //如果是查询所有则默认不加入查询条件
            if (!ALL_TYPE.equals(numtrendDto.getMarket_id())) {
                builder.startObject().startObject("term").field("market_id", numtrendDto.getMarket_id()).endObject().endObject();
            }
            //增加站点查询
            if (!ALL_TYPE.equals(numtrendDto.getSite_uids()) && !CollectionUtils.isEmpty(numtrendDto.getSite_uids())) {//非全部查询，切列表大于1
                if (numtrendDto.getSite_uids().size() > 1) {
                    builder.startObject().startObject("terms").array("site_uids", numtrendDto.getSite_uids().toArray()).endObject().endObject();
//                boolQuery.must(QueryBuilders.termsQuery("site_uid", numtrendDto.getSite_uids().toArray()));
                } else {//单个查询时
                    builder.startObject().startObject("term").field("site_uids", numtrendDto.getSite_uids().get(0)).endObject().endObject();
//                boolQuery.must(QueryBuilders.termQuery("site_uid", numtrendDto.getSite_uids().get(0)));
                }
            }
            //增加 终端查询
            if ("APP".equals(numtrendDto.getTerminal_type())) {
                builder.startObject().startObject("terms").array("terminal", new String[]{"IOS", "ANDROID"}).endObject().endObject();
//            boolQuery.must(QueryBuilders.termsQuery("terminal", new String[]{"IOS", "ANDROID"}));
            } else if (!ALL_TYPE.equals(numtrendDto.getTerminal_type()) && !StringUtils.isEmpty(numtrendDto.getTerminal_type())) {
                builder.startObject().startObject("term").field("terminal", numtrendDto.getTerminal_type()).endObject().endObject();
//            boolQuery.must(QueryBuilders.termQuery("terminal", numtrendDto.getTerminal_type()));
            }
            //增加日期查询：根据年查询，根据月查询
            if ("0".equals(numtrendDto.getDate_type())) {
                builder.startObject().startObject("term").field("stat_month", numtrendDto.getStat_date()).endObject().endObject();
//            boolQuery.must(QueryBuilders.termQuery("stat_month", numtrendDto.getStat_date()));
            } else {
                builder.startObject().startObject("term").field("stat_year", numtrendDto.getStat_date()).endObject().endObject();
//            boolQuery.must(QueryBuilders.termQuery("stat_year", numtrendDto.getStat_date()));
            }
            //2：增加聚合查询
            builder.endArray().endObject().endObject();
            //增加聚合查询
            builder.startObject("aggs").startObject("time");
            builder.startObject("terms").field("field", "stat_date").field("size", 5000).endObject();
            builder.startObject("aggs");
            builder.startObject("frst_order_cnt").startObject("sum").field("field", "frst_order_cnt").endObject().endObject().endObject();
            builder.endObject().endObject();
            builder.field("size", 0);
            builder.endObject();
            log.info("request json=" + builder.string());

            String rsURL = Constant.AWS_URL + "/memberrgst/memberrgst/_search";
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
                        double resultGrowth = Math.round((sumYesPayCnt.getDoubleValue("value") / sumyesCntValue) * 100) * 0.01;
                        map.put("first_order_conversion", Double.toString(resultGrowth));
                        dataList.add(map);
                    }

                }
            }

        } catch (IOException e) {
            log.error("", e);
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        log.info("MemSecQueryImp-queryNumtrendFrstorder end listSize=" + dataList.size());
        return new RestApiResult(dataList);

//        AggregationBuilder aggsBuilder = AggregationBuilders.terms("time")
//                .field("stat_date")
//                .size(5000)
//                .subAggregation(AggregationBuilders.sum("frst_order_cnt").field("frst_order_cnt"));

                //.setQuery(boolQuery).addAggregation(aggsBuilder).setSize(0).get();

//        SearchResponse response = clientUtil.getClient().prepareSearch("memberrgst").setTypes("memberrgst")
//                .setQuery(boolQuery).addAggregation(aggsBuilder).setSize(0).get();
//
//        //3:执行并调用数据返回
//        Map<String, Object> map = null;
//        List<Map<String, Object>> dataList = new ArrayList<>();
//        Terms terms = response.getAggregations().get("time");
//        for (Terms.Bucket bucket : terms.getBuckets()) {
//            map = new HashMap<>();
//            Sum userGrowth = (Sum) bucket.getAggregations().getAsMap().get("frst_order_cnt");
//            map.put("first_order_ nums", userGrowth.getValueAsString());
//            map.put("time", bucket.getKeyAsString());
//            dataList.add(map);
//        }
//        if (CollectionUtils.isEmpty(dataList)) {
//            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
//        }
//        log.info("MemSecQueryImp-queryNumtrendFrstorder end listSize=" + dataList.size());
//        return new RestApiResult(dataList);
    }
}
