package com.ctrip.car.osd.notificationcenter.sdk.qmq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.producer.MessageProducerProvider;

/**
 * Created by xiayx on 2021/12/31.
 */
@Configuration
public class TrackProducerConfig {
    @Bean(name = "trackProducerInstance")
    @ConditionalOnMissingBean
    MessageProducer producer() {
        MessageProducerProvider provider = new MessageProducerProvider();
        provider.init();
        return provider;
    }
}
