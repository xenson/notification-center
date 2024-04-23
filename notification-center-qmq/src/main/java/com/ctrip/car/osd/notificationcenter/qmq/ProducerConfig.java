package com.ctrip.car.osd.notificationcenter.qmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.producer.MessageProducerProvider;

/**
 * Created by xiayx on 2021/12/31.
 */
@Configuration
public class ProducerConfig {
    @Bean
    //默认主要的QMQ Producer实现,不指定Bean名称,不指定名称装配时默认使用Primary - 防止其他Bean获取冲突
    //Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'qmqNotifier'
    // defined in class path resource [com/ctrip/car/sd/dynamictp/DtpAutoConfiguration.class]:
    // Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException:
    // Failed to instantiate [com.ctrip.car.sd.dynamictp.notify.QmqNotifier]: Factory method 'qmqNotifier' threw exception;
    // nested exception is org.springframework.beans.factory.NoUniqueBeanDefinitionException:
    // No qualifying bean of type 'qunar.tc.qmq.MessageProducer' available: expected single matching bean but found 3:
    // producer,QmqConfigTestProducer,trackProducerInstance
    //@Primary
    MessageProducer producer() {
        MessageProducerProvider provider = new MessageProducerProvider();
        provider.init();
        return provider;
    }
}
