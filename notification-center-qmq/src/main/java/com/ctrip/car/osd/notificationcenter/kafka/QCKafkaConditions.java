package com.ctrip.car.osd.notificationcenter.kafka;

import com.ctrip.car.osd.notificationcenter.alert.AlertConditionEntity;
import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.spring.QConfig;

import java.util.List;

/**
 * Created by xiayx on 2022/1/4.
 */
@Component
public class QCKafkaConditions {
    @QConfig("kafka-conditions.json")
    private static List<KafkaConditionEntity> kafkaConditions;

    public static List<KafkaConditionEntity> get() {
        return kafkaConditions;
    }
}
