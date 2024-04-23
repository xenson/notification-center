package com.ctrip.car.osd.notificationcenter.module;

import cn.hutool.core.lang.Tuple;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.entity.CarAppBasicInfoEntity;
import com.ctrip.car.osd.notificationcenter.entity.PaasAppDetailEntity;
import com.ctrip.car.osd.notificationcenter.proxy.PaasAppProxy;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by xiayx on 2021/10/22.
 */
@Service
public class CarAppMonitorModule {
    @Autowired
    private PaasAppProxy paasAppProxy;

    /**
     * search app detail
     *
     * @param appBasicInfo
     * @return
     */
    public CarAppBasicInfoEntity pushCarAppBasicInfo(CarAppBasicInfoEntity appBasicInfo) {
        PaasAppDetailEntity appInfo = paasAppProxy.searchAppInfo(appBasicInfo.getAppId());
        try {
            appBasicInfo.setAppName(appInfo.getChinese_name());
            appBasicInfo.setAppCategory(appInfo.getCategory());
            appBasicInfo.setAppImportance(appInfo.getApp_importance());

            String owners = appInfo.getOwner();
            if (CollectionUtils.isNotEmpty(appInfo.getOwner_list())) {
                StringJoiner joiner = new StringJoiner(",");
                appInfo.getOwner_list().forEach((p) -> joiner.add(p.getLast_name()));
                owners = joiner.toString();
            }
            appBasicInfo.setAppOwners(owners);
        } catch (Exception ex) {
            Cat.logError("pushCarAppBasicInfo_" + appBasicInfo.getAppId(), ex);
        }
        return appBasicInfo;
    }

    /**
     * get default date scope by duration days&link days
     *
     * @param linkDay
     * @return
     */
    public Tuple calculateDateScope(Integer linkDay) {
        Integer duration = Integer.valueOf(QCAppsetting.get("CarAppMonitor_Duration"));
        LocalDate ldNow = LocalDate.now();
        LocalDateTime startLdt = ldNow.minusDays(duration).atStartOfDay().minusSeconds(1);
        LocalDateTime endLdt = ldNow.atStartOfDay().minusSeconds(1);

        if (duration == 0) {
            endLdt = endLdt.plusDays(1);
        }
        if (linkDay > 0) {
            startLdt = startLdt.minusDays(linkDay);
            endLdt = endLdt.minusDays(linkDay);
        }
        Timestamp start = Timestamp.valueOf(startLdt);
        Timestamp end = Timestamp.valueOf(endLdt);

        //hour step
        Integer step = 3600;
        if (duration > 1) {
            //day step
            step = 86400;
        }

        return new Tuple(start, end, step, duration);
    }

    /**
     * filter appid list by black list
     *
     * @return
     */
    public List<String> filterAppIds() {
        List<String> allAppIds = QCAppsetting.getList("CarAppMonitor_AppIds");
        List<String> exAppIds = QCAppsetting.getList("CarAppMonitor_App_BlackList");

        if (CollectionUtils.isNotEmpty(exAppIds)) {
            return allAppIds.stream().filter(p -> !exAppIds.contains(p)).collect(Collectors.toList());
        } else {
            return allAppIds;
        }
    }

    /**
     * calculate rate of increase(+/-)
     *
     * @param currentNum
     * @param linkNum
     * @return
     */
    public Double calculateGapRate(Double currentNum, Double linkNum) {
        if (linkNum == 0) {
            return 1D;
        }
        Double gapRate = (currentNum - linkNum) / linkNum;
        //NumberFormat percentInstance = NumberFormat.getPercentInstance();
        //percentInstance.setMinimumFractionDigits(2);
        //return percentInstance.format(gapRate);
        return gapRate;
    }

    /**
     * calculate rate of increase(+/-)
     *
     * @param currentNum
     * @param linkNum
     * @return
     */
    public Double calculateGapRate(Integer currentNum, Integer linkNum) {
        Double current = currentNum * 1.00;
        Double link = linkNum * 1.00;
        return calculateGapRate(current, link);
    }
}
