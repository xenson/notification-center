package com.ctrip.car.osd.notificationcenter.enums;

import com.ctrip.car.osd.framework.common.exception.BizException;

/**
 * Created by xiayx on 2019/7/31.
 */
public enum CarAppMonitors {
    //错误数
    Error(1),
    //请求数
    Request(2),
    //性能均值
    Cost(3);

    private final long value;

    CarAppMonitors(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public String getDesc() {
        switch (this) {
            case Error:
                return "Error Count";
            case Request:
                return "Request Count";
            case Cost:
                return "Cost Avg";
            default:
                throw new BizException("unknown CarAppMonitor: " + this.toString());
        }
    }
}
