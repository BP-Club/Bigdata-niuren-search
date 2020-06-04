package com.niuren.dsapi.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;


@Component
@Order(1)
public class DataSourceHiveConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceHiveConfig.class);

    /**
     * Hive数据源
     */
    private DataSource dataSource;

    /**
     * 连接池
     */
    private ConnectionPoolDataSource dataSourcePool;

    @Value("${custom.datasource.hive.user}")
    private String user;

    private String password;

    @Value("${custom.datasource.hive.jdbcUrl}")
    private String jdbcUrl;

    @Value("${custom.datasource.hive.driverClass}")
    private String driverClass;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ConnectionPoolDataSource getDataSourcePool() {
        return dataSourcePool;
    }

    public void setDataSourcePool(ConnectionPoolDataSource dataSourcePool) {
        this.dataSourcePool = dataSourcePool;
    }

    @PostConstruct
    public void primaryDataSource() {
        try {
            log.info("******DataSourceHiveConfig-connection loading******");
            this.dataSource = DataSourceBuilder.create()
                    .driverClassName(driverClass)
                    .url(jdbcUrl)
                    .username(user)
                    .type(ComboPooledDataSource.class).build();
//            //获取连接池
//            ComboPooledDataSource dataSourcePool = DataSourceBuilder.create()
//                    .driverClassName(driverClass)
//                    .url(jdbcUrl)
//                    .username(user)
//                    .type(ComboPooledDataSource.class).build();
//            dataSourcePool.setInitialPoolSize(15);//初始化15个
//            dataSourcePool.setMinPoolSize(10);//最小保持10个连接
//            dataSourcePool.setMaxIdleTime(60 * 15);//秒单位 连接的最大空闲时间，如果超过这个时间，某个数据库连接还没有被使用则会断开掉这个连接。如果为0，则永远不会断开连接,即回收此连接
//            dataSourcePool.setMaxIdleTimeExcessConnections(60 * 10);//秒单位 快速减轻连接池的负载,数据库连接数很少，需要快速释放
//            dataSourcePool.getConnectionPoolDataSource();
//            log.info("hive connection pool info=" + dataSourcePool.getConnectionPoolDataSource().getPooledConnection().getConnection().isClosed());
        } catch (Exception e) {
            log.error("get connect error info=", e);
        }
    }

}