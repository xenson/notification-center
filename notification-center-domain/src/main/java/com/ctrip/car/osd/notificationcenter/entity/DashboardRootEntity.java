package com.ctrip.car.osd.notificationcenter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by xiayx on 2021/10/15.
 */
public class DashboardRootEntity {
    private String appId;

    @JsonProperty("result-code")
    private String result_code;

    @JsonProperty("time-series-group-list")
    private List<DashboardTimeSeriesGroupEntity> time_series_group_list;

    public String getAppId() {
        return appId;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<DashboardTimeSeriesGroupEntity> getTime_series_group_list() {
        return time_series_group_list;
    }

    public void setTime_series_group_list(List<DashboardTimeSeriesGroupEntity> time_series_group_list) {
        this.time_series_group_list = time_series_group_list;
    }
}
