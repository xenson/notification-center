package com.ctrip.car.osd.notificationcenter.entity;

/**
 * Created by xiayx on 2021/10/15.
 */
public class CarAppRequestCountEntity extends CarAppBasicInfoEntity {
    private String apiName;
    private Integer reqCount;
    private Integer reqCount1;
    private Integer reqCount2;
    private Double gapRate1;
    private Double gapRate2;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Integer getReqCount() {
        return reqCount;
    }

    public void setReqCount(Integer reqCount) {
        this.reqCount = reqCount;
    }

    public Integer getReqCount1() {
        return reqCount1;
    }

    public void setReqCount1(Integer reqCount1) {
        this.reqCount1 = reqCount1;
    }

    public Integer getReqCount2() {
        return reqCount2;
    }

    public void setReqCount2(Integer reqCount2) {
        this.reqCount2 = reqCount2;
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
