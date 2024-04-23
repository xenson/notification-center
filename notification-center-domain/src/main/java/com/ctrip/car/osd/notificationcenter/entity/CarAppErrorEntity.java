package com.ctrip.car.osd.notificationcenter.entity;

import java.time.LocalDateTime;

/**
 * Created by xiayx on 2021/10/14.
 */
public class CarAppErrorEntity extends CarAppBasicInfoEntity {
    private Integer errorCount;
    private Integer errorCount1;
    private Integer errorCount2;
    private Double gapRate1;
    private Double gapRate2;

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getErrorCount1() {
        return errorCount1;
    }

    public void setErrorCount1(Integer errorCount1) {
        this.errorCount1 = errorCount1;
    }

    public Integer getErrorCount2() {
        return errorCount2;
    }

    public void setErrorCount2(Integer errorCount2) {
        this.errorCount2 = errorCount2;
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
