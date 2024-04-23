package com.ctrip.car.osd.notificationcenter.config;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.spring.QConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
public class QCClickhouse {
    @QConfig("clickhousetrack.properties")
    private static Map<String, String> appMap;

    public static String get(String key) {
        return appMap.get(key);
    }

    public static Map<String, String> getAppMap() {
        return appMap;
    }

    public static List<String> getList(String key) {
        String listVal = get(key);
        if (StringUtils.isNotEmpty(listVal)) {
            return JsonUtils.parseObjectList(listVal, String.class);
        } else {
            return new ArrayList<>();
        }
    }
}
