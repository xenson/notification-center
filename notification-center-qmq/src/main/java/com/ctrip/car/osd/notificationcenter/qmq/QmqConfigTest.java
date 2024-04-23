package com.ctrip.car.osd.notificationcenter.qmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.producer.MessageProducerProvider;

/**
 * @author by Jianglh on 2019/7/10 11:19
 */
//配置类，使用@Bean注解实现Bean的声明
@Configuration
public class QmqConfigTest {
    //注入QMQ Producer实现,并指定Bean 名称:messageProducerProvider
    //@Bean(name = "QmqConfigTestProducer")
    @Bean
    MessageProducer producer() {
        MessageProducerProvider provider = new MessageProducerProvider();
        provider.init();
        return provider;
    }
}
