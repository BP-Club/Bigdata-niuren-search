package com.niuren.dsapi.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;

/**
* @author dailinwei
* @description 工具类，发送 HTTP 请求
* @param:
* @return
* @date 2018/4/23 15:33
*/

public class HttpClientUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT;

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(20);
        connectionManager.setDefaultMaxPerRoute(5);

        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase
                        ("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 60 * 1000;//如果没有约定，则默认定义时长为60s
        };

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10 * 1000).setConnectTimeout(10 * 1000).build();

        HTTP_CLIENT = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setDefaultRequestConfig(requestConfig)
                .build();

        new HttpClientMonitorThread(connectionManager).start();
    }

	public static <T> T get(String url, ResponseHandler<T> responseHandler) throws IOException {
        return get(url, null, null, responseHandler);
    }

    public static <T> T get(String url, Map<String, String> headerMap, Map<String, Object> paramMap, ResponseHandler<T> responseHandler) throws IOException {
		if (paramMap != null) {
			url += HttpClientUtil.convertParameterMapToString(paramMap);
		}
		HttpGet httpGet = new HttpGet(url);
        if (headerMap != null) {
            headerMap.forEach(httpGet::addHeader);
        }
        return responseHandler.handleResponse(HTTP_CLIENT.execute(httpGet));
    }

    public static <T> T post(String url, Map<String, String> headerMap, Map<String, Object> paramMap, ResponseHandler<T> responseHandler) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (headerMap != null) {
            headerMap.forEach(httpPost::addHeader);
        }
        StringEntity entity = new StringEntity(OBJECT_MAPPER.writeValueAsString(paramMap), "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json;charset=UTF-8");
        httpPost.setEntity(entity);
        return responseHandler.handleResponse(HTTP_CLIENT.execute(httpPost));
    }

	/**
	 * map键值对 ——>> key=value&key=value
	 */
	private static String convertParameterMapToString(Map<String, Object> parameterMap) {
		StringBuilder sb = new StringBuilder();
		if (parameterMap != null) {
            sb.append("?");
			Iterator<String> iterator = parameterMap.keySet().iterator();
			String key;
			String value;
			while (iterator.hasNext()) {
				key = iterator.next();
				if (parameterMap.get(key) != null) {
					value = parameterMap.get(key).toString();
				} else {
					value = "";
				}
                sb.append(key).append("=").append(value);
				if (iterator.hasNext()) {
                    sb.append("&");
				}
			}
		}
		return sb.toString();
	}

    public static class HttpClientMonitorThread extends Thread {

        private final HttpClientConnectionManager connectionManager;
        private volatile boolean shutdown;

        HttpClientMonitorThread(HttpClientConnectionManager connectionManager) {
            super();
            this.connectionManager = connectionManager;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connectionManager.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
                shutdown();
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

}