package com.niuren.dsapi.model.dto;

public class WhereDto {
    /**
     * 字段名称
     */
    private String name;
    /**
     * >=,>,=
     */
    private String function;
    /**
     * and/or
     */
    private String term;
    /**
     * 值
     */
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
