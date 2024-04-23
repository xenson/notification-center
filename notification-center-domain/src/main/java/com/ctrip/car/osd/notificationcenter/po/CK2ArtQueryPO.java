package com.ctrip.car.osd.notificationcenter.po;

/**
 * Created by xiayx on 2022/8/29.
 */
public class CK2ArtQueryPO {
    private String title;
    private String model;
    private String db;
    private String fields;
    private String query;
    private Integer utc = 1;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getUtc() {
        return utc;
    }

    public void setUtc(Integer utc) {
        this.utc = utc;
    }
}
