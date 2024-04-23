package com.ctrip.car.osd.notificationcenter.kafka;

import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.hermes.consumer.api.*;
import com.ctrip.hermes.core.message.ConsumerMessage;
import com.dianping.cat.Cat;
import hermes.ubt.custom.CustomEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2022/8/3.
 */
public class KafkaMQConsumer {
    /**
     * kafka switch (temp)
     *
     * @param index
     * @return
     */
    public Boolean isKafkaOpen(Integer index) {
        List<String> kafkaOpen = QCSwitch.getList("KafkaConsumer_Open");
        if (kafkaOpen.get(index).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * kafka sleep (temp)
     *
     * @param index
     * @return
     */
    public Integer kafkaSleep(Integer index) {
        List<String> kafkaOpen = QCSwitch.getList("KafkaConsumer_Sleep");
        return Integer.valueOf(kafkaOpen.get(index));
    }

    /**
     * kafka listener start
     */
    public void listenerConsumerStart() {
        String topic = "fx.cat.log.car-rental-blueprint";
        String consumer = "car.sd.notificationcenter.tracker";
        Integer switchIndex = 0;

        try {
            Consumer.getInstance().start(topic, consumer, new BaseMessageListener<String>() {
                @Override
                public void onMessage(ConsumerMessage<String> msg) {
//                    if (!isKafkaOpen(switchIndex)) {
//                        return;
//                    }

                    //用户消息处理逻辑
                    Boolean isSuccess = MqDisassemble.disassembleKafkaMq(msg.getBody(), topic);
                    System.out.println("---" + msg.getBody());
                    if (isSuccess) {
                        System.out.println("===" + msg.getBody());
                    }

                    //解析失败无需重发
                    msg.ack();
                    try {
                        //thread to sleep for 1000 milliseconds
                        //Thread.sleep(kafkaSleep(switchIndex) * 1000);
                    } catch (Exception e) {
                        //System.err.println(e);
                        LogUtils.warn("KafkaConsumer_listenerConsumerStart", e);
                    }
                }
            });
        } catch (Exception ex) {
            LogUtils.warn("KafkaConsumer_listenerConsumerStart", ex);
        } finally {
            LogUtils.warn("KafkaConsumer_listenerConsumerStart", "listener start");
        }
    }

    public void rddevtrace_Consumer() {
        String topic = "car.rd.devtrace.dispatched.full";
        String consumer = "car.sd.notificationcenter.tracker";
        Integer switchIndex = 1;

        try {
            Consumer.getInstance().start(topic, consumer, new BaseMessageListener<CustomEvent>() {
                @Override
                public void onMessage(ConsumerMessage<CustomEvent> msg) {
//                    if (!isKafkaOpen(switchIndex)) {
//                        return;
//                    }
                    //用户消息处理逻辑
                    String msgBody = msg.getBody().toString();
                    //disassemble message from topic
                    Boolean isSuccess = MqDisassemble.disassembleKafkaMq(msgBody, topic);

                    if (QCSwitch.get("KafkaConsumer_CatLog")) {
                        Map<String, String> logTags = new HashMap<>();
                        Map<String, String> storeTags = new HashMap<>();
                        logTags.put("msg", msgBody);
                        logTags.put("isSuccess", isSuccess.toString());
                        logTags.put("method", "rddevtrace_Consumer");
                        Cat.logTags("car-rental-ubtdevtrace", logTags, storeTags);
                    }
                    //解析失败无需重发
                    msg.ack();
                    try {
                        //thread to sleep for 1000 milliseconds
                        //Thread.sleep(kafkaSleep(switchIndex) * 1000);
                    } catch (Exception ex) {
                        //System.err.println(e);
                        LogUtils.warn("KafkaConsumer_rddevtrace_Consumer", ex);
                    }
                }
            });
        } catch (Exception ex) {
            LogUtils.warn("KafkaConsumer_rddevtrace_Consumer", ex);
        } finally {
            LogUtils.warn("KafkaConsumer_rddevtrace_Consumer", "listener start");
        }
    }

    public void biplatform_Consumer() {
        String topic = "car.bi.platform.ubt.custom.full";
        String consumer = "car.sd.notificationcenter.trace.tracker";
        Integer switchIndex = 2;

        try {
            Consumer.getInstance().start(topic, consumer, new BaseMessageListener<CustomEvent>() {
                @Override
                public void onMessage(ConsumerMessage<CustomEvent> msg) {
//                    if (!isKafkaOpen(switchIndex)) {
//                        return;
//                    }
                    //用户消息处理逻辑
                    String msgBody = msg.getBody().toString();
                    //disassemble message from topic
                    Boolean isSuccess = MqDisassemble.disassembleKafkaMq(msgBody, topic);

                    if (QCSwitch.get("KafkaConsumer_CatLog")) {
                        Map<String, String> logTags = new HashMap<>();
                        Map<String, String> storeTags = new HashMap<>();
                        logTags.put("msg", msgBody);
                        logTags.put("isSuccess", isSuccess.toString());
                        logTags.put("method", "biplatform_Consumer");
                        Cat.logTags("car-rental-ubtdevtrace", logTags, storeTags);
                    }

                    //解析失败无需重发
                    msg.ack();
                    try {
                        //thread to sleep for 1000 milliseconds
                        //Thread.sleep(kafkaSleep(switchIndex) * 1000);
                    } catch (Exception e) {
                        //System.err.println(e);
                        LogUtils.warn("KafkaConsumer_biplatform_Consumer", e);
                    }
                }
            });
        } catch (Exception ex) {
            LogUtils.warn("KafkaConsumer_biplatform_Consumer", ex);
        } finally {
            LogUtils.warn("KafkaConsumer_biplatform_Consumer", "listener start");
        }
    }

//    /**
//     * kafka listener start
//     */
//    public void listenerConsumerStart() {
//        String topic = "fx.cat.log.car-rental-blueprint";
//        String consumer = "car.sd.notificationcenter.tracker";
//        try {
//            Consumer.getInstance().start(topic, consumer, new BaseMessageListener<String>() {
//                @Override
//                public void onMessage(ConsumerMessage<String> msg) {
//                    if (!isKafkaOpen(0)) {
//                        return;
//                    }
//
//                    //用户消息处理逻辑
//                    String body = msg.getBody();
//                    System.out.println("===" + body);
//                    Boolean isSuccess = MqDisassemble.disassembleKafkaMq(body, "");
//
//                    if (!isSuccess) {
//                        msg.nack();
//                    } else {
//                        msg.ack();
//                    }
//
//                    try {
//                        //thread to sleep for 1000 milliseconds
//                        Thread.sleep(kafkaSleep(0) * 1000);
//                    } catch (Exception e) {
//                        System.err.println(e);
//                    }
//                }
//            });
//        } catch (Exception ex) {
//            LogUtils.warn("KafkaConsumer_listenerConsumerStart", ex);
//        } finally {
//            LogUtils.warn("KafkaConsumer_listenerConsumerStart", "listener start");
//        }
//    }

//    public void kafkaConsumerTest() {
//        PullConsumerConfig config = new PullConsumerConfig();
//        //需要保证消息类型为生产者使用的类型，此处以String为例
//        PullConsumerHolder<String> holder = Consumer.getInstance().openPullConsumer(topic, consumerGroup, String.class, config);
//        try {
//            while (true) {
//                // PulledBatch<T> collect(int expectedMessageCount, int timeout);
//                // 此处期望1秒内尽量收集100条消息
//                PulledBatch<String> batch = holder.collect(1, 1000);
//                List<ConsumerMessage<String>> msgs = batch.getMessages();
//                if (msgs != null && msgs.size() > 0) {
//                    for (ConsumerMessage<String> msg : msgs) {
//                        try {
//                            // 客户端处理逻辑
//                            System.out.println(msg.getBody());
//                        } catch (Exception e) {
//                            // 对于有异常的消息或者需要后续重新处理的消息可以Nack
//                            msg.nack();
//                        }
//                    }
//                    //消费完一个batch的消息必须主动commit
//                    batch.commitAsync();
//                }
//            }
//        } finally {
//            try {
//                // 结束后必须Close
//                holder.close();
//            } catch (Exception ex) {
//                String stop = "stop";
//            }
//        }

//        String topic = "fx.cat.log.car-rental-blueprint";
//        String consumerGroup = "car.sd.notificationcenter.tracker";
//        PullConsumerConfig config = new PullConsumerConfig();
//        //需要保证消息类型为生产者使用的类型，此处以String为例
//        PullConsumerHolder<String> holder = Consumer.getInstance().openPullConsumer(topic, consumerGroup, String.class, config);
//        try {
//            while (true) {
//                // PulledBatch<T> poll(int maxMessageCount, int timeout);
//                //此处最多拉取100条消息，1秒超时(只要有1条消息到达，Poll方法立刻返回，否则最多Block 1秒)
//                PulledBatch<String> batch = holder.poll(1, 1000);
//                List<ConsumerMessage<String>> msgs = batch.getMessages();
//                if (msgs != null && msgs.size() > 0) {
//                    for (ConsumerMessage<String> msg : msgs) {
//                        try {
//                            // 客户端处理逻辑
//                            System.out.println(msg.getBody());
//                        } catch (Exception e) {
//                            // 对于有异常的消息或者需要后续重新处理的消息可以Nack
//                            msg.nack();
//                        }
//                    }
//                    //消费完一个batch的消息必须主动commit
//                    batch.commitAsync();
//                }
//            }
//        } finally {
//            try {
//                // 结束后必须Close
//                holder.close();
//            } catch (Exception ex) {
//                String stop = "stop";
//            }
//        }

//        while (true) {
//            try {
//                Consumer.getInstance().start(topic, consumer, new BaseMessageListener<String>() {
//                    @Override
//                    public void onMessage(ConsumerMessage<String> msg) {
//                        // 用户消息处理逻辑
//                        String body = msg.getBody();
//                        if (true) {
//                            msg.nack();
//                            System.out.println(msg.getBody());
//                        } else {
//                            msg.ack();
//                        }
//                    }
//                });
//            } catch (Exception ex) {
//                String stop = "stop";
//            }
//        }
//    }

}
