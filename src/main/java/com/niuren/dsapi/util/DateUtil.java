package com.niuren.dsapi.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateUtil {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM_DDSS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String YYYYMMDDSS = "yyyyMMddHHmmssSSS";

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);
    /**
     * @param date
     * @return
     */
    public static String getYesterday(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date datedt = calendar.getTime();
        String yesterday = sdf.format(datedt);
        return yesterday;
    }


    /**
     * @param dateDt
     * @return
     */
    public static String getYYYYMMDDYesterday(String dateDt) {
        String yesterday = null;
        try {
            if (!StringUtils.isEmpty(dateDt)) {
                Date date = new SimpleDateFormat(YYYYMMDD).parse(dateDt);
                SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                Date datedt = calendar.getTime();
                yesterday = sdf.format(datedt);
            }
        } catch (Exception e) {
            log.warn("DateUtil-getYesterday dateDt=" + dateDt, e);
        }
        return yesterday;
    }

    /**
     * @param dateDt
     * @return
     */
    public static String getYYYY_MM_DDYesterday(String dateDt) {
        String yesterday = null;
        try {
            if (!StringUtils.isEmpty(dateDt)) {
                Date date = new SimpleDateFormat(YYYY_MM_DD).parse(dateDt);
                SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                Date datedt = calendar.getTime();
                yesterday = sdf.format(datedt);
            }
        } catch (Exception e) {
            log.warn("DateUtil-getYesterday dateDt=" + dateDt, e);
        }
        return yesterday;
    }

    /**
     * 格式化日期
     *
     * @param dateDt
     * @return
     */
    public static String getYYYYmmdd(String dateDt) {
        String yesterday = null;
        try {
            if (!StringUtils.isEmpty(dateDt)) {
                Date date = new SimpleDateFormat(YYYY_MM_DDSS).parse(dateDt);
                SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
                yesterday = sdf.format(date);
            }
        } catch (Exception e) {
            log.warn("DateUtil-getYesterday dateDt=" + dateDt, e);
        }
        return yesterday;
    }

    public static String getYYYYmmddHHss(Date date) {
        String newData = null;
        try {
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DDSS);
                newData = sdf.format(date);

            }
        } catch (Exception e) {
            log.warn("DateUtil-getYYYYmmddHHss dateDt=" + newData, e);
        }
        return newData;
    }

    public static synchronized String getYYYYmmddHHssSSS() {
        String newData = null;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDDSS);
            newData = sdf.format(new Date());
        } catch (Exception e) {
            log.warn("DateUtil-getYYYYmmddHHsssss dateDt=" + newData, e);
        }
        return newData;
    }

    /**
     * 获取昨天日期
     *
     * @param dateDt yyyy-MM-dd
     * @return
     */
    public static String getYesterday(String dateDt) {
        String yesterday = null;
        try {
            if (!StringUtils.isEmpty(dateDt)) {
                Date date = new SimpleDateFormat(YYYY_MM_DD).parse(dateDt);
                SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                Date datedt = calendar.getTime();
                yesterday = sdf.format(datedt);
            }
        } catch (Exception e) {
            log.warn("DateUtil-getYesterday dateDt=" + dateDt, e);
        }
        return yesterday;
    }

    /**
     * 四舍五入并获取小数点的后两位
     *
     * @param data
     * @return
     */
    public static String getRound(double data) {
        String resultData = Double.toString(data);
        try {
            double dataValue = Math.round(data * 100) * 0.01;
            resultData = new DecimalFormat("0.00").format(dataValue).toString();//3.14
        } catch (Exception e) {
            log.warn("DateUtil-getRound fail data=" + data, e);
        }
        return resultData;
    }

    /**
     * 四舍五入并获取整数
     *
     * @param data
     * @return
     */
    public static String getZRound(double data) {
        String resultData = Double.toString(data);
        try {
            resultData = new DecimalFormat("0").format(data).toString();//3.14
        } catch (Exception e) {
            log.warn("DateUtil-getRound fail data=" + data, e);
        }
        return resultData;
    }

    /**
     * local时间转换成UTC时间
     *
     * @param localTime
     * @return
     */
    public static String localToUTC(String localTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DDSS);
        Date localDate = null;
        try {
            localDate = sdf.parse(localTime);
        } catch (Exception e) {
        }
        long localTimeInMillis = localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate = new Date(calendar.getTimeInMillis());
        String rsTime = getYYYYmmddHHss(utcDate);
        return rsTime;
    }

    /**
     * utc时间转成local时间
     *
     * @param utcTime
     * @return
     */
    public static Date utcToLocal(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DDSS);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        Date locatlDate = null;
        try {
            utcDate = sdf.parse(utcTime);
            sdf.setTimeZone(TimeZone.getDefault());
            String localTime = sdf.format(utcDate.getTime());
            locatlDate = sdf.parse(localTime);
        } catch (Exception e) {
            log.error("error", e);
        }
        return locatlDate;
    }

    /**
     *
     */
    public static void main(String args[]) {
        System.out.println("------" + getZRound(0.00));
        String stime = DateUtil.getYYYY_MM_DDYesterday("2018-09-12") + " 16:00:00";
        String etime = "2018-09-12" + " 17:59:59";
        System.out.println(stime + "------" + etime);
        System.out.println("--666----" + getYYYYmmddHHssSSS());
    }

}
