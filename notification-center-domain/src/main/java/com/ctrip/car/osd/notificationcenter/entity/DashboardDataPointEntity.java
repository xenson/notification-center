package com.ctrip.car.osd.notificationcenter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by xiayx on 2021/10/15.
 */
public class DashboardDataPointEntity {
    @JsonProperty("total")
    private Double total;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
