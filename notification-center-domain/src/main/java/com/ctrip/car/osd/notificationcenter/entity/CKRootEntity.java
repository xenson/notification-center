package com.ctrip.car.osd.notificationcenter.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2021/11/3.
 */
public class CKRootEntity {
    private List<CKMetaEntity> meta;
    private List<Map<String, String>> data;
    private int rows;
    private int rows_before_limit_at_least;

    public List<CKMetaEntity> getMeta() {
        return meta;
    }

    public void setMeta(List<CKMetaEntity> meta) {
        this.meta = meta;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getRows_before_limit_at_least() {
        return rows_before_limit_at_least;
    }

    public void setRows_before_limit_at_least(int rows_before_limit_at_least) {
        this.rows_before_limit_at_least = rows_before_limit_at_least;
    }
}
