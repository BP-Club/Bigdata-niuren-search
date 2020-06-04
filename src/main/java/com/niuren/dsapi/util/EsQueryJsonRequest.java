package com.niuren.dsapi.util;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * ES Json Request
 */
public class EsQueryJsonRequest {
    XContentBuilder builder = null;

    /**
     * 构建ES查询开始
     *
     * @param
     * @return
     * @throws IOException
     */
    public XContentBuilder getBuilderStart() throws IOException {
        builder = XContentFactory.jsonBuilder()
                .startObject();
        return builder;
    }

    /**
     * 构建BoolQuery结束
     *
     * @return
     * @throws IOException
     */
    public XContentBuilder getBoolQueryStart() throws IOException {
        builder.startObject("query").startObject("bool").startArray("must");
        return builder;
    }

    /**
     * 构建BoolQuery
     *
     * @return
     * @throws IOException
     */
    public XContentBuilder getBoolQueryEnd() throws IOException {
        builder.endArray().endObject().endObject();
        return builder;
    }

    /**
     * 查询到所有文档
     *
     * @return
     * @throws IOException
     */
    public XContentBuilder getMustAll() throws IOException {
        builder.startObject("match_all").endObject();
        return builder;
    }

    /**
     * 构建ES查询结束
     *
     * @param
     * @return
     * @throws IOException
     */
    public XContentBuilder getBuilderEnd(int from, int size) throws IOException {
        //begin-aggs   end-aggs
        //增加分页机制
        builder.field("from", from);
        builder.field("size", size);
        builder.endObject();
        return builder;
    }

    /**
     * 构建ES查询结束
     *
     * @param
     * @return
     * @throws IOException
     */
    public XContentBuilder getBuilderEnd(int size) throws IOException {
        //增加分页机制
        builder.field("size", size);
        builder.endObject();
        return builder;
    }

    /**
     * 增加单个数据查询
     *
     * @param key
     * @param value
     * @return
     * @throws IOException
     */
    public XContentBuilder setTerm(String key, String value) throws IOException {
        builder.startObject().startObject("term").field(key, value).endObject().endObject();
        return builder;
    }

    public XContentBuilder setTerm(String key, int value) throws IOException {
        builder.startObject().startObject("term").field(key, value).endObject().endObject();
        return builder;
    }

    public XContentBuilder setTerm(String key, long value) throws IOException {
        builder.startObject().startObject("term").field(key, value).endObject().endObject();
        return builder;
    }

    /**
     * 大于等于小于等于某个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setRange(String key, String start, String end) throws IOException {
        if (!StringUtils.isEmpty(start) && !StringUtils.isEmpty(end)) {
            builder.startObject().startObject("range").startObject(key).field("gte", start).field("lte", end).
                    endObject().endObject().endObject();
        }
        return builder;
    }

    /**
     * 大于一个范围，小于一个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setLessRange(String key, int start, int end) throws IOException {

        builder.startObject().startObject("range").startObject(key).field("gt", start).field("lt", end).
                endObject().endObject().endObject();

        return builder;
    }

    /**
     * 大于一个范围，小于一个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setLessRange(String key, long start, long end) throws IOException {
        builder.startObject().startObject("range").startObject(key).field("gt", start).field("lt", end).
                endObject().endObject().endObject();

        return builder;
    }

    /**
     * 大于一个范围，小于一个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setLessRange(String key, String start, String end) throws IOException {

        builder.startObject().startObject("range").startObject(key).field("gt", start).field("lt", end).
                endObject().endObject().endObject();

        return builder;
    }

    /**
     * 大于等于小于等于某个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setLessRange(String key, float start, float end) throws IOException {

        builder.startObject().startObject("range").startObject(key).field("gt", start).field("lt", end).
                endObject().endObject().endObject();

        return builder;
    }

    /**
     * 大于等于,小于等于某个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setRange(String key, int start, int end) throws IOException {

        builder.startObject().startObject("range").startObject(key).field("gte", start).field("lte", end).
                endObject().endObject().endObject();

        return builder;
    }

    /**
     * 大于等于小于等于某个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setRange(String key, long start, long end) throws IOException {

        builder.startObject().startObject("range").startObject(key).field("gte", start).field("lte", end).
                endObject().endObject().endObject();

        return builder;
    }

    /**
     * 大于等于小于等于某个范围
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public XContentBuilder setRange(String key, float start, float end) throws IOException {
        builder.startObject().startObject("range").startObject(key).field("gte", start).field("lte", end).
                endObject().endObject().endObject();

        return builder;
    }

    /**
     * 增加数组查询
     *
     * @param key
     * @param list
     * @return
     * @throws IOException
     */
    public XContentBuilder setTerms(String key, List list) throws IOException {
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                builder.startObject().startObject("terms").field("level", list.toArray()).endObject().endObject();
            } else {
                builder.startObject().startObject("term").field("level", list.get(0)).endObject().endObject();
            }
        }
        return builder;
    }


//*************************************Aggs*****************************************

    /**
     * 普通聚合-聚合开始
     *
     * @param key
     * @return
     * @throws IOException
     */
    public XContentBuilder getModeAggsStart(String key, String fieldValue, int size) throws IOException {
        builder.startObject("aggs").startObject(key);
        builder.startObject("terms").field("field", fieldValue).field("size", size).endObject();
        builder.startObject("aggs");
        return builder;
    }

    /**
     * 普通聚合-聚合开始
     *
     * @param
     * @return
     * @throws IOException
     */
    public XContentBuilder getModeAggsEnd() throws IOException {
        builder.endObject();
        builder.endObject().endObject();
        return builder;
    }

    /**
     * 聚合开始
     *
     * @param key
     * @return
     * @throws IOException
     */
    public XContentBuilder getAggsStart(String key) throws IOException {
        builder.startObject("aggs").startObject(key);
        return builder;
    }

    public XContentBuilder getAggsEnd(String key) throws IOException {
        builder.endObject().endObject();
        return builder;
    }

    public XContentBuilder startObject(String key) throws IOException {
        builder.startObject(key);
        return builder;
    }

    public XContentBuilder endObject() throws IOException {
        builder.endObject();
        return builder;
    }

    /**
     * 聚合分组
     *
     * @param key
     * @param size
     * @return
     * @throws IOException
     */
    public XContentBuilder setAggsTearms(String key, int size) throws IOException {
        builder.startObject("terms").field("field", key).field("size", size).endObject();
        return builder;
    }

    /**
     * sum求和
     *
     * @param key
     * @param value
     * @return
     * @throws IOException
     */
    public XContentBuilder setAggsSum(String key, String value) throws IOException {
        builder.startObject(key).startObject("sum").field("field", value).endObject().endObject();
        return builder;
    }

    /**
     * avg求平均
     *
     * @param key
     * @param value
     * @return
     * @throws IOException
     */
    public XContentBuilder setAggsAvg(String key, String value) throws IOException {
        builder.startObject(key).startObject("avg").field("field", value).endObject().endObject();
        return builder;
    }

    /**
     * 结果返回
     *
     * @return
     */
    public String getJsonString() throws IOException {
        if (builder != null) {
            return builder.string();
        } else {
            return "";
        }
    }


}
