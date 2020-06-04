package com.niuren.dsapi.config;

import java.io.File;

/**
 * @author: dailinwei
 * @description:
 * @create: 2018-07-17 11:49
 **/
public class Constant {

    public static final int NOT_LOGIN = -100;
    //未收到参数
    public static final int ERROR_NOFIND = 404;
    //条件不合法
    public static final int ERROR_NORULE = 403;
    //该条件下无数据
    public static final int ERROR_NODATA = 405;
    //其他错误
    public static final int ERROR_OTHER = 500;
    //执行成功
    public static final int SUCCESS_CODE = 200;
    //条件不合法
    public static final String EROR_MSG_NORULE = "No current data";
    //未收到参数
    public static final String EROR_MSG_NOFIND = "Value not found!";
    //该条件下无数据
    public static final String EROR_MSG_NODATA = "No such data!";

    public static final String ESCLINET_PORT = "80";

    public static final String ESCLINET_URL = "vpc-bi-member-test-nu4agiqjo6gy5i7n2givg4g4by.us-west-2.es.amazonaws.com";

    public static final String AWS_URL = "http://" + Constant.ESCLINET_URL + ":" + Constant.ESCLINET_PORT + "/";

    public static final String SUCCESS_MSG = "SUCCESS";
    // 最大允许查询数量
    public static final int MAX_SIZE = 150000;
    // 字节单位 100MB
    public static final int AWS_MAXUPLOAD_SIZE = 1024 * 1024 * 100;

    // 实时任务
    public static final String REALTIME_TASK = "REALTIME_TASK_";

    /**
     * 数据源枚举
     */
    public enum SourceType {
        //TIMETASK 实时计算任务
        HIVE("HIVE"), ES("ES"), HBASE("HBASE"), KAFKA("KAFKA"), INFLUXDB("INFLUXDB"), ORACLE("ORACLE"), MYSQL("MYSQL"), REALTIME("REALTIME");
        private String code;
        private String name;

        private SourceType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
