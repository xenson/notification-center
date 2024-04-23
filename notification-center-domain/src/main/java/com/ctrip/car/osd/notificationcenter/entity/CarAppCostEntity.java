package com.ctrip.car.osd.notificationcenter.entity;

/**
 * Created by xiayx on 2021/10/15.
 */
public class CarAppCostEntity extends CarAppBasicInfoEntity {
    private String apiName;
    private Double apiCost;
    private Double apiCost1;
    private Double apiCost2;
    private Double gapRate1;
    private Double gapRate2;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Double getApiCost() {
        return apiCost;
    }

    public void setApiCost(Double apiCost) {
        this.apiCost = apiCost;
    }

    public Double getApiCost1() {
        return apiCost1;
    }

    public void setApiCost1(Double apiCost1) {
        this.apiCost1 = apiCost1;
    }

    public Double getApiCost2() {
        return apiCost2;
    }

    public void setApiCost2(Double apiCost2) {
        this.apiCost2 = apiCost2;
    }

    public Double getGapRate1() {
        return gapRate1;
    }

    public void setGapRate1(Double gapRate1) {
        this.gapRate1 = gapRate1;
    }

    public Double getGapRate2() {
        return gapRate2;
    }

    public void setGapRate2(Double gapRate2) {
        this.gapRate2 = gapRate2;
    }
}
