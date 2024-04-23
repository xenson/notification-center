package com.ctrip.car.osd.notificationcenter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xiayx on 2021/10/15.
 */
public class DashboardTimeSeriesGroupEntity {
    @JsonProperty("data-points")
    private DashboardDataPointEntity data_points;
    @JsonProperty("time-series-group")
    private DashboardOperationEntity time_series_group;

    public DashboardDataPointEntity getData_points() {
        return data_points;
    }

    public void setData_points(DashboardDataPointEntity data_points) {
        this.data_points = data_points;
    }

    public DashboardOperationEntity getTime_series_group() {
        return time_series_group;
    }

    public void setTime_series_group(DashboardOperationEntity time_series_group) {
        this.time_series_group = time_series_group;
    }
}
