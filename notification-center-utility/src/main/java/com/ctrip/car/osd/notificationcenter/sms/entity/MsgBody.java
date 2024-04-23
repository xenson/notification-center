package com.ctrip.car.osd.notificationcenter.sms.entity;

/**
 * Created by xiayx on 2019/11/5.
 */
public class MsgBody {
    private SmsContent content;
    private SmsChannelInfo channelInfo;

    public SmsContent getContent() {
        return content;
    }

    public void setContent(SmsContent content) {
        this.content = content;
    }

    public SmsChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(SmsChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }
}
