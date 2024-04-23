package com.ctrip.car.osd.notificationcenter.alert;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.MessageSendStateListener;
import qunar.tc.qmq.producer.MessageProducerProvider;

import java.util.Map;

/**
 * Created by xiayx on 2021/12/30.
 */
public class AlertProducer {
    private MessageProducer producer;

    public AlertProducer() {
        MessageProducerProvider provider = new MessageProducerProvider();
        provider.init();
        producer = provider;

        LogUtils.info("AlertProducer", "qmq start");
    }

    /**
     * send qmq message by subject and message
     *
     * @param subject
     * @param alerts
     */
    public void sendMessage(String subject, Map<String, String> alerts) {
        //set message subject
        Message message = producer.generateMessage(subject);
        if (!alerts.isEmpty()) {
            for (Map.Entry<String, String> alert : alerts.entrySet()) {
                message.setProperty(alert.getKey(), alert.getValue());
            }
        }

        producer.sendMessage(message, new MessageSendStateListener() {
            @Override
            public void onSuccess(Message message) {
                // 投递消息成功的回调，do something
                LogUtils.info("AlertProducer", "success:\r\n" + JsonUtils.parseJson(message));
                //System.out.println(">>> message send: \n" + JsonUtils.parseJson(message));
            }

            @Override
            public void onFailed(Message message) {
                // 投递消息失败的回调，do something
                LogUtils.error("AlertProducer", new Exception("faild:\r\n" + JsonUtils.parseJson(message)));
            }
        });
        //System.out.println(">>> message send: \n"+ JsonUtils.parseJson(message));
    }
}
