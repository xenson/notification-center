package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.ctrip.car.osd.notificationcenter.config.QCRemoting;
import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.tracker.common.Constants;
import com.dianping.cat.Cat;
import org.apache.http.annotation.Obsolete;

import java.util.*;

/**
 * Created by xiayx on 2021/2/4.
 */
@Deprecated
public class TrackES extends TrackerAct {
    private static TrackES instance;

    /**
     * Singleton
     *
     * @return
     */
    public static TrackES getInstance() {
        if (instance == null) {
            instance = new TrackES();
        }
        return instance;
    }

    /**
     * es tracker switch
     *
     * @param trackInfo
     * @return
     */
    @Override
    public boolean open(Map<String, String> trackInfo) {
        if (QCSwitch.get("Blueprint_OpenCK")) {
            return false;
        }
        List<String> esExcludeKeys = QCRemoting.getList("Blueprint_ESExcludeKeys");
        if (esExcludeKeys.contains(trackInfo.get("key"))) {
            return false;
        }
        return true;
    }

    /**
     * es tracker action
     *
     * @param trackInfo
     */
    @Override
    public void track(Map<String, String> trackInfo) {
        List<String> esIndexKeys = QCRemoting.getList("Blueprint_IndexKeys");
        List<String> esExcludeStoreKeys = QCRemoting.getList("Blueprint_ExcludeStoreKeys");

        Map<String, String> indexTags = new HashMap<>();
        Map<String, String> storeTags = new HashMap<>();
        //customer specify es index by request
        List<String> esCustomIndexKeys = Arrays.asList(trackInfo.getOrDefault("esIndexKeys", "").split(";"));

        for (Iterator<Map.Entry<String, String>> trackItem = trackInfo.entrySet().iterator(); trackItem.hasNext(); ) {
            Map.Entry<String, String> entry = trackItem.next();
            if (esIndexKeys.contains(entry.getKey()) || esCustomIndexKeys.contains(entry.getKey())) {
                indexTags.put(entry.getKey(), entry.getValue());
            } else if (!esExcludeStoreKeys.contains(entry.getKey())) {
                storeTags.put(entry.getKey(), entry.getValue());
            }
        }
        //combine keys only store in indextags
        indexTags.putAll(storeTags);
        Cat.logTags(Constants.Scenario_Default, indexTags, new HashMap<>());
    }
}
