package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.ctrip.car.osd.notificationcenter.config.QCRemoting;

import java.util.Map;

/**
 * @author xiayx
 * @blame notification center
 * @since 2021/2/1
 * <p>
 * Composite+Singleton patterns
 */
public abstract class TrackerAct {
    /**
     * tracker switch depend on key value or another
     *
     * @return
     */
    public abstract boolean open(Map<String, String> trackInfo);

    /**
     * tracker action
     *
     * @return
     */
    public abstract void track(Map<String, String> trackInfo);

    /**
     * is not filter or duplicate
     *
     * @param trackInfo
     * @return
     */
    public boolean pure(Map<String, String> trackInfo) {
        return true;
    }

    //预解析
}
