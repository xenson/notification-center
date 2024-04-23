package com.ctrip.car.osd.notificationcenter.tracker.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xiayx on 2022/1/24.
 */
public class GaugeSupplier {
    private static ConcurrentHashMap<String, List<Long>> TrackVals = new ConcurrentHashMap<>();
    private static AtomicLong AtomLng = new AtomicLong(0);

    /**
     * push queue
     *
     * @param metricValue
     * @param metricName
     */
    public static void setTrackInfo(Long metricValue, String metricName) {
        if (TrackVals.containsKey(metricName)) {
            TrackVals.get(metricName).add(metricValue);
        } else {
            List<Long> metricVals = new ArrayList<Long>() {{
                add(metricValue);
            }};
            TrackVals.put(metricName, metricVals);
        }
    }

    /**
     * get queue
     *
     * @return
     */
    public static Long getTrackValue(String metricName) {
        if (TrackVals.containsKey(metricName)) {
            Long metricSum = TrackVals.get(metricName).stream().reduce(0L, Long::sum);
            //get queue once in unit time and clear value
            AtomLng.set(metricSum);
            TrackVals.remove(metricName);
        }
        return AtomLng.getAndSet(0);
    }

//    public static Long getTrackValueTest(String metricVal) {
//        System.out.println(metricVal + "__" + metricVal.hashCode() + "--");
//        Long val = ObjectUtils.convertToLong(metricVal);
//        System.out.println(val + "__" + val.hashCode() + "--");
//        return val;
//    }
//
//    public static Long getTrackValueTest(Long metricVal) {
//        System.out.println(metricVal + "__" + metricVal.hashCode() + "--");
//        return metricVal;
//    }

//    private static List<Map<String, String>> TrackInfos = new ArrayList<>();
//    private static AtomicInteger AtomInt = new AtomicInteger(0);
//
//    public static void setTrackInfo(Map<String, String> trackInfo, String metricField) {
//        Map<String, String> metricInfo = new HashMap<>();
//        metricInfo.putAll(trackInfo);
//        metricInfo.put("Metric_Field", metricField);
//        TrackInfos.add(metricInfo);
//    }
//
//    public static Integer getTrackValue() {
//        if (CollectionUtils.isNotEmpty(TrackInfos)) {
//            Integer metricSum = 0;
//            for (Map<String, String> trackInfo : TrackInfos) {
//                String metricField = trackInfo.get("Metric_Field");
//                metricSum += ObjectUtils.convertToLong(trackInfo.getOrDefault(metricField, "0")).intValue();
//            }
//            AtomInt.set(metricSum);
//            TrackInfos = new ArrayList<>();
//        }
//        return AtomInt.getAndSet(0);
//    }
}
