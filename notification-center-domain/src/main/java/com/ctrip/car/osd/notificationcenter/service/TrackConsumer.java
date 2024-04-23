package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.tracker.TrackerUnite;
import com.dianping.cat.Cat;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.consumer.annotation.QmqConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiayx on 2023/5/29.
 */
@Service
public class TrackConsumer {
    /**
     * 埋点消息消费 - 埋点分发
     *
     * @param message
     */
    @QmqConsumer(prefix = "car.osdnotificationcenter.server.tracker", consumerGroup = "car.osdnotificationcenter.server.tracker.consumer")
    public void processMessage(Message message) {
        //业务处理
        Map<String, String> indexTags = new HashMap<>();
        Map<String, String> storeTags = null;
        if (!message.getAttrs().isEmpty()) {
            for (Map.Entry item : message.getAttrs().entrySet()) {
                indexTags.put(item.getKey().toString(), item.getValue().toString());
            }

            if (QCSwitch.get("QmqConsumer_KeyCompare")) {
                indexTags.put("key", indexTags.getOrDefault("key", "183828") + "_qmq");
            }
            indexTags.put("qmqTag", "consumer");
        }

        if (QCSwitch.get("QmqConsumer_Distribute")) {
            TrackerUnite.tracker(indexTags);
        } else {
            Cat.logTags("car-rental-trackproducer", indexTags, storeTags);
        }
    }
}
