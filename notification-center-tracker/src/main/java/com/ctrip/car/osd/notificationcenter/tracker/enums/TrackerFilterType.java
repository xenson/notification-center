package com.ctrip.car.osd.notificationcenter.tracker.enums;

import com.ctrip.car.osd.framework.common.exception.BizException;

/**
 * Created by xiayx on 2022/4/28.
 */
public enum TrackerFilterType {
    /**
     * condition filtering
     */
    filtering(0),
    /**
     * condition duplication
     */
    duplication(1);


    private int value;

    TrackerFilterType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TrackerFilterType convert(int value) {
        switch (value) {
            case 0:
                return filtering;
            case 1:
                return duplication;
            default:
                throw new BizException("Unkown TrackerFilterType: " + value);
        }
    }

    public static TrackerFilterType convert(String value) {
        switch (value) {
            case "0":
                return filtering;
            case "1":
                return duplication;
            default:
                throw new BizException("Unkown TrackerFilterType: " + value);
        }
    }

}
