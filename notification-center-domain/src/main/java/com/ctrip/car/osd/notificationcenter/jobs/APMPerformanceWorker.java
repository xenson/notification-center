package com.ctrip.car.osd.notificationcenter.jobs;

import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.car.osd.notificationcenter.service.FrontEndPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qschedule.config.QSchedule;
import qunar.tc.schedule.Parameter;

import java.time.LocalDateTime;

/**
 * Created by xiayx on 2022/2/17.
 */
@Component
public class APMPerformanceWorker {
    @Autowired
    FrontEndPerformanceService frontEndPerformanceService;

    @QSchedule("com.ctrip.car.osd.notificationcenter.sendAPMPerformanceMail")
    public void sendMailWorker(Parameter parameter) {
        LocalDateTime endDate = null;
        if (parameter != null) {
            endDate = DateTimeUtils.tryParseLDate(parameter.getString("requestDate")).orElse(null);
        }
        boolean result = frontEndPerformanceService.sendAPMPerformanceMail(endDate);
        LogUtils.info("CarAppMonitorWorker", "result:" + result);
    }
}
