package com.niuren.dsapi.filter;

import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@WebFilter
@Slf4j
public class PrometheusFilter implements Filter {

    private ThreadLocal<String> clientIpLocal = new ThreadLocal<>();
    private ThreadLocal<Long> timeMillisLocal = new ThreadLocal<>();
    private ThreadLocal<List<Timer>> currentTimerListLocal = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public String getIp() {
        return clientIpLocal.get();
    }

    public Long getTimeMillis() {
        return timeMillisLocal.get();
    }

    public void addCurrentTimer(Timer timer) {
        if (currentTimerListLocal.get() == null) {
            currentTimerListLocal.set(new LinkedList<>());
        }
        currentTimerListLocal.get().add(timer);
    }

    public static String getIp(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 有多个ip，取第一个ip
            if (ip != null && ip.length() > 15) { // "***.***.***.***".length()
                if (ip.indexOf(",") > 0) {
                    ip = ip.substring(0, ip.indexOf(","));
                }
            }
        } catch (Exception e) {
            log.warn("error when find request ip", e);
            ip = "unknown";
        }

        return ip == null ? "unknown" : ip;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 设置请求开始时间
            timeMillisLocal.set(System.currentTimeMillis());
            currentTimerListLocal.set(null);

            // Thread.sleep(Math.abs(new Random().nextLong() % 1000));

            HttpServletRequest request = (HttpServletRequest) req;

            // 设置ip
            clientIpLocal.set(getIp(request));

            // 正常请求
            chain.doFilter(request, response);

            // 加入timer指标
            if (currentTimerListLocal.get() != null) {
                // 对于每个timer加入指标
                for (Timer timer : currentTimerListLocal.get()) {
                    timer.record(
                            System.currentTimeMillis() - timeMillisLocal.get(),
                            TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            log.error("filter error", e);
        }
    }

    @Override
    public void destroy() {

    }
}
