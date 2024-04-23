package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.email.EmailSend;
import com.ctrip.car.osd.notificationcenter.entity.*;
import com.ctrip.car.osd.notificationcenter.enums.CarAppMonitors;
import com.ctrip.car.osd.notificationcenter.module.CarAppMonitorModule;
import com.ctrip.car.osd.notificationcenter.module.DashboardResultModule;
import com.ctrip.car.osd.notificationcenter.module.HickwallResultModule;
import com.ctrip.car.osd.notificationcenter.proxy.PaasAppProxy;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by xiayx on 2021/10/13.
 */
@Service
public class CarAppMonitorService {
    @Autowired
    private PaasAppProxy paasAppProxy;
    @Autowired
    private DashboardResultModule dashboardResultModule;
    @Autowired
    private HickwallResultModule hickwallResultModule;
    @Autowired
    private CarAppMonitorModule carAppMonitorModule;

    /**
     * all content send mail entry
     *
     * @return
     */
    public boolean sendAppMonitorMail() {
        String recipients = QCAppsetting.get("CarAppMonitor_Recipients");
        String sender = QCAppsetting.get("CarAppMonitor_Sender");
        String cc = QCAppsetting.get("CarAppMonitor_CCs");
        String subject = QCAppsetting.get("CarAppMonitor_Subject");

        try {
            StringBuffer sbMailContent = new StringBuffer(QCAppsetting.get("CarAppMonitor_MailHead"));

            boolean runAsync = QCSwitch.get("CarAppMonitor_RunAsync");
            if (runAsync) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    sbMailContent.append(getErrorMailContent());
                }).thenAccept(p -> {
                    sbMailContent.append(getAppsCostMailContent());
                }).thenAccept(p -> {
                    sbMailContent.append(getAppsRequestCountMailContent());
                });
                future.join();
            } else {
                sbMailContent.append(getErrorMailContent());
                sbMailContent.append(getAppsCostMailContent());
                sbMailContent.append(getAppsRequestCountMailContent());
            }
            EmailSend.sendMail(recipients, recipients, sender, sender, cc, subject, sbMailContent.toString(), new ArrayList<>());
            return true;
        } catch (Exception ex) {
            Cat.logError("sendAppMonitorMail", ex);
            return false;
        }
    }

    /**
     * construct error mail content
     *
     * @return
     */
    public String getErrorMailContent() {
        try {
            List<CarAppErrorEntity> appsList = hickwallResultModule.getAppsErrorCount();
            hickwallResultModule.pushAppsErrorLink(appsList);

            if (CollectionUtils.isEmpty(appsList)) {
                return null;
            }
            StringBuffer sbContent = new StringBuffer(
                    getMailTitle(appsList.get(0).getStart(), appsList.get(0).getEnd(), CarAppMonitors.Error));

            String dataFormat = QCAppsetting.get("CarAppMonitor_DataRow");
            StringBuffer sbDataRow = new StringBuffer();
            for (CarAppErrorEntity app : appsList) {
                String dataRow = String.format(dataFormat, app.getAppId(), app.getAppId(), app.getAppName(),
                        app.getAppImportance().equals("1"), app.getAppOwners(),
                        app.getErrorCount().toString(), app.getErrorCount1(), getRateCellStyle(app.getGapRate1()),
                        app.getErrorCount2(), getRateCellStyle(app.getGapRate2()));
                sbDataRow.append(dataRow);
            }

            String tableFormat = QCAppsetting.get("CarAppMonitor_" + CarAppMonitors.Error.name() + "Table");
            sbContent.append(MessageFormat.format(tableFormat, sbDataRow.toString()));
            return sbContent.toString();
        } catch (Exception ex) {
            Cat.logError("getErrorMailContent", ex);
            return "";
        }
    }

    /**
     * construct api cost mail content
     *
     * @return
     */
    public String getAppsCostMailContent() {
        try {
            List<CarAppCostEntity> appsList = dashboardResultModule.getTopAppsCostAvg();
            dashboardResultModule.pushAppsCostAvgLink(appsList);
            dashboardResultModule.pushAppsCostBasicInfo(appsList);

            if (CollectionUtils.isEmpty(appsList)) {
                return null;
            }
            StringBuffer sbContent = new StringBuffer(
                    getMailTitle(appsList.get(0).getStart(), appsList.get(0).getEnd(), CarAppMonitors.Cost));

            String dataFormat = QCAppsetting.get("CarAppMonitor_DataRow");
            StringBuffer sbDataRow = new StringBuffer();
            for (CarAppCostEntity app : appsList) {
                String apiName = app.getApiName().substring(app.getApiName().lastIndexOf(".") + 1);

                String dataRow = String.format(dataFormat, app.getAppId(), app.getAppId(), apiName,
                        app.getAppImportance().equals("1"), app.getAppOwners(),
                        app.getApiCost().longValue(), app.getApiCost1().longValue(), getRateCellStyle(app.getGapRate1()),
                        app.getApiCost2().longValue(), getRateCellStyle(app.getGapRate2()));
                sbDataRow.append(dataRow);
            }

            String tableFormat = QCAppsetting.get("CarAppMonitor_" + CarAppMonitors.Cost.name() + "Table");
            sbContent.append(MessageFormat.format(tableFormat, sbDataRow.toString()));
            return sbContent.toString();
        } catch (Exception ex) {
            Cat.logError("getAppsCostMailContent", ex);
            return "";
        }
    }

    /**
     * construct app request mail content
     *
     * @return
     */
    public String getAppsRequestCountMailContent() {
        try {
            List<CarAppRequestCountEntity> appsList = dashboardResultModule.getTopAppsRequestCount();
            dashboardResultModule.pushAppsRequestLink(appsList);
            dashboardResultModule.pushAppsRequestBasicInfo(appsList);

            if (CollectionUtils.isEmpty(appsList)) {
                return null;
            }
            StringBuffer sbContent = new StringBuffer(
                    getMailTitle(appsList.get(0).getStart(), appsList.get(0).getEnd(), CarAppMonitors.Request));

            String dataFormat = QCAppsetting.get("CarAppMonitor_DataRow");
            StringBuffer sbDataRow = new StringBuffer();
            for (CarAppRequestCountEntity app : appsList) {
                String apiName = app.getApiName().substring(app.getApiName().lastIndexOf(".") + 1);

                String dataRow = String.format(dataFormat, app.getAppId(), app.getAppId(), apiName,
                        app.getAppImportance().equals("1"), app.getAppOwners(),
                        app.getReqCount().toString(), app.getReqCount1().toString(), getRateCellStyle(app.getGapRate1()),
                        app.getReqCount2().toString(), getRateCellStyle(app.getGapRate2()));
                sbDataRow.append(dataRow);
            }

            String tableFormat = QCAppsetting.get("CarAppMonitor_" + CarAppMonitors.Request.name() + "Table");
            sbContent.append(MessageFormat.format(tableFormat, sbDataRow.toString()));
            return sbContent.toString();
        } catch (Exception ex) {
            Cat.logError("getAppsCostMailContent", ex);
            return "";
        }
    }

    /**
     * fitting gap rate cell font style
     *
     * @param gapRate
     * @return
     */
    private String getRateCellStyle(Double gapRate) {
        try {
            List<String> cellStyles = JsonUtils.parseObjectList(QCAppsetting.get("CarAppMonitor_CellStyles"), String.class);
            List<Double> rateScopes = JsonUtils.parseObjectList(QCAppsetting.get("CarAppMonitor_RateScopes"), Double.class);

            NumberFormat percentInstance = NumberFormat.getPercentInstance();
            percentInstance.setMinimumFractionDigits(2);
            String cellContent = percentInstance.format(gapRate);

            int styleIndex = -1;
            for (int index = 1; index < rateScopes.size(); index++) {
                if (gapRate > rateScopes.get(index - 1) && gapRate <= rateScopes.get(index)) {
                    styleIndex = (index - 1);
                    break;
                }
            }

            if (styleIndex != -1) {
                cellContent = String.format(cellStyles.get(styleIndex), cellContent);
            }
            return cellContent;
        } catch (Exception ex) {
            Cat.logError("getRateCellStyle", ex);
            return "";
        }
    }

    /**
     * construct mail title row
     *
     * @param start
     * @param end
     * @param type
     * @return
     */
    private String getMailTitle(LocalDateTime start, LocalDateTime end, CarAppMonitors type) {
        String titleFormat = QCAppsetting.get("CarAppMonitor_" + type.name() + "Title");
        String startDate = DateTimeUtils.parseStr(start, "MM/dd");
        String endDate = DateTimeUtils.parseStr(end, "MM/dd");

        return String.format(titleFormat, endDate);
    }

    /**
     * initial app info cache from demoapi
     */
    public void constructCache() {
        List<String> appIds = carAppMonitorModule.filterAppIds();
        for (String appId : appIds) {
            paasAppProxy.searchAppInfo(appId);
        }
    }
}
