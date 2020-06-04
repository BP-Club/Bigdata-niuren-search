package com.niuren.dsapi.model.dto;

import java.util.List;

public class HitsResultDto {
    private String total;
    private String max_score;
    private List<HitsDto> hits;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMax_score() {
        return max_score;
    }

    public void setMax_score(String max_score) {
        this.max_score = max_score;
    }

    public List<HitsDto> getHits() {
        return hits;
    }

    public void setHits(List<HitsDto> hits) {
        this.hits = hits;
    }
}
