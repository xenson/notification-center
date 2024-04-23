package com.ctrip.car.osd.notificationcenter.config;

import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.spring.QConfig;

/**
 * Created by xiayx on 2021/3/5.
 */
@Component
public class QCKeyContent {
    @QConfig("keycontent.json")
    private static String keycontents;

    public static String getKeyContents() {
        return keycontents;
    }
}
