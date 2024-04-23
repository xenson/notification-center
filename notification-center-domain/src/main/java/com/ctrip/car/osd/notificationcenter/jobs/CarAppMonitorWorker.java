package com.ctrip.car.osd.notificationcenter.jobs;

import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.car.osd.notificationcenter.service.CarAppMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qschedule.config.QSchedule;

/**
 * Created by xiayx on 2021/10/15.
 */
@Component
public class CarAppMonitorWorker {
    @Autowired
    CarAppMonitorService carAppMonitorService;

    @QSchedule("com.ctrip.car.osd.notificationcenter.sendAppMonitorMail")
    public void sendMailWorker() {
        boolean result = carAppMonitorService.sendAppMonitorMail();
        LogUtils.info("CarAppMonitorWorker", "result:" + result);
    }
}
