package com.ctrip.car.osd.notificationcenter.entity;

/**
 * Created by xiayx on 2021/10/22.
 */
public class DashboardAggregateEntity {
    private String appId;
    private String serviceName;
    private String apiName;
    private Double aggregate;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Double getAggregate() {
        return aggregate;
    }

    public void setAggregate(Double aggregate) {
        this.aggregate = aggregate;
    }
}
