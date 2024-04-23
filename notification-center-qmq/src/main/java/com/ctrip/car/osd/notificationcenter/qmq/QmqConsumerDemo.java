package com.ctrip.car.osd.notificationcenter.qmq;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.consumer.annotation.QmqConsumer;

/**
 * Created by xiayx on 2022/1/5.
 */
@Service
public class QmqConsumerDemo {
    @QmqConsumer(prefix = "car.sd.notificationcenter.alert")
    public void processMessage(Message message) {
        // 业务处理
        System.out.println(">>> message received: \n"+ JsonUtils.parseJson(message));
    }
}
