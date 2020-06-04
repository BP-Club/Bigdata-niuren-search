package com.niuren.dsapi.config;

import com.niuren.dsapi.model.Entity.TempleDbEntity;
import com.niuren.dsapi.model.dao.TempleDbEntityMapper;
import com.niuren.dsapi.model.dto.DataSourceDto;
import com.niuren.dsapi.util.DataSourceUtil;
import com.niuren.dsapi.config.Constant;
import com.niuren.dsapi.config.DataSourceHiveConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * 数据源列表集合
 */

@Component
@Order(2)
public class SysDataSource {
    @Autowired
    private com.niuren.dsapi.config.DataSourceEsConfig esConfig;

    @Autowired
    private DataSourceHiveConfig hiveDataSource;

//    @Autowired
////    private DataSourceOracleConfig oracleDataSource;

    @Autowired
    private com.niuren.dsapi.config.DataSourceKafkaConfig kafkaDataSource;

    @Autowired
    TempleDbEntityMapper templeDbEntityMapper;

    //hive dataSource
    private HashMap<String, DataSource> hiveSourceMap = new HashMap();

    //es dataSource
    private HashMap<String, String> esSourceMap = new HashMap();

    //hbase dataSource
    private HashMap<String, Object> hbaseSourceMap = new HashMap();

    //kafka dataSource
    private HashMap<String, Object> kafkaSourceMap = new HashMap();

    //oracle dataSource
    private HashMap<String, Object> oracleSourceMap = new HashMap();

    //mysql dataSource
    private HashMap<String, Object> mysqlSourceMap = new HashMap();

    public HashMap<String, Object> getMysqlSourceMap() {
        return mysqlSourceMap;
    }

    public void setMysqlSourceMap(HashMap<String, Object> mysqlSourceMap) {
        this.mysqlSourceMap = mysqlSourceMap;
    }

    public HashMap<String, Object> getOracleSourceMap() {
        return oracleSourceMap;
    }

    public void setOracleSourceMap(HashMap<String, Object> oracleSourceMap) {
        this.oracleSourceMap = oracleSourceMap;
    }

    public HashMap<String, DataSource> getHiveSourceMap() {
        return hiveSourceMap;
    }

    public HashMap<String, String> getEsSourceMap() {
        return esSourceMap;
    }

    public HashMap<String, Object> getHbaseSourceMap() {
        return hbaseSourceMap;
    }

    public HashMap<String, Object> getKafkaSourceMap() {
        return kafkaSourceMap;
    }

    /**
     * 获取 es数据源
     *
     * @param key
     * @return
     */
    public String getEsSource(String key) {
        return esSourceMap.get(key);
    }

    /**
     * 获取 hive数据源
     *
     * @param key
     * @return
     */
    public DataSource getHiveSource(String key) {
        return hiveSourceMap.get(key);
    }

    /**
     * 获取 hbase数据源
     *
     * @param key
     * @return
     */
    public Object getHbaseSource(String key) {
        return hbaseSourceMap.get(key);

    }


    /**
     * 获取 kafka数据源
     *
     * @param key
     * @return
     */
    public Object getKafkaSource(String key) {
        return kafkaSourceMap.get(key);

    }

    @PostConstruct
    public void initDBSource() {
        //1:hive数据源 集合
        DataSource hive01 = hiveDataSource.getDataSource();
        hiveSourceMap.put("hive01", hive01);

        //hive01-存放到列表页面展示查询
        DataSourceDto dataHive01 = new DataSourceDto();
        dataHive01.setDriver(hiveDataSource.getDriverClass());
        dataHive01.setUrl(hiveDataSource.getJdbcUrl());
        dataHive01.setSourceType(Constant.SourceType.HIVE.getCode().toString());
        dataHive01.setUserName(hiveDataSource.getUser());
        dataHive01.setSourceName("hive01");
        DataSourceUtil.getHiveList().add(dataHive01);

        //2: es 数据源
        String url = esConfig.getPostUrl();
        esSourceMap.put("es01", url);
        //es01-存放到列表页面展示查询
        DataSourceDto dataEs01 = new DataSourceDto();
        dataEs01.setDriver("");
        dataEs01.setUrl(esConfig.getEsUrl());
        dataEs01.setPort(esConfig.getEsPort());
        dataEs01.setSourceType(Constant.SourceType.ES.getCode().toString());
        dataEs01.setUserName("");
        dataEs01.setSourceName("es01");
        DataSourceUtil.getEsList().add(dataEs01);

        //3:Hbase 数据源

        //4: kafka数据源
        kafkaSourceMap.put("kafka01", kafkaDataSource.getKafkaUtil());
        DataSourceDto dataKafka01 = new DataSourceDto();
        dataKafka01.setDriver("");
        dataKafka01.setUrl(kafkaDataSource.getBrokenList());
        dataKafka01.setPort("");
        dataKafka01.setSourceType(Constant.SourceType.KAFKA.getCode().toString());
        dataKafka01.setUserName("");
        dataKafka01.setSourceName("kafka01");
        DataSourceUtil.getKafkaList().add(dataKafka01);

        //1:oracle数据源 集合
//        Connection oracle01 = oracleDataSource.getConn();
//        oracleSourceMap.put("oracle01", oracle01);

        //oracle-存放到列表页面展示查询
//        DataSourceDto dataOracle01 = new DataSourceDto();
//        dataOracle01.setDriver(oracleDataSource.getDriverClass());
//        dataOracle01.setUrl(oracleDataSource.getJdbcUrl());
//        dataOracle01.setSourceType(Constant.SourceType.ORACLE.getCode().toString());
//        dataOracle01.setUserName(oracleDataSource.getUserName());
//        dataOracle01.setSourceName("oracle01");
//        DataSourceUtil.getOracleList().add(dataOracle01);

        //mysql
        TempleDbEntity record = new TempleDbEntity();
        record.setType(Constant.SourceType.MYSQL.getCode().toString());
        List<TempleDbEntity> templeList = templeDbEntityMapper.selectListByType(record);
        if (!CollectionUtils.isEmpty(templeList)) {
            DataSourceDto mysqlData = null;
            for (TempleDbEntity entity : templeList) {
                mysqlData = new DataSourceDto();
                mysqlData.setDriver(entity.getDriver());
                mysqlData.setUrl(entity.getUrl());
                mysqlData.setSourceType(Constant.SourceType.MYSQL.getCode().toString());
                mysqlData.setUserName(entity.getUsername());
                mysqlData.setSourceName(entity.getName());
                mysqlSourceMap.put(entity.getName(), entity);
                DataSourceUtil.getMysqlList().add(mysqlData);
            }
        }
    }

}
