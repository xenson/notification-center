package com.ctrip.car.osd.notificationcenter.qmq;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.MessageSendStateListener;

import java.util.Map;

/**
 * Created by xiayx on 2022/1/5.
 */
@Service
public class QmqProducerDemo {
    @Autowired
    //装配Bean指定 名称:messageProducerProvider
    //@Qualifier("QmqConfigTestProducer")
    private MessageProducer producer;

    /**
     * demo
     */
    public void sendMessage() {
        Message message = producer.generateMessage("car.sd.notificationcenter.alert");
        message.setProperty("this is a key 1", "this is message value 1");
        message.setProperty("this is a key 2", "this is message value 2");

        producer.sendMessage(message, new MessageSendStateListener() {
            @Override
            public void onSuccess(Message message) {
                // 投递消息成功的回调，do something
                LogUtils.info("AlertProducer", "success:\r\n" + JsonUtils.parseJson(message));
                System.out.println(">>> message send: \n" + JsonUtils.parseJson(message));
            }

            @Override
            public void onFailed(Message message) {
                // 投递消息失败的回调，do something
                LogUtils.error("AlertProducer", new Exception("faild:\r\n" + JsonUtils.parseJson(message)));
                producer.sendMessage(message);
            }
        });
        System.out.println(">>> message send: \n" + JsonUtils.parseJson(message));
    }

    public void sendMessage(String subject, Map<String, String> messages) {
        Message message = producer.generateMessage(subject);
        message.setProperty("this is a key 1", "this is message value 1");
        message.setProperty("this is a key 2", "this is message value 2");
        if (!messages.isEmpty()) {
            for (Map.Entry<String, String> alert : messages.entrySet()) {
                message.setProperty(alert.getKey(), alert.getValue());
            }
        }

        producer.sendMessage(message, new MessageSendStateListener() {
            @Override
            public void onSuccess(Message message) {
                // 投递消息成功的回调，do something
                LogUtils.info("AlertProducer", "success:\r\n" + JsonUtils.parseJson(message));
                System.out.println(">>> message send: \n" + JsonUtils.parseJson(message));
            }

            @Override
            public void onFailed(Message message) {
                // 投递消息失败的回调，do something
                LogUtils.error("AlertProducer", new Exception("faild:\r\n" + JsonUtils.parseJson(message)));
                producer.sendMessage(message);
            }
        });
        System.out.println(">>> message send: \n" + JsonUtils.parseJson(message));
    }
}
