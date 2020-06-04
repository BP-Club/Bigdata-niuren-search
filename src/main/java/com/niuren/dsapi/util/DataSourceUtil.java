package com.niuren.dsapi.util;

import com.niuren.dsapi.model.dto.DataSourceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源Util
 */
public class DataSourceUtil {
    private static List<DataSourceDto> hiveList = new ArrayList<>();

    private static List<DataSourceDto> esList = new ArrayList<>();

    private static List<DataSourceDto> hbaseList = new ArrayList<>();

    private static List<DataSourceDto> kafkaList = new ArrayList<>();

    private static List<DataSourceDto> oracleList = new ArrayList<>();

    private static List<DataSourceDto> mysqlList = new ArrayList<>();


    public static List<DataSourceDto> getOracleList() {
        return oracleList;
    }

    public static void setOracleList(List<DataSourceDto> oracleList) {
        DataSourceUtil.oracleList = oracleList;
    }

    public static List<DataSourceDto> getKafkaList() {
        return kafkaList;
    }

    public static void setKafkaList(List<DataSourceDto> kafkaList) {
        DataSourceUtil.kafkaList = kafkaList;
    }

    public static List<DataSourceDto> getHiveList() {
        return hiveList;
    }

    public static void setHiveList(List<DataSourceDto> hiveList) {
        DataSourceUtil.hiveList = hiveList;
    }

    public static List<DataSourceDto> getEsList() {
        return esList;
    }

    public static void setEsList(List<DataSourceDto> esList) {
        DataSourceUtil.esList = esList;
    }

    public static List<DataSourceDto> getHbaseList() {
        return hbaseList;
    }

    public static void setHbaseList(List<DataSourceDto> hbaseList) {
        DataSourceUtil.hbaseList = hbaseList;
    }

    public static List<DataSourceDto> getMysqlList() {
        return mysqlList;
    }

    public static void setMysqlList(List<DataSourceDto> mysqlList) {
        DataSourceUtil.mysqlList = mysqlList;
    }
}
