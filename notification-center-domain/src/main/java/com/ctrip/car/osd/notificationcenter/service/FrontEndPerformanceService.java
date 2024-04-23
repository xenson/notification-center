package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.basic.HttpUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.email.EmailSend;
import com.ctrip.car.osd.notificationcenter.entity.APMCRNCellEntity;
import com.ctrip.car.osd.notificationcenter.entity.APMCRNRootEntity;
import com.ctrip.car.osd.notificationcenter.entity.APMPerformanceEntity;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.car.osd.notificationcenter.tracker.TrackerUnite;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiayx on 2022/2/17.
 */
@Service
public class FrontEndPerformanceService {

    private String osgUri = "http://osg.ops.ctripcorp.com/api/19413";
    private String osgToken = "fd5e4756484e0feb20a76f4af54e521c";
    private static Map<String, String> OrganizationMaps = new HashMap<>();

    static {
        OrganizationMaps.put("36", "租车");
        OrganizationMaps.put("42", "门票");
        OrganizationMaps.put("43", "机票");
        OrganizationMaps.put("45", "酒店");
        OrganizationMaps.put("48", "火车票");
        OrganizationMaps.put("50", "金融平台");
        OrganizationMaps.put("51", "度假");
        OrganizationMaps.put("53", "内容信息");
        OrganizationMaps.put("56", "平台研发");
        OrganizationMaps.put("68", "汽车票");
        OrganizationMaps.put("76", "支付中心");
        OrganizationMaps.put("94", "用车");
        OrganizationMaps.put("unknown", "未知");
    }

    /**
     * all content send mail entry
     *
     * @return
     */
    public boolean sendAPMPerformanceMail(LocalDateTime endDate) {
        try {
            StringBuilder sbMailContent = new StringBuilder();
            sbMailContent.append(getAPMPerformanceMailContent(endDate));

            String recipients = QCAppsetting.get("APMMonitor_Recipients");
            String sender = QCAppsetting.get("APMMonitor_Sender");
            String cc = QCAppsetting.get("APMMonitor_CCs");
            String subject = QCAppsetting.get("APMMonitor_Subject");

            EmailSend.sendMail(recipients, recipients, sender, sender, cc, subject, sbMailContent.toString(), new ArrayList<>());
            return true;
        } catch (Exception ex) {
            LogUtils.error("sendAPMPerformanceMail", ex);
            return false;
        }
    }

    /**
     * get apm performance mail content
     *
     * @return
     */
    public String getAPMPerformanceMailContent(LocalDateTime endDate) {
        Integer dayScope = Integer.valueOf(QCAppsetting.get("APMMonitor_DayScope"));
        LocalDateTime today = ObjectUtils.defaultIfNull(endDate, LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        LocalDateTime fromDay = today.plusDays(dayScope);

        LocalDateTime startDateUTC = DateTimeUtils.localToUTC(fromDay);
        LocalDateTime endDateUTC = DateTimeUtils.localToUTC(today);
        String startTimeStr = DateTimeUtils.parseStr(startDateUTC, "yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        String endTimeStr = DateTimeUtils.parseStr(endDateUTC, "yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        String startDateStr = DateTimeUtils.parseStr(fromDay, "MM/dd");

        long startMS = DateTimeUtils.localDateTime2Timestamp(fromDay).getTime();
        long endMS = DateTimeUtils.localDateTime2Timestamp(today).getTime();

        List<String> versions = JsonUtils.parseObjectList(QCAppsetting.get("APMMonitor_CrnVesions"), String.class);
        String crnVersions = "\"" + StringUtils.join(versions, "\",\"") + "\"";

        StringBuilder sbContent = new StringBuilder();
        List<APMPerformanceEntity> pagePerfs = getPagePerformance(startTimeStr, endTimeStr, crnVersions);
        List<APMPerformanceEntity> crnPerfs = getCRNPerformance(startTimeStr, endTimeStr, crnVersions);

        if (CollectionUtils.isNotEmpty(pagePerfs)) {
            //页面秒开率
            sbContent.append("<h2>" + startDateStr + " 页面秒开率</h2>");
            List<APMPerformanceEntity> secList = pagePerfs.stream()
                    .sorted(Comparator.comparing(APMPerformanceEntity::getSecTimeRate).reversed())
                    .collect(Collectors.toList());

            sbContent.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"0\" style=\"background:white;border-collapse:collapse;width:1000px;\">");
            sbContent.append("<tr>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">BU名称</span></td>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">秒开率</span></td>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">样本量</span></td>");
            sbContent.append("</tr>");

            for (APMPerformanceEntity sec : secList) {
                sbContent.append("<tr>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + sec.getOrganizationName() + "</td>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + sec.getSecTimeRate() + "%</td>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + sec.getSampleCount() + "</td>");
                sbContent.append("</tr>");
            }
            sbContent.append("</table>");
            sbContent.append("<h4><a href=\"http://apm.site.ctripcorp.com/report/pagePerformance/CRN?interval=600&pageId=9a3fe7133a4f4b6d&startTime=" + startMS + "&endTime=" + endMS + "\">查看详情</a></h4>");

            //TTI
            sbContent.append("<h2>" + startDateStr + " TTI</h2>");
            List<APMPerformanceEntity> ttiList = pagePerfs.stream()
                    .sorted(Comparator.comparing(APMPerformanceEntity::getTotalTimeAvg))
                    .collect(Collectors.toList());

            sbContent.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"0\" style=\"background:white;border-collapse:collapse;width:1000px;\">");
            sbContent.append("<tr>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">BU名称</span></td>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">TTI</span></td>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">样本量</span></td>");
            sbContent.append("</tr>");

            for (APMPerformanceEntity tti : ttiList) {
                sbContent.append("<tr>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + tti.getOrganizationName() + "</td>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + tti.getTotalTimeAvg() + "s</td>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + tti.getSampleCount() + "</td>");
                sbContent.append("</tr>");
            }
            sbContent.append("</table>");

            //track to ck
            trackAPMPerformance(pagePerfs, 1, fromDay);
        }

        if (CollectionUtils.isNotEmpty(crnPerfs)) {
            sbContent.append("<h2>" + startDateStr + " 业务首屏首次render性能</h2>");
            List<APMPerformanceEntity> costList = crnPerfs.stream()
                    .sorted(Comparator.comparing(APMPerformanceEntity::getTotalTimeAvg))
                    .collect(Collectors.toList());

            sbContent.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"0\" style=\"background:white;border-collapse:collapse;width:1000px;\">");
            sbContent.append("<tr>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">BU名称</span></td>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">耗时</span></td>");
            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">样本量</span></td>");
            sbContent.append("</tr>");

            for (APMPerformanceEntity cost : costList) {
                sbContent.append("<tr>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + cost.getOrganizationName() + "</td>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + cost.getTotalTimeAvg() + "s</td>");
                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + cost.getSampleCount() + "</td>");
                sbContent.append("</tr>");
            }
            sbContent.append("</table>");
            sbContent.append("<h4><a href=\"http://apm.site.ctripcorp.com/report/crn?interval=600&pageId=6a1591fc26f31fbd&startTime=" + startMS + "&endTime=" + endMS + "\">查看详情</a></h4>");

            //track to ck
            trackAPMPerformance(crnPerfs, 2, fromDay);
        }

        //region 废弃=================
//        if (CollectionUtils.isNotEmpty(crnPerfs)) {
//            sbContent.append("<h2>" + startDate + " 页面秒开率</h2>");
//
//            sbContent.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"0\" style=\"background:white;border-collapse:collapse;width:1000px;\">");
//            sbContent.append("<tr>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">BU名称</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">增量耗时</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">成功率</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">耗时</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">样本量</span></td>");
//            sbContent.append("</tr>");
//
//            for (APMPerformanceEntity crnPerf : crnPerfs) {
//                sbContent.append("<tr>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + crnPerf.getOrganizationName() + "</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + crnPerf.getPkgLoadTimeAvg() + "s</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + crnPerf.getSuccessRate() + "%</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + crnPerf.getTotalTimeAvg() + "s</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + crnPerf.getSampleCount() + "</td>");
//                sbContent.append("</tr>");
//            }
//            sbContent.append("</table>");
//        }
//
//        if (CollectionUtils.isNotEmpty(pagePerfs)) {
//            sbContent.append("<h2>" + startDate + " Title2</h2>");
//
//            sbContent.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"0\" style=\"background:white;border-collapse:collapse;width:1000px;\">");
//            sbContent.append("<tr>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">BU名称</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">增量耗时</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">FCP</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">TTI</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">秒开率</span></td>");
//            sbContent.append("<td valign=top style=\"border:solid #555555;background:#555555;padding:2.25pt 2.25pt 2.25pt 2.25pt; \"><span style=\"color:white\">样本量</span></td>");
//            sbContent.append("</tr>");
//
//            for (APMPerformanceEntity pagePerf : pagePerfs) {
//                sbContent.append("<tr>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + pagePerf.getOrganizationName() + "</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + pagePerf.getPkgLoadTimeAvg() + "s</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + pagePerf.getFCPAvg() + "s</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + pagePerf.getTotalTimeAvg() + "s</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + pagePerf.getSecTimeRate() + "%</td>");
//                sbContent.append("<td valign=top style=\"border:solid #D4D4D4 1.0pt;border-top:none;padding:5.25pt 3.75pt 5.25pt 3.75pt\">" + pagePerf.getSampleCount() + "</td>");
//                sbContent.append("</tr>");
//            }
//            sbContent.append("</table>");
//        }
        //region 废弃=================

        return sbContent.toString();
    }

    /**
     * AMP performance data track to clickhouse
     *
     * @param perfDatas
     * @param type
     * @param dataDate
     */
    public void trackAPMPerformance(List<APMPerformanceEntity> perfDatas, Integer type, LocalDateTime dataDate) {
        String dataVersion = QCAppsetting.get("APMMonitor_DataVersion");
        if (type.equals(1)) {
            for (APMPerformanceEntity perfData : perfDatas) {
                Map<String, String> trackInfo = new HashMap<>();
                trackInfo.put("key", "o_page_render_check");
                trackInfo.put("keyfrom", "trackPage");

                trackInfo.put("buName", perfData.getOrganizationName());
                trackInfo.put("secTimeRate", perfData.getSecTimeRate().toString());
                trackInfo.put("sampleCount", perfData.getSampleCount().toString());
                trackInfo.put("totalTime", perfData.getTotalTimeAvg().toString());
                trackInfo.put("trackDate", DateTimeUtils.parseStr(dataDate));
                trackInfo.put("partialVersion", dataVersion);

                TrackerUnite.trackerCK(trackInfo);
            }
        } else if (type.equals(2)) {
            for (APMPerformanceEntity perfData : perfDatas) {
                Map<String, String> trackInfo = new HashMap<>();
                trackInfo.put("key", "crn_load_performance");
                trackInfo.put("keyfrom", "trackPage");

                trackInfo.put("buName", perfData.getOrganizationName());
                trackInfo.put("totalTime", perfData.getTotalTimeAvg().toString());
                trackInfo.put("sampleCount", perfData.getSampleCount().toString());
                trackInfo.put("trackDate", DateTimeUtils.parseStr(dataDate));
                trackInfo.put("partialVersion", dataVersion);

                TrackerUnite.trackerCK(trackInfo);
            }
        }
    }


    /**
     * CRN performance
     *
     * @param startTime
     * @param endTime
     * @param crnVersions
     * @return
     */
    public List<APMPerformanceEntity> getCRNPerformance(String startTime, String endTime, String crnVersions) {
        List<APMPerformanceEntity> buCrnPerfs = new ArrayList<>();

        StringBuilder sbCrnReq = new StringBuilder("{\"access_token\":\"" + osgToken + "\",\"request_body\":");
        sbCrnReq.append("{\"version\":1,\"namespace\":\"apm\",\"metric\":\"crn_load_performance\",");
        sbCrnReq.append("\"startTime\":\"" + startTime + "\",\"endTime\":\"" + endTime + "\",");
        sbCrnReq.append("\"columns\":[{\"alias\":\"organizationId\",\"field\":\"dimension.organizationId\"},");
        sbCrnReq.append("{\"alias\":\"pkgLoadTimeAvg\",\"avg\":\"measure.pkt\"},{\"alias\":\"failTime\",");
        sbCrnReq.append("\"count\":\"measure.failTime\"},{\"alias\":\"time\",\"count\":\"measure.time\"},{\"alias\":\"timeAvg\",");
        sbCrnReq.append("\"avg\":\"measure.time\"}],\"filters\":[{\"in\":{\"field\":\"dimension.appVer\",\"values\":");
        sbCrnReq.append("[" + crnVersions + "]}},{\"in\":{\"field\":\"dimension.appId\",\"values\":[\"99999999\"]}}],");
        sbCrnReq.append("\"groupBy\":{\"maxGroups\":20000},\"orderBy\":[]}}");

        String apiRes = HttpUtils.doPostProxy(osgUri, sbCrnReq.toString(), null);
        if (StringUtils.isNotBlank(apiRes)) {
            APMCRNRootEntity res = JsonUtils.parseObject(apiRes, APMCRNRootEntity.class);
            if (res != null && res.getStatus().equals(0) && CollectionUtils.isNotEmpty(res.getRows())) {
                for (APMCRNCellEntity cell : res.getRows()) {
                    APMPerformanceEntity buCrnPerf = new APMPerformanceEntity();

                    //BU id
                    if (cell.getCells().get(0).containsKey("blankVal") && cell.getCells().get(0).get("blankVal").equals("True")) {
                        buCrnPerf.setOrganizationId("");
                        buCrnPerf.setOrganizationName("");
                    } else {
                        buCrnPerf.setOrganizationId(cell.getCells().get(0).getOrDefault("stringVal", "0"));
                        buCrnPerf.setOrganizationName(mapOrganizationName(buCrnPerf.getOrganizationId()));
                    }

                    //增量耗时-s
                    BigDecimal pkgLoadTime = new BigDecimal(cell.getCells().get(1).getOrDefault("floatVal", "0"));
                    BigDecimal pkgLoadTimeScale = pkgLoadTime.divide(BigDecimal.valueOf(1000), 3, BigDecimal.ROUND_HALF_UP);
                    buCrnPerf.setPkgLoadTimeAvg(pkgLoadTimeScale);

                    //失败率=失败次数/样本量
                    Integer faileds = Integer.valueOf(cell.getCells().get(2).getOrDefault("intVal", "0"));
                    //样本量
                    Integer samples = Integer.valueOf(cell.getCells().get(3).getOrDefault("intVal", "0"));
                    buCrnPerf.setSampleCount(samples);
                    //成功率
                    BigDecimal successRate = BigDecimal.valueOf(1).subtract(
                                    BigDecimal.valueOf(faileds).divide(BigDecimal.valueOf(samples), 4, BigDecimal.ROUND_HALF_UP))
                            .multiply(BigDecimal.valueOf(100.00))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    buCrnPerf.setSuccessRate(successRate);

                    //耗时-s
                    BigDecimal cost = new BigDecimal(cell.getCells().get(4).getOrDefault("floatVal", "0"))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    buCrnPerf.setTotalTimeAvg(cost);

                    buCrnPerfs.add(buCrnPerf);
                }
            }
        } else {
            LogUtils.warn("getBUAPMPerformance", sbCrnReq.toString());
        }
        return buCrnPerfs;
    }

    /**
     * page render performance
     *
     * @param startTime
     * @param endTime
     * @param crnVersions
     * @return
     */
    public List<APMPerformanceEntity> getPagePerformance(String startTime, String endTime, String crnVersions) {
        List<APMPerformanceEntity> buPagePerfs = new ArrayList<>();

        StringBuilder sbCrnReq = new StringBuilder("{\"access_token\":\"" + osgToken + "\",\"request_body\":");
        sbCrnReq.append("{\"version\":1,\"namespace\":\"apm\",\"metric\":\"o_page_render_check\",");
        sbCrnReq.append("\"startTime\":\"" + startTime + "\",\"endTime\":\"" + endTime + "\",");
        sbCrnReq.append("\"columns\":[{\"alias\":\"organizationId\",\"field\":\"dimension.organizationId\"},");

        sbCrnReq.append("{\"alias\":\"totalTime\",\"count\":\"measure.totalTime\"},{\"alias\":\"totalTimeAvg\",\"avg\":\"measure.totalTime\"},");
        sbCrnReq.append("{\"alias\":\"pkgLoadTimeAvg\",\"avg\":\"measure.pkt\"},{\"alias\":\"FPCAvg\",\"avg\":\"measure.FCP\"},");
        sbCrnReq.append("{\"alias\":\"secTime\",\"count\":\"measure.secTime\"},{\"alias\":\"pageUsedMemoryAvg\",\"avg\":\"measure.pageUsedMemory\"}],");
        sbCrnReq.append("\"filters\":[{\"not\":{\"filter\":{\"exists\":{\"field\":\"dimension.errorMsg\"}}}},{\"in\":{\"field\":\"dimension.appVer\",\"values\":");
        sbCrnReq.append("[" + crnVersions + "]}},{\"in\":{\"field\":\"dimension.pageType\",\"values\":[\"CRN\"]}},{\"in\":{\"field\":\"dimension.appId\",");
        sbCrnReq.append("\"values\":[\"99999999\"]}}],\"groupBy\":{\"maxGroups\":20000},\"orderBy\":[]}}");

        String apiRes = HttpUtils.doPostProxy(osgUri, sbCrnReq.toString(), null);
        if (StringUtils.isNotBlank(apiRes)) {
            APMCRNRootEntity res = JsonUtils.parseObject(apiRes, APMCRNRootEntity.class);
            if (res != null && res.getStatus().equals(0) && CollectionUtils.isNotEmpty(res.getRows())) {
                for (APMCRNCellEntity cell : res.getRows()) {
                    APMPerformanceEntity buPagePerf = new APMPerformanceEntity();

                    //BU id
                    if (cell.getCells().get(0).containsKey("blankVal") && cell.getCells().get(0).get("blankVal").equals("True")) {
                        buPagePerf.setOrganizationId("");
                        buPagePerf.setOrganizationName("");
                    } else {
                        buPagePerf.setOrganizationId(cell.getCells().get(0).getOrDefault("stringVal", "0"));
                        buPagePerf.setOrganizationName(mapOrganizationName(buPagePerf.getOrganizationId()));
                    }

                    //样本量
                    Integer samples = Integer.valueOf(cell.getCells().get(1).getOrDefault("intVal", "0"));
                    buPagePerf.setSampleCount(samples);
                    //秒开量
                    Integer secTime = Integer.valueOf(cell.getCells().get(5).getOrDefault("intVal", "0"));
                    BigDecimal secTimeRate = BigDecimal.valueOf(secTime).divide(BigDecimal.valueOf(samples), 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    //秒开率
                    buPagePerf.setSecTimeRate(secTimeRate);

                    //TTI
                    BigDecimal tti = new BigDecimal(cell.getCells().get(2).getOrDefault("floatVal", "0"))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    buPagePerf.setTotalTimeAvg(tti);

                    //增量耗时-s
                    BigDecimal pkgLoadTime = new BigDecimal(cell.getCells().get(3).getOrDefault("floatVal", "0"));
                    BigDecimal pkgLoadTimeScale = pkgLoadTime.divide(BigDecimal.valueOf(1000), 3, BigDecimal.ROUND_HALF_UP);
                    buPagePerf.setPkgLoadTimeAvg(pkgLoadTimeScale);

                    //FPC
                    BigDecimal fcpAvg = new BigDecimal(cell.getCells().get(4).getOrDefault("floatVal", "0"))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    buPagePerf.setFCPAvg(fcpAvg);

                    buPagePerfs.add(buPagePerf);
                }
            }
        }
        return buPagePerfs;
    }

    public String mapOrganizationName(String buId) {
        return OrganizationMaps.getOrDefault(buId, "unknown");
    }

}
