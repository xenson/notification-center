package com.ctrip.car.osd.notificationcenter.config;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.spring.QConfig;

import java.util.*;

/**
 * Created by xiayx on 2021/5/18.
 */
@Component
public class QCSwitch {
    @QConfig("switch.properties")
    private static Map<String, String> switchMap;

    private static Set<String> trueSet = new HashSet<String>(Arrays.asList("1", "true", "yes"));
    private static Set<String> falseSet = new HashSet<String>(Arrays.asList("0", "false", "no"));

    public static boolean get(String key) {
        if (!switchMap.containsKey(key)) {
            return true;
        } else {
            String switchVal = switchMap.get(key).toLowerCase();
            if (trueSet.contains(switchVal)) {
                return true;
            } else if (falseSet.contains(switchVal)) {
                return false;
            } else {
                return false;
            }
        }
    }

    public static List<String> getList(String key) {
        String listVal = switchMap.get(key);
        if (StringUtils.isNotEmpty(listVal)) {
            return JsonUtils.parseObjectList(listVal, String.class);
        } else {
            return new ArrayList<>();
        }
    }
}
