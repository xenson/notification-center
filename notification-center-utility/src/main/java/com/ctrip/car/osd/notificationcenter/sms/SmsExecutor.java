package com.ctrip.car.osd.notificationcenter.sms;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.car.osd.notificationcenter.sms.entity.MsgBody;
import com.ctrip.car.osd.notificationcenter.sms.entity.SmsBody;
import com.ctrip.car.osd.notificationcenter.sms.entity.SmsChannelInfo;
import com.ctrip.car.osd.notificationcenter.sms.entity.SmsContent;
import com.ctrip.soa.platform.cti.comm.messageplatfrom.v1.MessagePlatformServiceClient;
import com.ctrip.soa.platform.cti.comm.messageplatfrom.v1.SendMessageRequestType;
import com.ctrip.soa.platform.cti.comm.messageplatfrom.v1.SendMessageResponseType;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;

import java.io.Serializable;

/**
 * Created by xiayx on 2019/11/5.
 */
public class SmsExecutor implements Runnable, Serializable {
    private final MessagePlatformServiceClient client;
    private final SmsBody smsBody;

    public SmsExecutor(final SmsBody msg) {
        smsBody = msg;
        client = MessagePlatformServiceClient.getInstance();
    }

    @Override
    public void run() {
        try {
            final SmsContent content = new SmsContent();
            content.setWarningMessage(smsBody.getContent());

            final SmsChannelInfo msgChannelInfo = new SmsChannelInfo();
            msgChannelInfo.setMobilePhone(smsBody.getRecipient());

            final MsgBody body = new MsgBody();
            body.setChannelInfo(msgChannelInfo);
            body.setContent(content);

            final SendMessageRequestType request = new SendMessageRequestType();
            request.setMessageCode(smsBody.getMessageCode());
            request.setMsgBody(JsonUtils.parseJson(body));

            final SendMessageResponseType response = client.sendMessage(request);

            if (!response.getResponseStatus().getAck().equals(AckCodeType.Success)) {
                LogUtils.info("SendSMS_Fail", "SendSMS_Fail:" + JsonUtils.parseJson(smsBody));
            }
        } catch (Exception ex) {
            LogUtils.error("SmsExecutor", ex);
        }
    }
}
