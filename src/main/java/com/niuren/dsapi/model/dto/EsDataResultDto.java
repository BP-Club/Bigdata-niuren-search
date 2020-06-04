package com.niuren.dsapi.model.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.search.aggregations.Aggregations;

import java.util.List;
import java.util.Map;

/**
 * Es查询结果返回
 */
public class EsDataResultDto {
    private Object took;
    private Object timed_out;
    private Object _shards;
    private HitsResultDto hits;
    private JSONObject aggregations;
    private JSONArray buckets;

    public JSONArray getBuckets(String key) {
        if (aggregations != null) {
            buckets = aggregations.getJSONObject(key).getJSONArray("buckets");
        }
        return buckets;
    }

    public void setBuckets(JSONArray buckets) {
        this.buckets = buckets;
    }

    public Object getTook() {
        return took;
    }

    public void setTook(Object took) {
        this.took = took;
    }

    public Object getTimed_out() {
        return timed_out;
    }

    public void setTimed_out(Object timed_out) {
        this.timed_out = timed_out;
    }

    public Object get_shards() {
        return _shards;
    }

    public void set_shards(Object _shards) {
        this._shards = _shards;
    }

    public HitsResultDto getHits() {
        return hits;
    }

    public void setHits(HitsResultDto hits) {
        this.hits = hits;
    }

    public JSONObject getAggregations() {
        return aggregations;
    }

    public void setAggregations(JSONObject aggregations) {
        this.aggregations = aggregations;
    }
}
