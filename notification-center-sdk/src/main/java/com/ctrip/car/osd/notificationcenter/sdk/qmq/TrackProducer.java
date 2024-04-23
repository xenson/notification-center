package com.ctrip.car.osd.notificationcenter.sdk.qmq;

import com.ctrip.car.osd.notificationcenter.sdk.basic.TrackDateUtil;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.MessageSendStateListener;

import java.util.Map;

/**
 * Created by xiayx on 2023/5/29.
 */
@Service
public class TrackProducer {
    /**
     * producer subject
     */
    private static String TRACK_SUBJECT = "car.osdnotificationcenter.server.tracker";

    @Autowired
    private MessageProducer trackProducerInstance;

    /**
     * send qmq message
     *
     * @param trackInfo
     */
    public void sendMessage(Map<String, String> trackInfo) {
        sendMessage(TRACK_SUBJECT, trackInfo);
    }

    /**
     * send qmq message
     *
     * @param subject
     * @param trackInfo
     */
    public void sendMessage(String subject, Map<String, String> trackInfo) {
        Message message = trackProducerInstance.generateMessage(subject);
        if (!trackInfo.isEmpty()) {
            trackInfo.put("dtimestamp", TrackDateUtil.dateNowStr());
            for (Map.Entry<String, String> tracker : trackInfo.entrySet()) {
                message.setProperty(tracker.getKey(), tracker.getValue());
            }
        }

        trackProducerInstance.sendMessage(message, new MessageSendStateListener() {
            @Override
            public void onSuccess(Message message) {
                //投递消息成功的回调LOG
                trackInfo.put("qmqSuccess", "true");
                Cat.logTags("car-rental-trackproducer", trackInfo, null);
            }

            @Override
            public void onFailed(Message message) {
                //投递消息失败的回调LOG
                trackInfo.put("qmqSuccess", "false");
                trackProducerInstance.sendMessage(message);
                Cat.logTags("car-rental-trackproducer", trackInfo, null);
            }
        });
        //System.out.println(">>> message send: \n" + JsonUtils.parseJson(message));
    }
}
