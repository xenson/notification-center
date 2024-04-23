package com.ctrip.car.osd.notificationcenter.module;

import cn.hutool.core.lang.Tuple;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.entity.*;
import com.ctrip.car.osd.notificationcenter.proxy.DashboardProxy;
import com.dianping.cat.Cat;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by xiayx on 2021/10/22.
 */
@Service
public class DashboardResultModule {
    @Autowired
    private CarAppMonitorModule carAppMonitorModule;

    private static String requestMetricName = "soa.service.request.count";
    private static String requestAggregator = "sum";
    private static String requestGroupKeys = "operation";

    private static String latencyMetricName = "soa.service.request.latency";
    private static String latencyAggregator = "avg";
    private static String latencyGroupKeys = "operation";

    /**
     * 获取TopN应用接口请求量
     *
     * @return
     */
    public List<CarAppRequestCountEntity> getTopAppsRequestCount() {
        List<String> appIds = carAppMonitorModule.filterAppIds();
        Integer topCount = Integer.valueOf(QCAppsetting.get("CarAppMonitor_TopCount"));

        Tuple dateScope = carAppMonitorModule.calculateDateScope(0);
        Timestamp start = dateScope.get(0);
        Timestamp end = dateScope.get(1);

        List<CarAppRequestCountEntity> costList = getAppsRequestResults(appIds, start, end);
        if (CollectionUtils.isEmpty(costList)) {
            return null;
        }
        costList.sort(Comparator.comparing(CarAppRequestCountEntity::getReqCount).reversed());
        if (costList.size() >= topCount) {
            return costList.subList(0, topCount);
        } else {
            return costList;
        }
    }

    /**
     * 批量获取应用接口请求量
     *
     * @param appIds
     * @param start
     * @param end
     * @return
     */
    public List<CarAppRequestCountEntity> getAppsRequestResults(List<String> appIds, Timestamp start, Timestamp end) {
        List<CarAppRequestCountEntity> requestList = new ArrayList<>();
        List<DashboardAggregateEntity> results = getAppsDashboardResult(requestMetricName, appIds, start, end,
                requestAggregator, requestGroupKeys);
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        for (DashboardAggregateEntity result : results) {
            CarAppRequestCountEntity request = new CarAppRequestCountEntity();
            request.setAppId(result.getAppId());
            request.setStart(start.toLocalDateTime());
            request.setEnd(end.toLocalDateTime());
            request.setAppName(result.getServiceName());
            request.setApiName(result.getApiName());
            request.setReqCount(result.getAggregate().intValue());
            requestList.add(request);
        }
        return requestList;
    }

    /**
     * 批量注入应用接口请求量环比,同比数据
     *
     * @param costList
     */
    public void pushAppsRequestLink(List<CarAppRequestCountEntity> costList) {
        //环比前一天
        Tuple dateScope1 = carAppMonitorModule.calculateDateScope(1);
        Timestamp start1 = dateScope1.get(0);
        Timestamp end1 = dateScope1.get(1);
        //同比前一周
        Tuple dateScope2 = carAppMonitorModule.calculateDateScope(7);
        Timestamp start2 = dateScope2.get(0);
        Timestamp end2 = dateScope2.get(1);

        List<String> appIds = costList.stream().map(CarAppRequestCountEntity::getAppId).distinct().collect(Collectors.toList());
        List<CarAppRequestCountEntity> linkResults1 = getAppsRequestResults(appIds, start1, end1);
        List<CarAppRequestCountEntity> linkResults2 = getAppsRequestResults(appIds, start2, end2);

        if (CollectionUtils.isNotEmpty(costList)) {
            for (CarAppRequestCountEntity app : costList) {
                CarAppRequestCountEntity link1 = linkResults1.stream().filter(p -> p.getAppId().equals(app.getAppId())
                        && p.getApiName().equals(app.getApiName())).findFirst().orElse(null);
                CarAppRequestCountEntity link2 = linkResults2.stream().filter(p -> p.getAppId().equals(app.getAppId())
                        && p.getApiName().equals(app.getApiName())).findFirst().orElse(null);

                app.setReqCount1(0);
                app.setReqCount2(0);
                app.setGapRate1(1D);
                app.setGapRate2(1D);
                if (link1 != null) {
                    app.setReqCount1(link1.getReqCount());
                    app.setGapRate1(carAppMonitorModule.calculateGapRate(app.getReqCount(), app.getReqCount1()));
                }
                if (link2 != null) {
                    app.setReqCount2(link2.getReqCount());
                    app.setGapRate2(carAppMonitorModule.calculateGapRate(app.getReqCount(), app.getReqCount2()));
                }
            }
        }
    }

    /**
     * 批量注入应用基本信息
     *
     * @param appList
     */
    public void pushAppsRequestBasicInfo(List<CarAppRequestCountEntity> appList) {
        if (CollectionUtils.isNotEmpty(appList)) {
            for (CarAppRequestCountEntity app : appList) {
                carAppMonitorModule.pushCarAppBasicInfo(app);
            }
        }
    }

    /**
     * 获取TopN应用接口耗时均值
     *
     * @return
     */
    public List<CarAppCostEntity> getTopAppsCostAvg() {
        List<String> appIds = carAppMonitorModule.filterAppIds();
        Integer topCount = Integer.valueOf(QCAppsetting.get("CarAppMonitor_TopCount"));

        Tuple dateScope = carAppMonitorModule.calculateDateScope(0);
        Timestamp start = dateScope.get(0);
        Timestamp end = dateScope.get(1);

        List<CarAppCostEntity> costList = getAppsCostResults(appIds, start, end);
        if (CollectionUtils.isEmpty(costList)) {
            return null;
        }
        costList.sort(Comparator.comparing(CarAppCostEntity::getApiCost).reversed());
        if (costList.size() >= topCount) {
            return costList.subList(0, topCount);
        } else {
            return costList;
        }
    }

    /**
     * 批量获取应用接口耗时均值
     *
     * @param appIds
     * @param start
     * @param end
     * @return
     */
    public List<CarAppCostEntity> getAppsCostResults(List<String> appIds, Timestamp start, Timestamp end) {
        List<CarAppCostEntity> costList = new ArrayList<>();
        List<DashboardAggregateEntity> results = getAppsDashboardResult(latencyMetricName, appIds, start, end,
                latencyAggregator, latencyGroupKeys);
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }
        for (DashboardAggregateEntity result : results) {
            CarAppCostEntity cost = new CarAppCostEntity();
            cost.setAppId(result.getAppId());
            cost.setStart(start.toLocalDateTime());
            cost.setEnd(end.toLocalDateTime());
            cost.setAppName(result.getServiceName());
            cost.setApiName(result.getApiName());
            cost.setApiCost(result.getAggregate());
            costList.add(cost);
        }
        return costList;
    }

    /**
     * 批量注入环比,同比数据
     *
     * @param costList
     */
    public void pushAppsCostAvgLink(List<CarAppCostEntity> costList) {
        //环比前一天
        Tuple dateScope1 = carAppMonitorModule.calculateDateScope(1);
        Timestamp start1 = dateScope1.get(0);
        Timestamp end1 = dateScope1.get(1);
        //同比前一周
        Tuple dateScope2 = carAppMonitorModule.calculateDateScope(7);
        Timestamp start2 = dateScope2.get(0);
        Timestamp end2 = dateScope2.get(1);

        List<String> appIds = costList.stream().map(CarAppCostEntity::getAppId).distinct().collect(Collectors.toList());
        List<CarAppCostEntity> linkResults1 = getAppsCostResults(appIds, start1, end1);
        List<CarAppCostEntity> linkResults2 = getAppsCostResults(appIds, start2, end2);

        if (CollectionUtils.isNotEmpty(costList)) {
            for (CarAppCostEntity app : costList) {
                CarAppCostEntity link1 = linkResults1.stream().filter(p -> p.getAppId().equals(app.getAppId())
                        && p.getApiName().equals(app.getApiName())).findFirst().orElse(null);
                CarAppCostEntity link2 = linkResults2.stream().filter(p -> p.getAppId().equals(app.getAppId())
                        && p.getApiName().equals(app.getApiName())).findFirst().orElse(null);

                app.setApiCost1(0D);
                app.setApiCost2(0D);
                app.setGapRate1(1D);
                app.setGapRate2(1D);
                if (link1 != null) {
                    app.setApiCost1(link1.getApiCost());
                    app.setGapRate1(carAppMonitorModule.calculateGapRate(app.getApiCost(), app.getApiCost1()));
                }
                if (link2 != null) {
                    app.setApiCost2(link2.getApiCost());
                    app.setGapRate2(carAppMonitorModule.calculateGapRate(app.getApiCost(), app.getApiCost2()));
                }
            }
        }
    }

    /**
     * 批量注入应用基本信息
     *
     * @param appList
     */
    public void pushAppsCostBasicInfo(List<CarAppCostEntity> appList) {
        if (CollectionUtils.isNotEmpty(appList)) {
            for (CarAppCostEntity app : appList) {
                carAppMonitorModule.pushCarAppBasicInfo(app);
            }
        }
    }

    //请求重试
    private Retryer<DashboardRootEntity> retryer = RetryerBuilder
            .<DashboardRootEntity>newBuilder()
            //抛出runtime异常、checked异常时都会重试，但是抛出error不会重试。
            .retryIfException()
            //返回false也需要重试
            .retryIfResult(Objects::isNull)
            //尝试次数
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .build();

    /**
     * 批量获取Dashboard metric指标聚合后结果
     *
     * @param metricName
     * @param appIds
     * @param start
     * @param end
     * @param aggregator
     * @param groupKeys
     * @return
     */
    public List<DashboardAggregateEntity> getAppsDashboardResult(String metricName, List<String> appIds, Timestamp start, Timestamp end,
                                                                 String aggregator, String groupKeys) {
        List<DashboardAggregateEntity> appsResult = new ArrayList<>();
        for (String appId : appIds) {
            List<DashboardAggregateEntity> appResult = dashboardAggregateResults(metricName, appId, start, end,
                    aggregator, groupKeys);
            if (CollectionUtils.isNotEmpty(appResult)) {
                appsResult.addAll(appResult);
            }
        }
        return appsResult;
    }

    /**
     * 获取Dashboard metric指标聚合后结果
     *
     * @param metricName
     * @param appId
     * @param start
     * @param end
     * @param aggregator
     * @param groupKeys
     * @return
     */
    public List<DashboardAggregateEntity> dashboardAggregateResults(String metricName, String appId, Timestamp start, Timestamp end,
                                                                    String aggregator, String groupKeys) {
        List<String> apiBlackList = QCAppsetting.getList("CarAppMonitor_Api_BlackList");
        List<DashboardAggregateEntity> aggreResults = new ArrayList<>();

        try {
            Callable<DashboardRootEntity> retryerCall = () -> DashboardProxy.searchDashboardMeric(metricName, appId, start, end,
                    aggregator, groupKeys);
            DashboardRootEntity dashboardResult = retryer.call(retryerCall);

            if (dashboardResult != null) {
                if (CollectionUtils.isNotEmpty(dashboardResult.getTime_series_group_list())) {
                    for (DashboardTimeSeriesGroupEntity timeSeries : dashboardResult.getTime_series_group_list()) {
                        try {
                            String fullName = timeSeries.getTime_series_group().getOperation();
                            String apiName = fullName.substring(fullName.lastIndexOf(".") + 1);
                            if (apiBlackList.contains(apiName)) {
                                continue;
                            }
                            String serviceName = fullName.substring(0, fullName.lastIndexOf("."));

                            DashboardAggregateEntity aggreResult = new DashboardAggregateEntity();
                            aggreResult.setAppId(appId);
                            aggreResult.setServiceName(serviceName);
                            aggreResult.setApiName(apiName);
                            aggreResult.setAggregate(timeSeries.getData_points().getTotal() != null
                                    ? timeSeries.getData_points().getTotal() : 0);

                            aggreResults.add(aggreResult);
                        } catch (Exception ex) {
                            Cat.logError("getDashboardAggregateResult_" + metricName + "_" + appId, ex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Cat.logError("getDashboardAggregateResult", ex);
        }
        return aggreResults;
    }

}
