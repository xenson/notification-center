package com.ctrip.car.osd.notificationcenter.sms;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.car.osd.notificationcenter.sms.entity.SmsBody;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiayx on 2019/10/29.
 */
public class SmsSend {
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 2, 1L,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(1000),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private SmsSend() {
    }

    public static void sendSms(final String recipient, final String content, final int messageCode) {
        final SmsBody smsBody = new SmsBody();
        try {
            smsBody.setRecipient(recipient);
            smsBody.setContent(content);
            smsBody.setMessageCode(messageCode);

            executorService.execute(new SmsExecutor(smsBody));
        } catch (Exception ex) {
            LogUtils.info("MessageSend_Fail", "MessageSend_Fail:" + JsonUtils.parseJson(smsBody));
        }
    }

    public static void sendSms(final String recipient, final String content) {
        sendSms(recipient, content, 150013);
    }
}
