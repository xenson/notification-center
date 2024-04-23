package com.ctrip.car.osd.notificationcenter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by xiayx on 2021/11/5.
 */
public class CKReqBodyEntity {
    @JsonProperty("query_key")
    private String query_key;
    private List<String> params;

    public String getQuery_key() {
        return query_key;
    }

    public void setQuery_key(String query_key) {
        this.query_key = query_key;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
