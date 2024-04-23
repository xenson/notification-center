package com.ctrip.car.osd.notificationcenter.qmq;

import com.ctrip.car.osd.notificationcenter.alert.AlertProducer;

import java.util.Map;

/**
 * Created by xiayx on 2022/1/5.
 */
public class QMQProxy {
    private static AlertProducer alertProducer = new AlertProducer();

    /**
     * send qmq message by subject and message
     *
     * @param subject
     * @param alerts
     */
    public static void sendMessage(String subject, Map<String, String> alerts) {
        alertProducer.sendMessage(subject, alerts);
    }
}
