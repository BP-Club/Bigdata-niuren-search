package com.niuren.dsapi.model.dto;

public class MemListRequestDto {

    private MemListQueryDto condition;

    //每页数量
    private Integer pageSize;
    //页码
    private Integer pageNumber;

    public MemListQueryDto getCondition() {
        return condition;
    }

    public void setCondition(MemListQueryDto condition) {
        this.condition = condition;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
