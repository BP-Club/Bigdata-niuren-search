package com.niuren.dsapi.util;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLFeatureNotSupportedException;


public class EsNodeClientUtil {

    private static final Logger log = LoggerFactory.getLogger(EsNodeClientUtil.class);
    /**
     * 2:Client 节点创建
     */
    static volatile Client client = null;

    //无节点方式连接
    public static SearchDao getClient() {
        if (client == null) {
            Settings settings = Settings.builder()
                    .put("node.data", false)
                    .put("node.client", true)
                    .build();
            ThreadPool threadPool = new ThreadPool(settings);
            client = new NodeClient(settings, threadPool);
        }
        return new SearchDao(client);
    }

    /**
     * 2:Client 获取数据连接
     *
     * @return
     */
    public static SearchDao getTranClient(String host, String port) throws UnknownHostException {
        if (client == null) {
            Settings settings = Settings.builder().put("client.transport.ignore_cluster_name", true).build();
            client = new PreBuiltTransportClient(settings).addTransportAddress(getTransportAddress(host, port));
        }
        return new SearchDao(client);
    }


    protected static InetSocketTransportAddress getTransportAddress(String host, String port) throws UnknownHostException {
        if (StringUtil.isEmpty(port)) {
            port = "9300";
        }
        if (StringUtil.isEmpty(host)) {
            host = "10.100.19.144";
        }
        log.info(String.format("Connection details: host: %s. port:%s.", host, port));
        return new InetSocketTransportAddress(InetAddress.getByName(host), Integer.parseInt(port));
    }

    /**
     * 查询数据
     *
     * @param query
     * @param host
     * @param port
     * @return
     * @throws SqlParseException
     * @throws UnknownHostException
     * @throws SQLFeatureNotSupportedException
     * @throws SQLFeatureNotSupportedException
     */
    public static SearchHits query(String query, String host, String port) throws SqlParseException, UnknownHostException,
            SQLFeatureNotSupportedException, SQLFeatureNotSupportedException {
        SearchDao searchDao = getTranClient(host, port);
        SqlElasticSearchRequestBuilder select = (SqlElasticSearchRequestBuilder) searchDao.explain(query).explain();
        return ((SearchResponse) select.get()).getHits();
    }

    @SuppressWarnings("unused")
    public static SqlElasticSearchRequestBuilder getRequestBuilder(String query, String host, String port) throws SqlParseException,
            SQLFeatureNotSupportedException, UnknownHostException, SQLFeatureNotSupportedException {
        SearchDao searchDao = getTranClient(host, port);
        return (SqlElasticSearchRequestBuilder) searchDao.explain(query).explain();
    }

    @SuppressWarnings("unused")
    public static SearchResponse getSearchResponse(String query, String host, String port) throws SqlParseException,
            SQLFeatureNotSupportedException, UnknownHostException, SQLFeatureNotSupportedException {
        SearchDao searchDao = getTranClient(host, port);
        SqlElasticSearchRequestBuilder select = (SqlElasticSearchRequestBuilder) searchDao.explain(query).explain();
        return ((SearchResponse) select.get());
    }
}
