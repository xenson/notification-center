package com.ctrip.car.osd.notificationcenter.qmq;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.dianping.cat.Cat;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.consumer.annotation.QmqConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiayx on 2022/1/5.
 */
@Service
public class QmqConsumerServer {
//    @QmqConsumer(prefix = "car.osdnotificationcenter.server.tracker", consumerGroup = "car.osdnotificationcenter.server.tracker.consumer")
//    public void processMessage(Message message) {
//        //业务处理
//        System.out.println(">>> message received: \n" + JsonUtils.parseJson(message));
//        Map<String, String> indexTags = new HashMap<>();
//        Map<String, String> storeTags = null;
//        if (!message.getAttrs().isEmpty()) {
//            for (Map.Entry item : message.getAttrs().entrySet()) {
//                indexTags.put(item.getKey().toString(), item.getValue().toString());
//            }
//            indexTags.put("keyfrom", indexTags.getOrDefault("keyfrom", "trackAPI") + "_qmq");
//        }
//        Cat.logTags("car-rental-trackpage", indexTags, storeTags);
//    }
}
