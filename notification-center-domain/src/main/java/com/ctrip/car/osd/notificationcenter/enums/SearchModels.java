package com.ctrip.car.osd.notificationcenter.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by xiayx on 2021/11/18.
 */
public enum SearchModels {
    //0.常规字段查询
    Log(0),
    //1.字段性能聚合统计
    Perf(1),
    //2.字段数量分布聚合统计
    Rate(2),
    //3.流量聚合统计
    Flow(3);

    private final int value;

    SearchModels(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SearchModels findByName(String name) {
        SearchModels type = Log;
        if (StringUtils.isNotBlank(name)) {
            for (SearchModels obj : values()) {
                if (obj.name().equalsIgnoreCase(name)) {
                    type = obj;
                    break;
                }
            }
        }
        return type;
    }

    public static SearchModels findByAny(String val) {
        SearchModels type = Log;
        Boolean isFindName = false;
        if (StringUtils.isBlank(val)) {
            return type;
        }

        for (SearchModels obj : values()) {
            if (obj.name().equalsIgnoreCase(val)) {
                type = obj;
                isFindName = true;
                break;
            }
        }
        if (!isFindName) {
            for (SearchModels obj : values()) {
                if (obj.getValue() == Integer.valueOf(val)) {
                    type = obj;
                    break;
                }
            }
        }

        return type;
    }

}
