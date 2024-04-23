package com.ctrip.car.osd.notificationcenter.tracker.enums;

/**
 * Created by xiayx on 2020/5/18.
 */
public enum HickwallRecordType {
    /**
     * 埋点记录这个方法被调用了一次，以及这次调用使用的时间
     */
    recordOne(1),
    /**
     * 点记录这次调用返回的数据条数
     */
    recordSize(2),
    /**
     * 假如系统内部有一个缓存，缓存的key的数量随着时间而改变
     * 我们关心缓存的表现，指标：缓存的key随着时间是如何变化的。
     * addGauge方法要求传入一个Supplier，这个Supplier应该返回一个整数值
     * Metrics会每分钟调用一次Supplier，并把它返回的整数值发送给hickwall
     */
    addGauge(3);

    private int value;

    HickwallRecordType(int value) {
        this.value = value;
    }

    public HickwallRecordType getByName(String name) {
        for (HickwallRecordType obj : values()) {
            if (obj.name().equals(name)) {
                return obj;
            }
        }
        return recordOne;
    }

}
