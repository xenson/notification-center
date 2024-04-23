package com.ctrip.car.osd.notificationcenter.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by xiayx on 2021/10/11.
 */
public class PaasAppInfoRootEntity {
    private int count;
    private int page_size;
    private String next;
    private String previous;
    private List<PaasAppDetailEntity> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<PaasAppDetailEntity> getResults() {
        return results;
    }

    public void setResults(List<PaasAppDetailEntity> results) {
        this.results = results;
    }
}