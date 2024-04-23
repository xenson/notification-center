package com.ctrip.car.osd.notificationcenter.tracker.enums;

/**
 * Created by xiayx on 2021/4/12.
 */
public enum TrackerType {
    /**
     * Click House tracker
     */
    trackCK(0),
    /**
     * Hickwall tracker
     */
    trackHickwall(1),
    /**
     * UBT tracker
     */
    trackUBT(2);

    private int value;

    TrackerType(int value) {
        this.value = value;
    }

}
