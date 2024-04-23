package com.ctrip.car.osd.notificationcenter.jobs;

import cn.hutool.json.JSONObject;
import com.ctrip.car.osd.framework.common.exception.BizException;
import com.ctrip.car.osd.notificationcenter.entity.QscheduleParameter;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.car.osd.notificationcenter.service.CarNotificationConvergeService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qschedule.config.QSchedule;
import qunar.tc.schedule.Parameter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by xiayx on 2021/10/15.
 */
@Component
public class CarNotificationConvergeWorker {
    @Autowired
    CarNotificationConvergeService carNotificationConvergeService;
    @QSchedule("com.ctrip.car.osd.notificationcenter.carNotificationConverge")
    public void convergeWorker(Parameter parameter) throws SQLException {
        boolean result;
        if (parameter != null) {
            QscheduleParameter qscheduleParameter = new QscheduleParameter();
            String[] dateGaps = null;
            String[] ids = null;
            if (StringUtils.isNotBlank(parameter.getString("dateGap"))) {
                dateGaps = parameter.getString("dateGap").replaceAll("\\s", "").split(",");
            }
            if (StringUtils.isNotBlank(parameter.getString("ids"))) {
                ids = parameter.getString("ids").replaceAll("\\s", "").split(",");
                qscheduleParameter.setIds(ids);
            }
            if (dateGaps != null) {
                List<String> dateList = new ArrayList<>();
                if (dateGaps.length == 1) {
                    dateList = Arrays.asList(dateGaps);
                }
                if (dateGaps.length == 2) {
                    dateList = getAllDate(dateGaps);
                }
                if (dateGaps.length > 2) {
                    throw new BizException("The date parameter is incorrect. Please provide only the start and end dates.");
                }
                for (String start : dateList) {
                    qscheduleParameter.setDateGap(start);
                    result = carNotificationConvergeService.aggregationAnalysisDay(qscheduleParameter);
                    LogUtils.info("CarNotificationConvergeWorker", "date:" + start + " result:" + result);
                }
            } else {
                result = carNotificationConvergeService.aggregationAnalysisDay(qscheduleParameter);
                LogUtils.info("CarNotificationConvergeWorker", "date:T+1 result:" + result);
            }
        } else {
            result = carNotificationConvergeService.aggregationAnalysisDay(new QscheduleParameter());
            LogUtils.info("CarNotificationConvergeWorker", "date:T+1 result:" + result);
        }
    }
    private List<String> getAllDate(String[] dateGaps) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        LocalDate start = LocalDate.parse(dateGaps[0], formatter);
        LocalDate end = LocalDate.parse(dateGaps[1], formatter);
        LocalDate current = start;
        List<String> dateList = new ArrayList<>();
        while (!current.isAfter(end)) {
            dateList.add(current.format(formatter));
            current = current.plusDays(1);
        }
        return dateList;
    }
}
