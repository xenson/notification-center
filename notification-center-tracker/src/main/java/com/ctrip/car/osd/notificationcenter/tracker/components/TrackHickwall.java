package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.ctrip.car.osd.notificationcenter.basic.ObjectUtils;
import com.ctrip.car.osd.notificationcenter.config.QCRemoting;
import com.ctrip.car.osd.notificationcenter.tracker.enums.HickwallRecordType;
import com.ctrip.flight.intl.common.metric.MetricBuilder;
import com.ctrip.flight.intl.common.metric.Metrics;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author xiayx
 * @blame notification center
 * @since 2021/2/1
 * Metric-client: http://conf.ctripcorp.com/pages/viewpage.action?pageId=147166117
 * <p>
 * Composite+Singleton patterns
 */
@Deprecated
public class TrackHickwall extends TrackerAct {
    private static TrackHickwall instance;

    /**
     * Singleton
     *
     * @return
     */
    public static TrackHickwall getInstance() {
        if (instance == null) {
            instance = new TrackHickwall();
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
        List<String> hickwallIncludeKeys = QCRemoting.getList("Blueprint_HickwallIncludeKeys");
        if (hickwallIncludeKeys.contains(trackInfo.get("key"))) {
            return true;
        }
        return false;
    }

    /**
     * hickwall tracker action
     *
     * @param trackInfo
     */
    @Override
    public void track(Map<String, String> trackInfo) {
        List<String> hotspots = QCRemoting.getList("Blueprint_HickwallHotspots");
        List<String> hotspotTags = QCRemoting.getList("Blueprint_HickwallHotspotTags");

        for (String hotspot : hotspots) {
            //hotspot format: recordType_trackkey_trackkey_..._tracktarget
            List<String> spots = Arrays.asList(hotspot.split("_"));
            String record = spots.get(0);
            String target = spots.get(spots.size() - 1);
            boolean isSpot = true;
            List<String> spotVals = new ArrayList<>();

            for (String spot : spots.subList(1, spots.size())) {
                if (!trackInfo.containsKey(spot)) {
                    isSpot = false;
                    spotVals = new ArrayList<>();
                    break;
                } else {
                    spotVals.add(trackInfo.getOrDefault(spot, "0"));
                }
            }

            if (isSpot) {
                //hotspot name format: trackkey val_trackkey val..._tracktarget
                String hotspotName = StringUtils.join(spotVals.subList(0, spotVals.size() - 1), "_") + "_" + target;
                //default hotspot value
                Long hotspotValue = ObjectUtils.convertNumeric(spotVals.get(spotVals.size() - 1));

                //hickwall tags
                MetricBuilder mbTags = new MetricBuilder();
                Map<String, String> tags = new HashMap<>();
                if (hotspotTags.contains(target)) {
                    tags.put(target, ObjectUtils.boolToInt(spotVals.get(spotVals.size() - 1)));
                }
                mbTags.setTags(tags);

                if (record.equals(HickwallRecordType.recordOne.name())) {
                    //track use time - 埋点记录这个方法被调用了一次，以及这次调用使用的时间
                    Metrics.recordOne(hotspotName, hotspotValue, mbTags);
                } else if (record.equals(HickwallRecordType.recordSize.name())) {
                    if (hotspotValue.equals(Long.valueOf(0))) {
                        hotspotValue = ObjectUtils.convertNumeric(trackInfo.getOrDefault("resultSize", "0"));
                    }
                    //track result size - 埋点记录这次调用返回的成本中心的条数
                    Metrics.recordSize(hotspotName, hotspotValue, mbTags);
                } else if (record.equals(HickwallRecordType.addGauge.name())) {
                    //cache key count according to the time
                    Metrics.addGauge(hotspotName, () -> trackInfo.getOrDefault("cacheCount", "0"), mbTags);
                }
            }
        }
    }
}
