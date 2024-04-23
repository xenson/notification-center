package com.ctrip.car.osd.notificationcenter.config;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import org.apache.commons.lang.StringUtils;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2020/5/28.
 */
public class QCRemoting {
    private static final String APP_ID = "100025206";
    private static final String FILE_NAME = "appsetting.properties";
    private static volatile Map<String, String> configMap;

    public static List<String> getList(String key) {
        String listVal = get(key);
        if (StringUtils.isNotEmpty(listVal)) {
            return JsonUtils.parseObjectList(listVal, String.class);
        } else {
            return new ArrayList<>();
        }
    }

    public static String get(final String key) {
        return getConfigMap().get(key);
    }

    public static Map<String, String> getConfigMap() {
        if (configMap == null) {
            MapConfig config = MapConfig.get(APP_ID, FILE_NAME, Feature.DEFAULT);
            configMap = config.asMap();
        }
        return configMap;
    }
}
