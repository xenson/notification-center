package com.ctrip.car.osd.notificationcenter.alert;

import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.spring.QConfig;

import java.util.List;

/**
 * Created by xiayx on 2022/1/4.
 */
@Component
public class QCAlertConditions {
    @QConfig("alert-conditions.json")
    private static List<AlertConditionEntity> alertConditions;

    public static List<AlertConditionEntity> get() {
        return alertConditions;
    }
}
