package com.ctrip.car.osd.notificationcenter.tracker;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.tracker.common.TrackerUtils;
import com.ctrip.car.osd.notificationcenter.tracker.components.*;
import com.ctrip.car.osd.notificationcenter.tracker.enums.TrackerType;
import com.dianping.cat.Cat;
import com.dtp.core.DtpRegistry;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author xiayx
 * @blame notification center
 * @since 2021/2/1
 * <p>
 * Composite+Singleton patterns
 */
public class TrackerUnite {
    /**
     * tracker threads pool
     */
    //private final static ExecutorService ThreadPool = ThreadPoolUtil.callerRunsPool("blueprint-track-%d", 10);
    private final static ExecutorService ThreadPool = DtpRegistry.getDtpExecutor("blueprint-track");

    /**
     * composite tracker - single trackinfo,default all tracker
     *
     * @param trackInfo
     * @return
     */
    public static boolean tracker(Map<String, String> trackInfo) {
        try {
            //filter and abandon trackinfo
            if (trackerFilters(trackInfo)) {
                return false;
            }

            //add tracker components
            List<TrackerAct> trackerActs = new ArrayList<>();
            trackerActs.add(CKTracker.getInstance());
            trackerActs.add(HickwallTracker.getInstance());
            trackerActs.add(UBTTracker.getInstance());
            trackerActs.add(AlertTracker.getInstance());

            //trackerActs.add(DemoTracker.getInstance());
            //trackerActs.add(TrackHickwall.getInstance());
            //trackerActs.add(TrackES.getInstance());

            trackRoute(trackInfo, trackerActs);
        } catch (Exception ex) {
            Cat.logError("TrackerUnite_tracker", ex.toString());
            return false;
        }
        return true;
    }

    /**
     * composite tracker - list trackinfo
     *
     * @param trackInfos
     * @return
     */
    public static boolean tracker(List<Map<String, String>> trackInfos) {
        boolean result = true;
        if (CollectionUtils.isNotEmpty(trackInfos)) {
            for (Map<String, String> trackInfo : trackInfos) {
                boolean trackResult = tracker(trackInfo);
                if (!trackResult) {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * composite tracker - single trackinfo,optional tracker
     *
     * @param trackInfo
     * @return
     */
    public static boolean tracker(Map<String, String> trackInfo, TrackerType... trackerTypes) {
        try {
            //add tracker components
            List<TrackerAct> trackerActs = new ArrayList<>();

            for (TrackerType trackerType : trackerTypes) {
                if (trackerType.equals(TrackerType.trackCK)) {
                    trackerActs.add(CKTracker.getInstance());
                }
                if (trackerType.equals(TrackerType.trackHickwall)) {
                    //trackerActs.add(TrackHickwall.getInstance());
                    trackerActs.add(HickwallTracker.getInstance());
                }
                if (trackerType.equals(TrackerType.trackUBT)) {
                    trackerActs.add(UBTTracker.getInstance());
                }
            }
            trackRoute(trackInfo, trackerActs);
        } catch (Exception ex) {
            Cat.logError("TrackerUnite_tracker", ex.toString());
            return false;
        }
        return true;
    }

    /**
     * single tracker - clickhouse
     *
     * @param trackInfo
     * @return
     */
    public static boolean trackerCK(Map<String, String> trackInfo) {
        //add tracker components
        List<TrackerAct> trackerActs = new ArrayList<>();
        trackerActs.add(CKTracker.getInstance());
        trackRoute(trackInfo, trackerActs);
        return true;
    }

    /**
     * execute trackers
     *
     * @param trackInfo
     * @param trackerActs or TrackerAct... trackerActs
     * @return
     */
    public static void trackRoute(Map<String, String> trackInfo, List<TrackerAct> trackerActs) {
        //clip track info fields
        TrackerUtils.adjustTrackInfo(trackInfo);

        //execute tracker
        for (TrackerAct trackerAct : trackerActs) {
            ThreadPool.submit(() -> {
                if (trackerAct.open(trackInfo) && trackerAct.pure(trackInfo)) {
                    try {
                        //Thread.sleep(10000);//multiple thread test
                        trackerAct.track(trackInfo);
                    } catch (Exception ex) {
                        Cat.logError(trackerAct.getClass().getSimpleName() + "_trackRoute", ex.toString());
                    } finally {
                        Cat.logEvent("TrackerUnite_trackRoute", trackerAct.getClass().getSimpleName());
                    }
                }
            });
        }
    }

    /**
     * tracker filters by Qconfig
     * ex: [{"appId":["100009572"],"eventType":["99999",""]},{"appId":["100025206"],"eventType":["52501",""]}]
     * empty string as no key or key value is blank
     *
     * @param trackInfo
     * @return
     */
    public static boolean trackerFilters(Map<String, String> trackInfo) {
        //JsonUtils.parseObject(trackFilterStr, typeRef)内存占用问题
        String trackFilterStr = QCAppsetting.get("Blueprint_TrackFilters");
        if (StringUtils.isBlank(trackFilterStr) || trackFilterStr.equals("[]")) {
            return false;
        }
        //filter - key,appid,eventType
        TypeReference<List<Map<String, List<String>>>> typeRef = new TypeReference<List<Map<String, List<String>>>>() {
        };
        List<Map<String, List<String>>> filters = JsonUtils.parseObject(trackFilterStr, typeRef);

        boolean hitFilter = false;
        for (Map<String, List<String>> filter : filters) {
            boolean hitItem = true;
            for (Map.Entry<String, List<String>> filterItem : filter.entrySet()) {
                boolean hitKeyVal = filterItem.getValue().contains(trackInfo.getOrDefault(filterItem.getKey(), ""));
                hitItem = hitItem & hitKeyVal;
            }
            hitFilter = hitFilter | hitItem;
        }
        return hitFilter;
    }

}
