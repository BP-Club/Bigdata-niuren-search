package com.niuren.dsapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.InfluxDBConfig;
import com.niuren.dsapi.model.Entity.EyeOrderDataEntity;
import com.niuren.dsapi.model.Entity.EyeOrderPeDataEntity;
import com.niuren.dsapi.model.Entity.EyeVisitorDataEntity;
import com.niuren.dsapi.model.dao.InfluxDBDao;
import com.niuren.dsapi.model.dto.DataEyeRequestDto;
import com.niuren.dsapi.model.dto.DataEyeResultDto;
import com.niuren.dsapi.model.dto.DateEyeSiteDto;
import com.niuren.dsapi.service.DataEyeQueryApi;
import com.niuren.dsapi.util.DateUtil;
import com.niuren.dsapi.util.EsNodeClientUtil;
import com.niuren.dsapi.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class DataEyeQueryImp implements DataEyeQueryApi {

    private static final Logger log = LoggerFactory.getLogger(DataEyeQueryImp.class);
    @Autowired
    InfluxDBDao influxDBDao;
    @Autowired
    private InfluxDBConfig influxDBConfig;

    @Override
    public DsapiResult querySaleData(DataEyeRequestDto saleDto) {
        log.info("************DataEyeApiResult-querySaleData begin************" + JSON.toJSONString(saleDto));
        //执行查询
        DataEyeResultDto resultDto = new DataEyeResultDto();
        try {
            //按国内实际时间-8
            String stime = DateUtil.getYYYY_MM_DDYesterday(saleDto.getDate()) + " 16:00:00";
            String etime = saleDto.getDate() + " 17:59:59";
            log.info("DataEyeQueryImp-querySaleData--- stime=" + stime + ",etime=" + etime);
            //1：先查询 一级站点的最大时间数据
            String querySiteSql = "select max(timestamp) as timestamp,sec_site , first_site  from visitorData  where sec_site='null' ";
            //如果一级站点不为空则只统计一级站点，为空这统计所有
            if (StringUtil.isNotEmpty(saleDto.getOneSite())) {
                querySiteSql += " and first_site=" + StringUtil.getInNoDbFormat(saleDto.getOneSite());
            }
            querySiteSql += " and  time >= " + "'" + stime + "'";
            querySiteSql += " and  time <= " + "'" + etime + "'";
            querySiteSql += " and  brand= " + "'" + saleDto.getBrand() + "'";
            querySiteSql += " group by first_site";
            log.info("DataEyeQueryImp-querySaleData querySiteSql=" + querySiteSql);
            List<EyeVisitorDataEntity> resultList = influxDBDao.queryList(querySiteSql, EyeVisitorDataEntity.class, influxDBConfig.getDatabase());


            //2:根据一级站点的时间和站点ID 去查询对应的 访问数 和 客户数 存放列表，后续累加
            Map<String, DateEyeSiteDto> sitesMap = new HashMap<>();//存储所有站点的信息
            //总访问数量
            double totalVisitCount = 0d;

            if (!CollectionUtils.isEmpty(resultList)) {
                for (EyeVisitorDataEntity entity : resultList) {
                    String queryVisitSql = "select timestamp,sec_site ,first_site ,mem_num ,visitor_num";
                    queryVisitSql += " from visitorData  where  first_site =" + StringUtil.getInNoDbFormat(entity.getFirstSite());
                    queryVisitSql += " and  timestamp = " + Long.toString(entity.getTimestamps());
                    queryVisitSql += " and  brand= " + "'" + saleDto.getBrand() + "'";

                    List<EyeVisitorDataEntity> vsList = influxDBDao.queryList(queryVisitSql, EyeVisitorDataEntity.class, influxDBConfig.getDatabase());

                    if (!CollectionUtils.isEmpty(vsList)) {
                        try {
                            double visitCount = Double.valueOf(vsList.get(0).getVisitCount());
                            //   double customCount = Double.valueOf(vsList.get(0).getCustomCount());

                            totalVisitCount += visitCount;
                            // totalCustomCount += customCount;
                            //用于对象存储
                            if (StringUtil.isNotEmpty(entity.getFirstSite())) {
                                DateEyeSiteDto sitDto = new DateEyeSiteDto();
                                sitDto.setSiteCode(entity.getFirstSite());
                                sitDto.setVisitCount(DateUtil.getRound(visitCount));
                                //     sitDto.setCustomCount(DateUtil.getRound(customCount));
                                sitesMap.put(entity.getFirstSite(), sitDto);
                            }
                        } catch (Exception e) {
                            log.error("count convert error=", e);
                        }
                    }
                }
                resultList.clear();
            }
            resultDto.setVisitCount(DateUtil.getZRound(totalVisitCount));


            //log.info("step1-A==" + JSON.toJSONString(sitesMap));

            //2B:*********************需求变更客户数 取orderPeopleNum表中*********************
            //1：先查询 一级站点的最大时间数据
            String queryCoustomSql = "select max(timestamp) as timestamp,sec_site , first_site  from orderPeopleNum  where  ";
            //如果一级站点不为空则只统计一级站点，为空这统计所有
            queryCoustomSql += "   brand= " + "'" + saleDto.getBrand() + "'";

            if (StringUtil.isNotEmpty(saleDto.getOneSite())) {
                queryCoustomSql += " and first_site=" + StringUtil.getInNoDbFormat(saleDto.getOneSite());
            }
            queryCoustomSql += " and  time >= " + "'" + stime + "'";
            queryCoustomSql += " and  time <= " + "'" + etime + "'";
            queryCoustomSql += " group by first_site";
            //用于记录一级站点对应的客户数
            log.info("step1-B==queryCoustomSql=" + queryCoustomSql);
            //总客户数
            double totalCustomCount = 0d;
            List<EyeOrderPeDataEntity> queryCoustomList = influxDBDao.queryList(queryCoustomSql, EyeOrderPeDataEntity.class, influxDBConfig.getDatabase());


            if (!CollectionUtils.isEmpty(queryCoustomList)) {
                for (EyeOrderPeDataEntity entity : queryCoustomList) {
                    String queryVisitSql = "select timestamp,sec_site ,first_site ,order_home_num  ";
                    queryVisitSql += " from orderPeopleNum  where  first_site =" + StringUtil.getInNoDbFormat(entity.getFirstSite());
                    queryVisitSql += " and  timestamp = " + Long.toString(entity.getTimestamps());
                    queryVisitSql += " and  brand= " + "'" + saleDto.getBrand() + "'";

                    List<EyeOrderPeDataEntity> subCoustomList = influxDBDao.queryList(queryVisitSql, EyeOrderPeDataEntity.class, influxDBConfig.getDatabase());
                    if (sitesMap.containsKey(entity.getFirstSite())) {
                        DateEyeSiteDto siteDto = sitesMap.get(entity.getFirstSite());
                        if (siteDto != null) {
                            //存放数据
                            if (!CollectionUtils.isEmpty(subCoustomList) && StringUtil.isNotEmpty(subCoustomList.get(0))) {
                                double customCount = Double.parseDouble(subCoustomList.get(0).getOrderHomeNum());
                                totalCustomCount += customCount;
                                siteDto.setCustomCount(subCoustomList.get(0).getOrderHomeNum());
                            }
                        }
                    }
                }
            }
            resultDto.setCustomCount(DateUtil.getZRound(totalCustomCount));

            //3:**************************** 根据条件统计所有数据到集合中 全部查询或者按照一级站点查询****************************************
            String queryOrderSql = "select  timestamp,sec_site ,first_site ,sales_num,order_num   from orderData  where";
            queryOrderSql += "  time >= " + "'" + stime + "'";
            queryOrderSql += " and  time <= " + "'" + etime + "'";
            //如果一级站点不为空则只统计一级站点，为空这统计所有
            if (StringUtil.isNotEmpty(saleDto.getOneSite())) {
                queryOrderSql += " and first_site=" + StringUtil.getInNoDbFormat(saleDto.getOneSite());
            }
            queryOrderSql += " and  brand= " + "'" + saleDto.getBrand() + "'";
            queryOrderSql += " group by first_site";


            //4：如果是全部查询：需要按照按站点累加销售额:此处是查询的二级站点额度，一级站点为所属二级站点之和
            List<EyeOrderDataEntity> orderList = influxDBDao.queryList(queryOrderSql, EyeOrderDataEntity.class, influxDBConfig.getDatabase());
            //订单数量
            double totalOrderNum = 0d;
            //销售额户数
            double totalSalesNum = 0d;
            if (!CollectionUtils.isEmpty(orderList)) {
                for (EyeOrderDataEntity orderEntity : orderList) {
                    try {
                        double orderNum = Double.valueOf(orderEntity.getOrderNum());
                        double saleNum = Double.valueOf(orderEntity.getSalesNum());
                        totalSalesNum += saleNum;
                        totalOrderNum += orderNum;
                    } catch (Exception e) {
                        log.error("count  convert error=", e);
                    }

                }
            }
            resultDto.setOrderCount(DateUtil.getZRound(totalOrderNum));
            resultDto.setSale(DateUtil.getZRound(totalSalesNum));

            //5:****************************如果全部查询，需要统计销售额排行列表*******************************************
            if (StringUtil.isNotEmpty(saleDto.getOneSite())) {
                resultDto.setSiteCode(saleDto.getOneSite());
            } else {//查询站点的销售额列表->并降序排列
                if (!CollectionUtils.isEmpty(orderList)) {
                    for (EyeOrderDataEntity orderEntity : orderList) {
                        try {
                            //获取销售额度
                            double saleNum = Double.valueOf(orderEntity.getSalesNum());
                            double orderNum = Double.valueOf(orderEntity.getOrderNum());
                            if (StringUtil.isNotEmpty(orderEntity.getFirstSite())) {
                                if (sitesMap.containsKey(orderEntity.getFirstSite())) {
                                    DateEyeSiteDto eyeDto = sitesMap.get(orderEntity.getFirstSite());
                                    if (eyeDto == null) {
                                        eyeDto = new DateEyeSiteDto();
                                    }
                                    if (StringUtil.isEmpty(eyeDto.getSale())) {
                                        eyeDto.setSale("0.00");
                                    }
                                    if (StringUtil.isEmpty(eyeDto.getOrderCount())) {
                                        eyeDto.setOrderCount("0.00");
                                    }
                                    //默认值
                                    if (StringUtil.isEmpty(eyeDto.getVisitCount())) {
                                        eyeDto.setVisitCount("0.00");
                                    }
                                    if (StringUtil.isEmpty(eyeDto.getSiteCode())) {
                                        eyeDto.setSiteCode(orderEntity.getFirstSite());
                                    }
                                    eyeDto.setSale(Double.toString(Double.parseDouble(eyeDto.getSale()) + saleNum));
                                    eyeDto.setOrderCount(Double.toString(Double.parseDouble(eyeDto.getOrderCount()) + orderNum));
                                    sitesMap.put(orderEntity.getFirstSite(), eyeDto);//如果存在则累加销售额
                                } else {
                                    DateEyeSiteDto eyeDto = new DateEyeSiteDto();
                                    eyeDto.setSale(Double.toString(saleNum));
                                    eyeDto.setOrderCount(Double.toString(orderNum));
                                    sitesMap.put(orderEntity.getFirstSite(), eyeDto);
                                }

                            }

                        } catch (Exception e) {
                            log.error("count error=", e);
                        }

                    }
                    orderList.clear();//清空内存
                }
                // log.info("step2=" + JSON.toJSONString(sitesMap));
                //map转换成list并排序
                List<DateEyeSiteDto> saleList = new ArrayList<>();

                if (sitesMap.size() > 0) {
                    Map<String, JSONObject> sitesMap2 = new HashMap<>();
                    sitesMap2 = JSON.parseObject(JSON.toJSONString(sitesMap), sitesMap2.getClass());
                    for (Map.Entry<String, JSONObject> entry : sitesMap2.entrySet()) {
                        if (entry.getValue() != null) {
                            JSONObject obj = entry.getValue();
                            DateEyeSiteDto sitDto = obj.toJavaObject(DateEyeSiteDto.class);
                            sitDto.setSale(DateUtil.getRound(Double.parseDouble(sitDto.getSale() == null ? "0.00" : sitDto.getSale())));
                            sitDto.setCustomCount(DateUtil.getRound(Double.parseDouble(sitDto.getCustomCount() == null ? "0.00" : sitDto.getCustomCount())));
                            saleList.add(sitDto);
                        }
                    }

                    //降序排列
                    saleList.sort((dto1, dto2) -> new Double(dto2.getSale()).compareTo(new Double(dto1.getSale())));
                }
                resultDto.setSaleList(saleList);
            }
            //6:*********************计算客单价和转化率-暂不处理*********************

        } catch (Exception e) {
            log.error("************DataEyeApiResult-querySaleData  error************ ", e);
            return new DsapiResult(Constant.ERROR_OTHER, e.getMessage());
        } finally {
            log.info("************DataEyeApiResult-querySaleData end  ");
        }
        return new DsapiResult(resultDto);
    }
}
