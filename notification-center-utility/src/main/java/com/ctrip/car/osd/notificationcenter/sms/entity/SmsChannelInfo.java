package com.ctrip.car.osd.notificationcenter.sms.entity;

import java.util.List;

/**
 * Created by xiayx on 2019/11/5.
 */
public class SmsChannelInfo {
    private String orderID;
    private List<String> UIDList;
    private String uID;
    private String eID;
    private String mobilePhone;
    private String scheduleTime;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public List<String> getUIDList() {
        return UIDList;
    }

    public void setUIDList(List<String> UIDList) {
        this.UIDList = UIDList;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String geteID() {
        return eID;
    }

    public void seteID(String eID) {
        this.eID = eID;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
}
