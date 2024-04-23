package com.ctrip.car.osd.notificationcenter.module;

import cn.hutool.core.lang.Tuple;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.entity.CarAppErrorEntity;
import com.ctrip.car.osd.notificationcenter.entity.HickwallVMDataRootEntity;
import com.ctrip.car.osd.notificationcenter.proxy.HickwallVMDataProxy;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by xiayx on 2021/10/22.
 */
@Service
public class HickwallResultModule {
    @Autowired
    private CarAppMonitorModule carAppMonitorModule;

    /**
     * search car apps error count list
     *
     * @return
     */
    public List<CarAppErrorEntity> getAppsErrorCount() {
        List<String> appIds = carAppMonitorModule.filterAppIds();
        Integer topCount = Integer.valueOf(QCAppsetting.get("CarAppMonitor_TopCount"));

        Tuple dateScope = carAppMonitorModule.calculateDateScope(0);
        Timestamp start = dateScope.get(0);
        Timestamp end = dateScope.get(1);
        Integer step = dateScope.get(2);

        List<CarAppErrorEntity> appsErrorCount = searchAppsErrorCount(appIds, start, end, step);
        for (CarAppErrorEntity errorItem : appsErrorCount) {
            carAppMonitorModule.pushCarAppBasicInfo(errorItem);
        }
        appsErrorCount.sort(Comparator.comparing(CarAppErrorEntity::getErrorCount).reversed());
        if (appsErrorCount.size() >= topCount) {
            return appsErrorCount.subList(0, topCount);
        } else {
            return appsErrorCount;
        }
    }

    /**
     * search car apps error count list
     *
     * @param appIds
     * @param start
     * @param end
     * @param step
     * @return
     */
    public List<CarAppErrorEntity> searchAppsErrorCount(List<String> appIds, Timestamp start, Timestamp end, Integer step) {
        List<CarAppErrorEntity> appsError = new ArrayList<>();
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            for (String appId : appIds) {
                //multi thread/async todo
                CarAppErrorEntity appError = searchAppErrorCount(appId, start, end, step);
                if (appError != null) {
                    appsError.add(appError);
                }
            }
        });
        future.join();
        return appsError;
    }

    /**
     * push apps link day error
     *
     * @param appList
     */
    public void pushAppsErrorLink(List<CarAppErrorEntity> appList) {
        //环比前一天
        Tuple dateScope1 = carAppMonitorModule.calculateDateScope(1);
        Timestamp start1 = dateScope1.get(0);
        Timestamp end1 = dateScope1.get(1);
        Integer step1 = dateScope1.get(2);
        //同比前一周
        Tuple dateScope2 = carAppMonitorModule.calculateDateScope(7);
        Timestamp start2 = dateScope2.get(0);
        Timestamp end2 = dateScope2.get(1);
        Integer step2 = dateScope2.get(2);

        for (CarAppErrorEntity app : appList) {
            CarAppErrorEntity linkError1 = searchAppErrorCount(app.getAppId(), start1, end1, step1);
            CarAppErrorEntity linkError2 = searchAppErrorCount(app.getAppId(), start2, end2, step2);

            if (linkError1 != null) {
                app.setErrorCount1(linkError1.getErrorCount());
                app.setGapRate1(carAppMonitorModule.calculateGapRate(app.getErrorCount(), app.getErrorCount1()));
            }
            if (linkError2 != null) {
                app.setErrorCount2(linkError2.getErrorCount());
                app.setGapRate2(carAppMonitorModule.calculateGapRate(app.getErrorCount(), app.getErrorCount2()));
            }
        }
    }

    /**
     * search app error count by appid
     *
     * @param appId
     * @param start
     * @param end
     * @param step
     * @return
     */
    public CarAppErrorEntity searchAppErrorCount(String appId, Timestamp start, Timestamp end, Integer step) {
        String qurery = "sum(sum_over_time(app.appid.idc.error.count_value{bu='租车',appid='%s'}))";
        String db = "FX-APM";
        Integer errorCount = 0;
        String reqQurery = String.format(qurery, appId);

        HickwallVMDataRootEntity res = HickwallVMDataProxy.searchHickwallVM(reqQurery, db, start, end, step);
        if (res != null && res.getData() != null && CollectionUtils.isNotEmpty(res.getData().getResult())) {
            List<List<String>> matrix = res.getData().getResult().get(0).getValues();
            for (List<String> kv : matrix) {
                errorCount += Integer.valueOf(kv.get(1));
            }

            CarAppErrorEntity errorEntity = new CarAppErrorEntity();
            errorEntity.setAppId(appId);
            errorEntity.setErrorCount(errorCount);
            errorEntity.setStart(start.toLocalDateTime());
            errorEntity.setEnd(end.toLocalDateTime());
            return errorEntity;
        } else {
            Cat.logError(appId, "searchAppErrorCount_Error : " + appId + "_" + start);
            return null;
        }
    }

}
