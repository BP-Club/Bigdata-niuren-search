package com.niuren.dsapi.service.prometheus;

import com.niuren.dsapi.filter.PrometheusFilter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PrometheusService {

    // 对于 百分之百的数据做 summary
    public final double TIMER_PERCENTILES = 1.0;
    // 每 50 条数据做统计
    public final int TIMER_STATISTICS_BUFFER_LENGTH = 50;
    // 统计 5 分钟之内的数据
    public final int TIMER_STATISTICS_EXPIRY_MINUTES = 5;

    @Autowired
    private PrometheusFilter prometheusFilter;

    private Map<String, Timer> sendKafkaDataTimerIpMap = new ConcurrentHashMap<>();
    private Map<String, Timer> sendKafkaDataTimerMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryRealTimeDataTimerIpMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryRealTimeDataTimerMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryHiveSqlTimerIpMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryHiveSqlTimerMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryOracleSqlTimerIpMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryOracleSqlTimerMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryMysqlSqlTimerIpMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryMysqlSqlTimerMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryEsSqlTimerIpMap = new ConcurrentHashMap<>();
    private Map<String, Timer> queryEsSqlTimerMap = new ConcurrentHashMap<>();

    @Synchronized
    public void incrementSendKafkaData(String templateCode, String creator) {
        try {
            log.info("templateCode=" + templateCode, "creator=" + creator);
            String ip = prometheusFilter.getIp();
            String key = templateCode + "," + creator + "," + ip;
            Timer timer = sendKafkaDataTimerIpMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.send_kafka_data_with_ip")
                        .tag("template_code", templateCode)
                        .tag("ip", ip)
                        .tag("creator", creator)
                        .description("调用接口 /api/data/sendKafKaData 的次数和响应时长（包含ip）")
                        .register(Metrics.globalRegistry);
                sendKafkaDataTimerIpMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);

            key = templateCode + "," + creator;
            timer = sendKafkaDataTimerMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.send_kafka_data")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .description("调用接口 /api/data/sendKafKaData 的次数和响应时长（不包含ip）")
                        .register(Metrics.globalRegistry);
                sendKafkaDataTimerMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);
        } catch (Exception e) {
            log.warn("incrementSendKafkaData fail", e);
        }
    }

    @Synchronized
    public void incrementQueryRealTimeData(String templateCode, String creator) {
        try {
            log.info("templateCode=" + templateCode, "creator=" + creator);
            String ip = prometheusFilter.getIp();
            String key = templateCode + "," + creator + "," + ip;
            Timer timer = queryRealTimeDataTimerIpMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_real_time_data_with_ip")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .tag("ip", ip)
                        .description("调用接口 /api/data/queryRealTimeData 的次数和相应时长 （包含ip）")
                        .register(Metrics.globalRegistry);
                queryRealTimeDataTimerIpMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);

            key = templateCode + "," + creator;
            timer = queryRealTimeDataTimerMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_real_time_data")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .description("调用接口 /aip/data/queryRealTimeData 的次数和响应时长 （不包含ip）")
                        .register(Metrics.globalRegistry);
                queryRealTimeDataTimerMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);
        } catch (Exception e) {
            log.warn("incrementQueryRealTimeData fail", e);
        }
    }

    @Synchronized
    public void incrementQueryHiveSql(String templateCode, String creator) {
        try {
            log.info("templateCode=" + templateCode, "creator=" + creator);
            String ip = prometheusFilter.getIp();
            String key = templateCode + "," + creator + "," + ip;
            Timer timer = queryHiveSqlTimerIpMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_hive_sql_with_ip")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .tag("ip", ip)
                        .description("调用接口 /api/data/queryHiveSql 的次数和响应时长 （包含ip）")
                        .register(Metrics.globalRegistry);
                queryHiveSqlTimerIpMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);

            key = templateCode + "," + creator;
            timer = queryHiveSqlTimerMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_hive_sql")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .description("调用接口 /aip/data/queryHiveSql 的次数和响应时长 （不包含ip）")
                        .register(Metrics.globalRegistry);
                queryHiveSqlTimerMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);
        } catch (Exception e) {
            log.warn("incrementQueryHiveSql fail", e);
        }
    }

    @Synchronized
    public void incrementQueryOracleSql(String templateCode, String creator) {
        try {
            log.info("templateCode=" + templateCode, "creator=" + creator);
            String ip = prometheusFilter.getIp();
            String key = templateCode + "," + creator + "," + ip;
            Timer timer = queryOracleSqlTimerIpMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_oracle_sql_with_ip")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .tag("ip", ip)
                        .description("调用接口 /api/data/queryOracleSql 的次数和响应时长 （包含ip）")
                        .register(Metrics.globalRegistry);
                queryOracleSqlTimerIpMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);

            key = templateCode + "," + creator;
            timer = queryOracleSqlTimerMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_oracle_sql")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .description("调用接口 /aip/data/queryOracleSql 的次数和响应时长 （不包含ip）")
                        .register(Metrics.globalRegistry);
                queryOracleSqlTimerMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);
        } catch (Exception e) {
            log.warn("incrementQueryOracleSql fail", e);
        }
    }

    @Synchronized
    public void incrementQueryMysqlSql(String templateCode, String creator) {
        try {
            log.info("templateCode=" + templateCode, "creator=" + creator);
            String ip = prometheusFilter.getIp();
            String key = templateCode + "," + creator + "," + ip;
            Timer timer = queryMysqlSqlTimerIpMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_mysql_sql_with_ip")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .tag("ip", ip)
                        .description("调用接口 /api/data/queryMysqlSql 的次数和响应时间 （包含ip）")
                        .register(Metrics.globalRegistry);
                queryMysqlSqlTimerIpMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);

            key = templateCode + "," + creator;
            timer = queryMysqlSqlTimerMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_mysql_sql")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .description("调用接口 /api/data/queryMysqlSql 的次数和响应时间 （不包含ip）")
                        .register(Metrics.globalRegistry);
                queryMysqlSqlTimerMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);
        } catch (Exception e) {
            log.warn("incrementQueryMysqlSql fail", e);
        }
    }

    @Synchronized
    public void incrementQueryEsSql(String templateCode, String creator) {
        try {
            log.info("templateCode=" + templateCode, "creator=" + creator);
            String ip = prometheusFilter.getIp();
            String key = templateCode + "," + creator + "," + ip;
            Timer timer = queryEsSqlTimerIpMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_es_sql_with_ip")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .tag("ip", ip)
                        .description("调用接口 /api/data/queryEsSql 的次数和响应时间 （包含ip）")
                        .register(Metrics.globalRegistry);
                queryEsSqlTimerIpMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);

            key = templateCode + "," + creator;
            timer = queryEsSqlTimerMap.get(key);
            if (timer == null) {
                timer = Timer.builder("dsapi.query_es_sql")
                        .tag("template_code", templateCode)
                        .tag("creator", creator)
                        .description("调用接口 /aip/data/queryEsSql 的次数和响应时间 （不包含ip）")
                        .register(Metrics.globalRegistry);
                queryEsSqlTimerMap.put(key, timer);
            }
            prometheusFilter.addCurrentTimer(timer);
        } catch (Exception e) {
            log.warn("incrementQueryEsSql fail", e);
        }
    }
}
