package com.ctrip.car.osd.notificationcenter.sms.entity;

/**
 * Created by xiayx on 2019/11/5.
 */
public class SmsContent {
    private String warningMessage;
    private String smsExpiredTime;

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public String getSmsExpiredTime() {
        return smsExpiredTime;
    }

    public void setSmsExpiredTime(String smsExpiredTime) {
        this.smsExpiredTime = smsExpiredTime;
    }
}
