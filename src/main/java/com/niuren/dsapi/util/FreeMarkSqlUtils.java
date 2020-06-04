package com.niuren.dsapi.util;

import com.niuren.dsapi.exception.ServiceException;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * freemark解析类
 */
public class FreeMarkSqlUtils {


    /**
     * 执行SQL 解析
     *
     * @param queryString
     * @return
     * @throws Exception
     */
    public static String getQueryString(String queryString, Object params) {
        String sql = "";
        Configuration freeMarkerConfig = new Configuration();
        try {
            //若内存无此命名SQL，动态读取并写入内存
            StringTemplateLoader loader = new StringTemplateLoader();
            String sqlName = Long.toString(System.currentTimeMillis());
            loader.putTemplate(sqlName, queryString);
            freeMarkerConfig.setTemplateLoader(loader);

            StringWriter stringWriter = new StringWriter();
            Template template = freeMarkerConfig.getTemplate(sqlName);
            template.process(params, stringWriter);
            sql = stringWriter.toString();
            stringWriter.flush();
            stringWriter.close();

            freeMarkerConfig.clearTemplateCache();
         } catch (Exception e) {
            throw new ServiceException("freemark process fail =" + e.getMessage());
        } finally {
            System.out.println("freeMarker-finish =" + sql);
        }

        return sql;
    }

    //执行
    public static void main(String args[]) {
        String sql = " select u.* from user u where u.user_name = :userName\n" +
                "            <#if userName??> \n" +
                "                or 1 = 1\n" +
                "            </#if>";
        //  sql = "esadfdasf";
        String sqlResult = getQueryString(sql, new HashMap<>());

        System.out.println(sqlResult);
    }


}
