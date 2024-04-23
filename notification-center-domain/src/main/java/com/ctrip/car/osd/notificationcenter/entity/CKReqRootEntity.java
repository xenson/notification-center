package com.ctrip.car.osd.notificationcenter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xiayx on 2021/11/5.
 */
public class CKReqRootEntity {
    @JsonProperty("access_token")
    private  String access_token;
    @JsonProperty("request_body")
    private CKReqBodyEntity request_body;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public CKReqBodyEntity getRequest_body() {
        return request_body;
    }

    public void setRequest_body(CKReqBodyEntity request_body) {
        this.request_body = request_body;
    }
}
