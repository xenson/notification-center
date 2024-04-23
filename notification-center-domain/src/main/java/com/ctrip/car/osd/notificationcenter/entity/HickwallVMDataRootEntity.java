package com.ctrip.car.osd.notificationcenter.entity;

import java.util.List;

/**
 * Created by xiayx on 2021/10/12.
 */
public class HickwallVMDataRootEntity {
    private String status;
    private HickwallDataEntity data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HickwallDataEntity getData() {
        return data;
    }

    public void setData(HickwallDataEntity data) {
        this.data = data;
    }
}