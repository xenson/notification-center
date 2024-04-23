package com.ctrip.car.osd.notificationcenter.tracker;

import com.ctrip.car.osd.notificationcenter.tracker.components.TrackES;
import com.ctrip.car.osd.notificationcenter.tracker.components.TrackerAct;
import com.ctrip.car.osd.notificationcenter.tracker.components.CKTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2021/2/1.
 */
public class TrackUniteTest {

    public static void main(String[] args) {
        try {
            Map<String, String> trackInfos = new HashMap<>();
            trackInfos.put("key", "test");

            List<TrackerAct> trackerActs = new ArrayList<>();
            trackerActs.add(CKTracker.getInstance());
            trackerActs.add(TrackES.getInstance());

            TrackerUnite.trackRoute(trackInfos, trackerActs);
        } catch (Exception ex) {

        }
    }
}
