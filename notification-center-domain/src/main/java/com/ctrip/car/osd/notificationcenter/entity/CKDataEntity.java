package com.ctrip.car.osd.notificationcenter.entity;

/**
 * Created by xiayx on 2021/11/3.
 */
public class CKDataEntity {
    private String stored_request;
    private String sourceFrom;
    private String title;

    public String getStored_request() {
        return stored_request;
    }

    public void setStored_request(String stored_request) {
        this.stored_request = stored_request;
    }

    public String getSourceFrom() {
        return sourceFrom;
    }

    public void setSourceFrom(String sourceFrom) {
        this.sourceFrom = sourceFrom;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
