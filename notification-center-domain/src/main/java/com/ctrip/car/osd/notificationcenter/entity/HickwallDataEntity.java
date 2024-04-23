package com.ctrip.car.osd.notificationcenter.entity;

import java.util.List;

/**
 * Created by xiayx on 2021/10/13.
 */
public class HickwallDataEntity {
    private String resultType;
    private List<HickwallResultEntity> result;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List<HickwallResultEntity> getResult() {
        return result;
    }

    public void setResult(List<HickwallResultEntity> result) {
        this.result = result;
    }
}
