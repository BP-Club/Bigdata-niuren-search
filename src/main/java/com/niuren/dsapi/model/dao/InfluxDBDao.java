package com.niuren.dsapi.model.dao;

import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.InfluxDBConfig;
import com.niuren.dsapi.util.StringUtil;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class InfluxDBDao<T> {
    @Autowired
    private InfluxDBConfig influxDBConfig;

    private InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

    /**
     * 插入数据
     */

    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields, long time) throws Exception {
        Point.Builder builder = Point.measurement(measurement).tag(tags);
        builder.fields(fields);
        if (time > 0) {
            builder.time(time, TimeUnit.MILLISECONDS);
        }
        influxDBConfig.getInfluxDB().write(influxDBConfig.getDatabase(), influxDBConfig.getPolicy(), builder.build());
    }

    /**
     * 查询数据并返回列表
     *
     * @param querySql
     * @return
     * @throws Exception
     */
    public List<Map> queryList(String querySql, String dataBase) throws Exception {
        if (StringUtil.isEmpty(dataBase)) {
            dataBase = influxDBConfig.getDatabase();
        }
        Query request = new Query(querySql, dataBase);
        QueryResult result = influxDBConfig.getInfluxDB().query(request);
        if (result.hasError()) {
            throw new Exception(result.getError());
        }
        List<Map> resultList = new ArrayList<>();
        List<QueryResult.Result> resList = result.getResults();
        if (CollectionUtils.isEmpty(resList)) {
            return new ArrayList<>();
        }
        for (QueryResult.Result res : resList) {
            List<QueryResult.Series> serList = res.getSeries();
            if (CollectionUtils.isEmpty(serList)) {
                continue;
            }
            for (QueryResult.Series series : serList) {
                if (series == null) {
                    continue;
                }
                Map<String, String> map = series.getTags();
                String name = series.getName();//表名称
                List<String> listColumns = series.getColumns();//字段名称
                List<List<Object>> listValues = series.getValues();//值名称
                if (!CollectionUtils.isEmpty(listColumns) && !CollectionUtils.isEmpty(listValues)) {
                    Map<String, Object> data = null;
                    for (List<Object> listObject : listValues) {
                        int i = 0;
                        data = new HashMap();
                        for (String column : listColumns) {
                            data.put(column, listObject.get(i));
                            i++;
                        }
                        if (resultList.size() >= Constant.MAX_SIZE) {
                            throw new Exception("out of max size=" + Constant.MAX_SIZE);
                        }
                        resultList.add(data);
                    }
                }


            }
        }
        return resultList;
    }


    /**
     * 查询数据并返回列表
     *
     * @param query
     * @param clazz
     * @return
     * @throws Exception
     */
    public List<T> queryList(String query, Class<T> clazz, String dataBase) throws Exception {
        if (StringUtil.isEmpty(dataBase)) {
            dataBase = influxDBConfig.getDatabase();
        }
        Query request = new Query(query, dataBase);
        QueryResult result = influxDBConfig.getInfluxDB().query(request);
        if (result.hasError()) {
            throw new Exception(result.getError());
        } else {
            return resultMapper.toPOJO(result, clazz);
        }
    }


    public <E> List<E> queryTag(String query, Class<E> clazz) throws Exception {
        Query request = new Query(query, influxDBConfig.getDatabase());
        QueryResult result = influxDBConfig.getInfluxDB().query(request);
        if (result.hasError()) {
            throw new Exception(result.getError());
        } else {
            return resultMapper.toPOJO(result, clazz);
        }
    }

    ;
}
