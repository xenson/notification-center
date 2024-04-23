package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.tracker.TrackerUnite;
import com.dianping.cat.Cat;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.consumer.annotation.QmqConsumer;

import java.util.HashMap;
import java.util.Map;
@Service
public class TrackerConfigConsumer {
    @QmqConsumer(prefix = "car.flight.intl.resource.smart", consumerGroup = "car.osdnotificationcenter.tracker.config.consumer")
    public void processMessage(Message message) {
        Map<String, String> indexTags = new HashMap<>();
        Map<String, String> storeTags = null;
        if (!message.getAttrs().isEmpty()) {
            for (Map.Entry item : message.getAttrs().entrySet()) {
                indexTags.put(item.getKey().toString(), item.getValue().toString());
            }
            indexTags.put("qmqTag", "consumer");
        }
        Cat.logTags("car-rental-tracker-config", indexTags, storeTags);
    }
}
