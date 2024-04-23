package com.ctrip.car.osd.notificationcenter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xiayx on 2021/10/15.
 */
public class DashboardOperationEntity {
    @JsonProperty("operation")
    private String operation;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
