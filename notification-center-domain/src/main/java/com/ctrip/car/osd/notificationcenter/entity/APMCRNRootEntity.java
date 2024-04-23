package com.ctrip.car.osd.notificationcenter.entity;

import java.util.List;

/**
 * Created by xiayx on 2022/2/21.
 */
public class APMCRNRootEntity {
    private Integer status;
    private String metric;
    private List<APMCRNCellEntity> rows;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public List<APMCRNCellEntity> getRows() {
        return rows;
    }

    public void setRows(List<APMCRNCellEntity> rows) {
        this.rows = rows;
    }
}
