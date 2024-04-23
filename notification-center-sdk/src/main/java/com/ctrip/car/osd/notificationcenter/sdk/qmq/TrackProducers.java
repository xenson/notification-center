package com.ctrip.car.osd.notificationcenter.sdk.qmq;

import com.ctrip.car.osd.notificationcenter.sdk.basic.TrackDateUtil;
import com.dianping.cat.Cat;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageSendStateListener;
import qunar.tc.qmq.producer.MessageProducerProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2023/5/29.
 */
public class TrackProducers {
    /**
     * producer subject
     */
    private static String TRACK_SUBJECT = "car.osdnotificationcenter.server.tracker";

    //initial qmq provider
    private static MessageProducerProvider TRACK_PROVIDER;

    static {
        TRACK_PROVIDER = new MessageProducerProvider();
        TRACK_PROVIDER.init();
    }

    /**
     * send qmq message
     *
     * @param trackInfo
     */
    public static void sendMessage(Map<String, String> trackInfo) {
        sendMessage(TRACK_SUBJECT, trackInfo);
    }

    /**
     * send qmq messages
     *
     * @param trackInfos
     */
    public static void sendMessages(List<Map<String, String>> trackInfos) {
        for (Map<String, String> trackInfo : trackInfos) {
            sendMessage(TRACK_SUBJECT, trackInfo);
        }
    }

    /**
     * send qmq message
     *
     * @param subject
     * @param trackInfo
     */
    public static void sendMessage(String subject, Map<String, String> trackInfo) {
        Message message = TRACK_PROVIDER.generateMessage(subject);
        if (!trackInfo.isEmpty()) {
            trackInfo.put("dtimestamp", TrackDateUtil.dateNowStr());
            for (Map.Entry<String, String> tracker : trackInfo.entrySet()) {
                message.setProperty(tracker.getKey(), tracker.getValue());
            }
        }

        TRACK_PROVIDER.sendMessage(message, new MessageSendStateListener() {
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
                TRACK_PROVIDER.sendMessage(message);
                Cat.logTags("car-rental-trackproducer", trackInfo, null);
            }
        });
        //System.out.println(">>> message send: \n" + TrackJsonUtil.parseJson(message));
    }
}
