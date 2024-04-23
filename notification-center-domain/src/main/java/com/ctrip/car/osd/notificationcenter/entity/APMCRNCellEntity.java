package com.ctrip.car.osd.notificationcenter.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2022/2/21.
 */
public class APMCRNCellEntity {
    private List<Map<String,String>> cells;

    public List<Map<String, String>> getCells() {
        return cells;
    }

    public void setCells(List<Map<String, String>> cells) {
        this.cells = cells;
    }
}
