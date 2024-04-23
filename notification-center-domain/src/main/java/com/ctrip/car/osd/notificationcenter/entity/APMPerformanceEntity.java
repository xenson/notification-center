package com.ctrip.car.osd.notificationcenter.entity;

import java.math.BigDecimal;

/**
 * Created by xiayx on 2022/2/21.
 */
public class APMPerformanceEntity {
    /**
     * BU id
     */
    private String organizationId;
    /**
     * BU 名称
     */
    private String organizationName;
    /**
     * 增量下载时间-ms
     */
    private BigDecimal pkgLoadTimeAvg;

    /**
     * TTI(耗时)-s
     */
    private BigDecimal totalTimeAvg;
    /**
     * FCP
     */
    private BigDecimal FCPAvg;
    /**
     * 秒开率=秒开数量/样本量
     */
    private BigDecimal secTimeRate;

    /**
     * 成功率=1-(失败数量/样本量)
     */
    private BigDecimal successRate;
    /**
     * 样本量
     */
    private Integer sampleCount;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public BigDecimal getPkgLoadTimeAvg() {
        return pkgLoadTimeAvg;
    }

    public void setPkgLoadTimeAvg(BigDecimal pkgLoadTimeAvg) {
        this.pkgLoadTimeAvg = pkgLoadTimeAvg;
    }

    public BigDecimal getTotalTimeAvg() {
        return totalTimeAvg;
    }

    public void setTotalTimeAvg(BigDecimal totalTimeAvg) {
        this.totalTimeAvg = totalTimeAvg;
    }

    public BigDecimal getFCPAvg() {
        return FCPAvg;
    }

    public void setFCPAvg(BigDecimal FCPAvg) {
        this.FCPAvg = FCPAvg;
    }

    public BigDecimal getSecTimeRate() {
        return secTimeRate;
    }

    public void setSecTimeRate(BigDecimal secTimeRate) {
        this.secTimeRate = secTimeRate;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public Integer getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }
}
