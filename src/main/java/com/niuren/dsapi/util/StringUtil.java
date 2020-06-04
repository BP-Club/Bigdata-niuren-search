package com.niuren.dsapi.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.niuren.dsapi.exception.ServiceException;
import com.niuren.dsapi.model.Entity.User;
import com.niuren.dsapi.model.dto.OAuthAccessToken;
import com.niuren.dsapi.model.dto.SqlTempleRequestDto;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static boolean isEmpty(Object str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(Object str) {
        return !StringUtils.isEmpty(str);
    }

    public static String checkNull(String str) {
        if ("null".equals(str) || "NULL".equals(str)) {
            str = null;
        }
        return str;
    }

    /**
     * 获取格式
     *
     * @param str
     * @return
     */
    public static String getInNoDbFormat(String str) {

        return "'" + str + "'";
    }

    /**
     * es 获取表名称
     *
     * @param sql
     * @return
     */
    public static String getIndex(String sql) {
        String table = "";
        try {
            String array[] = sql.split(" ");
            if (array.length > 0) {
                for (int i = 0; i < array.length; i++) {
                    if (StringUtil.isNotEmpty(array[i]) && "from".equals(array[i].toLowerCase())) {
                        table = array[i + 1];
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException("get index fail please check you sql !");
        }
        return table;
    }

    /**
     * 获取用户名称
     *
     * @param session
     * @return
     */
    public static String getUserName(HttpSession session) {
        String userName = "";
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                userName = user.getName();
            }
        }
        return userName;
    }

    //执行
    public static void main(String args[]) {

        String json = "{\n" +
                "  \"paramsMap\":{\n" +
                "    \"td_date\": [\"static_month1\",\"static_month2\",\"static_month3\"],         \n" +
                "    \"stime\": \"'2018-01-01','65656565','5675655'\",\n" +
                "    \"etime\": \"2018-10-01\",\n" +
                "    \"supplier_id\":\"7982\"\n" +
                "  },\n" +
                "  \"templeCode\": \"10001\",\n" +
                "  \"type\": \"HIVE\" \n" +
                "}";

        String json2="{\n" +
                "   \"templeCode\": \"10133\",\n" +
                "   \"paramsMap\": {\n" +
                "   },\n" +
                "   \"type\": \"HIVE\"\n" +
                "}";

        SqlTempleRequestDto requestDto = JSON.parseObject(json2, SqlTempleRequestDto.class);
        String sql = "select * from abc where a in({td_date}) and b in(:td_date) and b=:stime";
        String newSql = "";

        Map<String, Object> params = requestDto.getParamsMap();

        //增加freemakeer解析
        sql = FreeMarkSqlUtils.getQueryString(sql, params);

        if (params != null && StringUtil.isNotEmpty(sql)) {
            //对:参数执行解析
            newSql = sql;
            Pattern pattern = Pattern.compile("\\:(\\w+)");
            Matcher matcher = pattern.matcher(sql);
            //:参数替换
            while (matcher.find()) {
                //对值变量替换 :value
                String groupParam = matcher.group(0);
                String groupName = matcher.group(1);
                if (StringUtil.isNotEmpty(groupName) && params.containsKey(groupName)) {
                    //默认字符类型增加引号查询
                    if (params.get(groupName) instanceof List) {
                        List<Object> jsonList = (List) params.get(groupName);
                        StringBuilder builder = new StringBuilder();
                        for (Object obj : jsonList) {
                            if (obj instanceof String) {
                                builder.append(StringUtil.getInNoDbFormat((String) obj));
                            } else {
                                builder.append(obj);
                            }

                            builder.append(",");
                        }
                        builder.deleteCharAt(builder.lastIndexOf(","));
                        newSql = newSql.replace(groupParam, builder.toString());
                        System.out.println(StringUtil.getInNoDbFormat(builder.toString()));
                    } else {
                        newSql = newSql.replace(groupParam, (String) params.get(groupName));
                    }
                }
            }
            System.out.println(newSql);
            //对查询字段变量替换格式{test}
            Pattern pattern2 = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
            Matcher matcher2 = pattern2.matcher(newSql);
            while (matcher2.find()) {
                String temp = "{" + matcher2.group() + "}";
                if (params.containsKey(matcher2.group())) {
                    if (params.get(matcher2.group()) instanceof List) {
                        List<Object> jsonList = (List) params.get(matcher2.group());
                        StringBuilder builder = new StringBuilder();
                        for (Object obj : jsonList) {
                            builder.append(obj);
                            builder.append(",");
                        }
                        builder.deleteCharAt(builder.lastIndexOf(","));
                        newSql = newSql.replace(temp, builder.toString());
                        System.out.println("list-" + builder.toString());
                    } else {
                        newSql = newSql.replace(temp, (String) params.get(matcher2.group()));
                        System.out.println("string-" + params.get(matcher2.group()));
                    }
                }
            }
        }

        newSql = newSql.replaceAll("[\\n\\r]", " ");

        System.out.println(newSql);
    }
}
