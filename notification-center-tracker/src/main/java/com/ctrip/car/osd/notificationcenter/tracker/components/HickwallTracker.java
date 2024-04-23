package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.basic.ObjectUtils;
import com.ctrip.car.osd.notificationcenter.config.QCHickwall;
import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.tracker.common.GaugeSupplier;
import com.ctrip.car.osd.notificationcenter.tracker.common.TrackerUtils;
import com.ctrip.car.osd.notificationcenter.analyzer.entity.HitFilterResult;
import com.ctrip.car.osd.notificationcenter.tracker.enums.HickwallRecordType;
import com.ctrip.flight.intl.common.metric.MetricBuilder;
import com.ctrip.flight.intl.common.metric.Metrics;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiayx
 * @blame notification center
 * @since 2021/2/1
 * Metric-client: http://conf.ctripcorp.com/pages/viewpage.action?pageId=147166117
 * <p>
 * Composite+Singleton patterns
 */
public class HickwallTracker extends TrackerAct {
    private static HickwallTracker instance;

    /**
     * Singleton
     *
     * @return
     */
    public static HickwallTracker getInstance() {
        if (instance == null) {
            instance = new HickwallTracker();
        }
        return instance;
    }

    /**
     * hickwall tracker switch
     *
     * @param trackInfo
     * @return
     */
    @Override
    public boolean open(Map<String, String> trackInfo) {
        List<String> hickwallIncludeKeys = QCHickwall.getList("Blueprint_HickwallMetricsKeys");
        if (QCSwitch.get("Blueprint_OpenHickwallTracker") && hickwallIncludeKeys.contains(trackInfo.get("key"))) {
            return true;
        }
        return false;
    }

    /**
     * hickwall tracker action
     * <p>
     * 确定一类数据-trackkey: keyfrom,key,serviceName,apiName,pageId?
     * 度量值-metricField:
     * 性能-irrCost,networkCost,renderCost,pageReady,resultSize,processCost,cost
     * 稳定-isSuccess,eventResult,isFromCache,useCache,hasResult,result
     * 数据可分组(过滤)-metricTags: sourceFrom,appId,pageName,serviceName,apiName,distibutionChannelId?,resCode?,code?,serverErrorCode?
     * 对应关系: pageId(数量过大不可tag) 1->N sourceFrom
     *
     * @param trackInfo
     */
    @Override
    public void track(Map<String, String> trackInfo) {
        List<String> metrics = QCHickwall.getList("Blueprint_HickwallMetrics");

        for (String metric : metrics) {
            boolean hitSpot = true;
            //hotspot format: recordType_trackkey_trackkey_..._metricField
            List<String> spots = Arrays.asList(metric.split("_"));
            List<String> spotVals = new ArrayList<>();
            for (String spot : spots.subList(1, spots.size())) {
                if (!trackInfo.containsKey(spot)) {
                    hitSpot = false;
                    spotVals = new ArrayList<>();
                    break;
                } else {
                    spotVals.add(trackInfo.getOrDefault(spot, "0"));
                }
            }

            if (hitSpot) {
                //hotspot name format: trackkey val_trackkey val..._metricField
                String recordType = spots.get(0);
                String metricField = spots.get(spots.size() - 1);
                String metricName = StringUtils.join(spotVals.subList(0, spotVals.size() - 1), "_") + "_" + metricField;
                //default hotspot value
                Long metricValue = ObjectUtils.convertToLong(spotVals.get(spotVals.size() - 1));
                MetricBuilder metricTags = trackTags(trackInfo);

                if (recordType.equals(HickwallRecordType.recordOne.name())) {
                    //track use time - 埋点记录这个方法被调用了一次，以及这次调用使用的时间
                    Metrics.recordOne(metricName, metricValue, metricTags);

                } else if (recordType.equals(HickwallRecordType.recordSize.name())) {
                    //track result size - 埋点记录这次调用返回的成本中心的条数 - 原值:CAR.notificationcenter.key_listCout.size_rsum
                    Metrics.recordSize(metricName, metricValue, metricTags);

                } else if (recordType.equals(HickwallRecordType.addGauge.name())) {
                    //cache key count(or similar) according to the time - 单位时间(1分钟)累加值:CAR.notificationcenter.key_cacheCout.value_value
                    GaugeSupplier.setTrackInfo(metricValue, metricName);
                    Metrics.addGauge(metricName, new Supplier<Long>() {
                        @Override
                        public Long get() {
                            return GaugeSupplier.getTrackValue(metricName);
                        }
                    }, metricTags);

                }
            }
        }
    }

    /**
     * group tags of hickwall track
     *
     * @param trackInfo
     * @return
     */
    public MetricBuilder trackTags(Map<String, String> trackInfo) {
        List<String> metricsTags = QCHickwall.getList("Blueprint_HickwallMetricsTags");
        Map<String, String> metricsTagsScope = QCHickwall.getMap("Blueprint_HickwallMetricsTagsScope");
        List<String> fixTags = QCHickwall.getList("Blueprint_HickwallMetricsFixTags");

        //hickwall tags
        MetricBuilder mbTags = new MetricBuilder();
        Map<String, String> tags = new HashMap<>(8);
        for (String metricsTag : metricsTags) {
            String tagVal;
            if (trackInfo.containsKey(metricsTag)) {
                tagVal = trackInfo.get(metricsTag);
            } else {
                tagVal = "unknown";
            }

            if (metricsTagsScope.containsKey(metricsTag)) {
                String tagScopeRex = metricsTagsScope.get(metricsTag);
                Matcher m = Pattern.compile(tagScopeRex).matcher(tagVal);
                if (!m.matches()) {
                    tagVal = "unknown";
                }
            }

            if (fixTags.contains(metricsTag)) {
                tagVal = ObjectUtils.convertToTag(tagVal, "0");
            } else {
                tagVal = ObjectUtils.convertToTag(tagVal);
            }
            tags.put(metricsTag, tagVal);
        }

        if (!tags.isEmpty()) {
            mbTags.setTags(tags);
        }
        return mbTags;
    }

    @Override
    public boolean pure(Map<String, String> trackInfo) {
        List<HitFilterResult> hitResults = TrackerUtils.hitHickwallFilterRules(trackInfo);
        Boolean isDiscard = hitResults.stream().anyMatch(p -> p.getDiscard());
        return !isDiscard;
    }

}
