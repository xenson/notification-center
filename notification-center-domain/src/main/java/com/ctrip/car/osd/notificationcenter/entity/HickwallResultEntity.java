package com.ctrip.car.osd.notificationcenter.entity;

import java.util.List;

/**
 * Created by xiayx on 2021/10/13.
 */
public class HickwallResultEntity {
    private List<List<String>> values;

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
    }
}
