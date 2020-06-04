package com.niuren.dsapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.model.dao.MemDataMapper;
import com.niuren.dsapi.model.dto.*;
import com.niuren.dsapi.service.MemGroupOptApi;
import com.niuren.dsapi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class MemGroupOptImp implements MemGroupOptApi {


    private static final Logger log = LoggerFactory.getLogger(MemGroupOptImp.class);
    @Autowired
    private MemDataMapper memDataMapper;

    @Override
    public RestApiResult query(MemMarketDto markDto) {
        log.info("MemGroupOptImp-query begin markDto=" + JSON.toJSONString(markDto));
        //获取ID列表
        List<String> croList = new ArrayList<>();
        for (PeopleGroupDto croDto : markDto.getFollow_list()) {
            croList.add(croDto.getCrowd_id());
        }
        //1:组装数据
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.termQuery("market_id", markDto.getMarket_id()));
        boolQuery.must(QueryBuilders.termsQuery("crowd_id", croList.toArray()));
        log.info("queryinfo=" + boolQuery.toString());
        //2:数据执行查询
        SearchResponse response = null;//clientUtil.getClient().prepareSearch("crowd").setTypes("query").setQuery(boolQuery).get();
        log.info("MemGroupOptImp-query  length=" + response.getHits().getHits().length);
        //3：组装返回数据
        Map<String, Object> valueMap = null;
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SearchHit searchHit : response.getHits().getHits()) {
            valueMap = new HashMap<>();
            valueMap.put("total_num", searchHit.getSource().get("total_num"));
            valueMap.put("yesterday_visitors_num", searchHit.getSource().get("yesterday_transactions_num"));
            valueMap.put("Yesterday_transactions_num", searchHit.getSource().get("yesterday_visitors_num"));
            dataList.add(valueMap);
        }
        //4：校验是否有此数据
        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        log.info("MemGroupOptImp-query  end");
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult queryStatus(PeopleGroupDto peopleGroupDto) {
        log.info("MemGroupOptImp-queryStatus  begin=" + JSON.toJSONString(peopleGroupDto));
        //执行sql 查询Oracle 数据库
        String hiveTaskId = "bm_" + peopleGroupDto.getCrowd_id();
        try {
            //字符串日期格式
            String dateDt = peopleGroupDto.getDt();


            //TODO  执行查询ORACLE操作逻辑如下
            //当结果大于等于1执ok 否则返回异常提醒
            Map<String, String> map = new HashMap<String, String>();
            map.put("dt", DateUtil.getYesterday(dateDt));
            map.put("taskId", hiveTaskId);
            log.info("queryStatus-jsonMap=" + JSON.toJSONString(map));
            if (memDataMapper.findeJobCount(map) >= 1) {
                return new RestApiResult(new ArrayList());
            } else {
                return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, new ArrayList());
            }

        } catch (Exception e) {
            log.warn("MemGroupOptImp-queryStatus message=", e);
            return new RestApiResult(Constant.ERROR_NORULE, Constant.EROR_MSG_NORULE, new ArrayList(), null);
        } finally {
            log.info("MemGroupOptImp-queryStatus  end");
        }

    }

    @Override
    public RestApiResult queryMemList(MemListQueryDto memQueryDto) {

        log.info("MemGroupOptImp-queryMemList  end dataList=" + memQueryDto);
        return new RestApiResult(null);
    }

    @Override
    public RestApiResult conversion(ConversionDto conver) {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.termsQuery("planid", conver.getPlanId()));
        SearchResponse response = null;// clientUtil.getClient().prepareSearch("crowdconversion").setTypes("crowdconversion")
        //  .addSort("senddate", SortOrder.DESC).setQuery(boolQuery).get();

        Map<String, Object> valueMap = null;
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            valueMap = new HashMap<>();
            valueMap.put("sendDate", hit.getSource().get("senddate"));
            valueMap.put("memberCnt", hit.getSource().get("membercnt"));
            valueMap.put("buyCnt", hit.getSource().get("buycnt"));
            valueMap.put("buyMoney", hit.getSource().get("buymoney"));
            dataList.add(valueMap);
        }
        //4：校验是否有此数据
        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult contribution(ContributionDto contribution) {
        log.info("MemGroupOptImp-contribution  begin");
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        int leavls[] = {-1, 0, 1, 2, 3, 4, 5};
        boolQuery.must(QueryBuilders.termsQuery("level".toLowerCase(), leavls));//????????????-1时候可能是全部待确定
        boolQuery.must(QueryBuilders.rangeQuery("stat_dt".toLowerCase()).gte(contribution.getStart_time()).lte(contribution.getEnd_time()));
        boolQuery.must(QueryBuilders.termQuery("market_id", contribution.getMarket_id()));
        boolQuery.must(QueryBuilders.termQuery("stat_dt", contribution.getMarket_id()));

        //获取昨天的日期
        boolQuery.must(QueryBuilders.termQuery("stat_dt", DateUtil.getYesterday(new Date())));
        log.info("queryinfo=" + boolQuery.toString());
        SearchResponse response = null;//clientUtil.getClient().prepareSearch("membercontribution").setTypes("membercontribution")
        // .addSort("level", SortOrder.ASC).setQuery(boolQuery).get();

        ContributionResultdto resulDto = null;
        Map<String, Object> mapVlue = new HashMap();
        List<ContributionResultdto> dataList = new ArrayList<ContributionResultdto>();
        //按日期归类，并按日期排序
        //拼凑Json格式如下
        //{{"data":[{"start_time":"sdfsaf","end_time":"23ew"},{"start_time":"sdfsaf","end_time":"23ew"}],"time":"2017-09-22"},{"data":[{"start_time":"sdfsaf","end_time":"23ew"},{"start_time":"sdfsaf","end_time":"23ew"}],"time":"2017-09-22"}}
        for (SearchHit hit : response.getHits().getHits()) {
            if (mapVlue.containsKey(hit.getSource().get("stat_dt"))) {
                resulDto = (ContributionResultdto) mapVlue.get(hit.getSource().get("stat_dt"));
            } else {
                resulDto = new ContributionResultdto();
            }

            resulDto.setTime((String) hit.getSource().get("stat_dt"));
            List<ContributionEnDto> listDto = resulDto.getData();
            if (CollectionUtils.isEmpty(listDto)) {
                listDto = new ArrayList();
            }
            //组装对象
            ContributionEnDto enDto = new ContributionEnDto();
            enDto.setAvgOffer((String) hit.getSource().get("avgoffer"));
            enDto.setLevel((String) hit.getSource().get("level"));
            listDto.add(enDto);
            resulDto.setData(listDto);
            //放入仓库
            mapVlue.put((String) hit.getSource().get("stat_dt"), resulDto);
            dataList.add(resulDto);
        }
        //回收内存
        mapVlue.clear();
        //4：校验是否有此数据
        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        //排序时间的降序
        dataList.sort((dto1, dto2) -> dto1.getTime().compareTo(dto2.getTime()));
        log.info("MemGroupOptImp-contribution  end" + dataList.size());
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult querylevel(MemMarketDto markDto) {
        log.info("MemGroupOptImp-querylevel  begin");
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.termQuery("market_id", markDto.getMarket_id()));
        log.info("querylevel-queryinfo=" + boolQuery.toString());
        SearchResponse response = null;//clientUtil.getClient().prepareSearch("memberlevel").setTypes("memberlevel")
        //.setQuery(boolQuery).get();

        Map<String, Object> valueMap = null;
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            valueMap = new HashMap<>();
            valueMap.put("level", hit.getSource().get("level"));
            valueMap.put("total", hit.getSource().get("total"));
            valueMap.put("percent", hit.getSource().get("percent"));
            dataList.add(valueMap);
        }
        //4：校验是否有此数据
        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        log.info("MemGroupOptImp-querylevel  end" + dataList.size());
        return new RestApiResult(dataList);

    }

    @Override
    public RestApiResult queryNumtrend(ContributionDto contribution) {
        log.info("MemGroupOptImp-queryNumtrend  begin");
        //拼装查询
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        boolQuery.must(QueryBuilders.termQuery("market_id", contribution.getMarket_id()));
        boolQuery.must(QueryBuilders.rangeQuery("time".toLowerCase()).gte(contribution.getStart_time()).lte(contribution.getEnd_time()));
        log.info("queryNumtrend-queryinfo=" + boolQuery.toString());
        SearchResponse response = null;//clientUtil.getClient().prepareSearch("memberrgst").setTypes("memberrgst")
        // .setQuery(boolQuery).get();

        Map<String, Object> valueMap = null;
        List<NumtrendDto> dataList = new ArrayList<NumtrendDto>();
        NumtrendDto numDto = null;
        for (SearchHit hit : response.getHits().getHits()) {
            numDto = new NumtrendDto();
            numDto.setTime((String) hit.getSource().get("time"));
            numDto.setAnnulus_growth((String) hit.getSource().get("annulus_growth"));
            numDto.setUser_growth((String) hit.getSource().get("user_growth"));
            dataList.add(numDto);
        }
        //2：校验是否有此数据
        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }
        log.info("MemGroupOptImp-queryNumtrend  end" + dataList.size());
        return new RestApiResult(dataList);
    }

    @Override
    public RestApiResult querySiteuid(List<String> list) {
        log.info("MemGroupOptImp-querySiteuid begin list=" + list.size());
        //1：会员分群按照站点查询
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();

        if (list.size() > 1) {
            boolQuery.must(QueryBuilders.termsQuery("site_uid", list.toArray()));
        } else {
            boolQuery.must(QueryBuilders.termQuery("site_uid", list.get(0)));
        }
        //2：增加聚合SUM过滤
        AggregationBuilder aggregation = AggregationBuilders
                .terms("crowd")
                .field("crowd_id").size(5000)
                .subAggregation(AggregationBuilders.sum("sum_num").field("total_num"));

        log.info("querySiteuid-boolQuery=" + boolQuery.toString());
        log.info("querySiteuid-aggregation=" + aggregation.toString());
        SearchResponse response = null;//clientUtil.getClient().prepareSearch("crowdsite").setTypes("crowdsite")
        //.setQuery(boolQuery).addAggregation(aggregation).setSize(0).get();
        //crowd_id 列表统计所有符合条件ID
        CrowdSiteidDto dto = null;
        List<Double> crowdList = new ArrayList<>();
        List<CrowdSiteidDto> dataList = new ArrayList<>();
        log.info("querySiteuid-response length=" + response.getHits().getHits().length);
        //获取统计结果
        Terms terms = response.getAggregations().get("crowd");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            dto = new CrowdSiteidDto();
            dto.setCrowd_id(bucket.getKeyAsString());
            Sum sum = (Sum) bucket.getAggregations().getAsMap().get("sum_num");
            dto.setTotal_num(Double.toString(sum.getValue()));
            crowdList.add(sum.getValue());
            dataList.add(dto);
        }

        //封装返回对象
        CrowdSiteidResultDto resultDto = new CrowdSiteidResultDto();
        resultDto.setCrowd_list(crowdList);
        resultDto.setData(dataList);

        if (CollectionUtils.isEmpty(dataList)) {
            return new RestApiResult(Constant.ERROR_NODATA, Constant.EROR_MSG_NODATA, null, dataList);
        }

        log.info("MemGroupOptImp-querySiteuid  end" + dataList.size());
        return new RestApiResult(dataList);
    }
}
