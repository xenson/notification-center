package com.ctrip.car.osd.notificationcenter.entity;

import java.util.List;

public class QscheduleParameter {
    private String dateGap;
    private String[] ids;

    public String getDateGap() {
        return dateGap;
    }

    public void setDateGap(String dateGap) {
        this.dateGap = dateGap;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }
}
