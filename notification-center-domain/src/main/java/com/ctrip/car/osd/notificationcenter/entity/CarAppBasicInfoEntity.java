package com.ctrip.car.osd.notificationcenter.entity;

import java.time.LocalDateTime;

/**
 * Created by xiayx on 2021/10/14.
 */
public class CarAppBasicInfoEntity {
    private String appId;
    private String appName;
    private String appCategory;
    private String appOwners;
    private String appImportance;

    private LocalDateTime start;
    private LocalDateTime end;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getAppOwners() {
        return appOwners;
    }

    public void setAppOwners(String appOwners) {
        this.appOwners = appOwners;
    }

    public String getAppImportance() {
        return appImportance;
    }

    public void setAppImportance(String appImportance) {
        this.appImportance = appImportance;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
