package com.ctrip.car.osd.notificationcenter.sms.entity;

/**
 * Created by xiayx on 2019/11/5.
 */
public class SmsBody {
    private String recipient;
    private String content;
    private int messageCode;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int messageCode) {
        this.messageCode = messageCode;
    }
}
