package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCClickhouse;
import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.tracker.common.Constants;
import com.ctrip.car.osd.notificationcenter.tracker.common.TrackerUtils;
import com.ctrip.car.osd.notificationcenter.analyzer.entity.HitFilterResult;
import com.ctrip.car.osd.notificationcenter.tracker.enums.TrackerFromEnum;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author xiayx
 * @blame notification center
 * @since 2021/2/1
 * <p>
 * Composite+Singleton patterns
 */
public class CKTracker extends TrackerAct {
    private static CKTracker instance;

    /**
     * Singleton
     *
     * @return
     */
    public static CKTracker getInstance() {
        if (instance == null) {
            instance = new CKTracker();
        }
        return instance;
    }

    /**
     * click house tracker switch
     *
     * @param trackInfo
     * @return
     */
    @Override
    public boolean open(Map<String, String> trackInfo) {
        if (!QCSwitch.get("Blueprint_OpenCK")) {
            return false;
        }
        List<String> esExcludeKeys = QCClickhouse.getList("Blueprint_CKExcludeKeys");
        if (esExcludeKeys.contains(trackInfo.get("key"))) {
            return false;
        }
        return true;
    }

    /**
     * click house tracker action
     *
     * @param trackInfo
     */
    @Override
    public void track(Map<String, String> trackInfo) {
        List<String> excludeStoreKeys = QCClickhouse.getList("Blueprint_ExcludeStoreKeys");
        Map<String, String> indexTags = new HashMap<>();
        Map<String, String> storeTags = new HashMap<>();

        List<HitFilterResult> hitResults = TrackerUtils.hitHickwallFilterRules(trackInfo);
        Boolean isDiscard = hitResults.stream().anyMatch(p -> p.getDiscard());
        if (isDiscard) {
            trackInfo.put("hitResult", JsonUtils.parseJson(hitResults));
            trackInfo.put("hitCount", hitResults.stream().mapToInt(HitFilterResult::getHitCount).sum() + "");
        }
        //多线程查询问题
//        List<HitFilterResult> hitResults1 = TrackerUtils.hitHickwallFilterRules(trackInfo);
//        Boolean isDiscard1 = hitResults.stream().anyMatch(p -> p.getDiscard());
//        List<HitFilterResult> hitResults2 = TrackerUtils.hitHickwallFilterRules(trackInfo);
//        Boolean isDiscard2 = hitResults.stream().anyMatch(p -> p.getDiscard());

        for (Iterator<Map.Entry<String, String>> trackItem = trackInfo.entrySet().iterator(); trackItem.hasNext(); ) {
            Map.Entry<String, String> entry = trackItem.next();
            if (excludeStoreKeys.contains(entry.getKey())) {
                continue;
            }

            //revise illegal field
            String entryKey = entry.getKey();
            if (entryKey.contains("-")) {
                entryKey = entryKey.replace("-", "_");
            }
            indexTags.put(entryKey, entry.getValue());
        }

        //scenarios distribute - default cluster trackAPI
        String keyFrom = indexTags.get("keyfrom");
        String disScenarioName = distributeScenario(keyFrom);
        if (StringUtils.isNotBlank(disScenarioName)) {
            Cat.logTags(disScenarioName, indexTags, storeTags);
            //trackSearching distribute to trackAPI (syn restful and shopping trackinfo) temp todo
            if (TrackerFromEnum.trackSearching.name().equals(keyFrom)) {
                Cat.logTags(distributeScenario("trackAPI"), indexTags, storeTags);
            }
        }

        //default redundancy cluster - all track
        Cat.logTags(Constants.Scenario_Default, indexTags, storeTags);
    }

    /**
     * scenario cluster distribution
     *
     * @param keyfrom
     * @return
     */
    private String distributeScenario(String keyfrom) {
        String disScenario = "";
        List<String> scenarioDistribute = QCClickhouse.getList("Blueprint_Scenario_Distribute");

        if (scenarioDistribute.contains(keyfrom.toLowerCase())) {
            disScenario = Constants.Scenario_Prefix + keyfrom.toLowerCase();
        } else if (CollectionUtils.isNotEmpty(scenarioDistribute)) {
            //default cluster trackAPI
            disScenario = Constants.Scenario_Prefix + scenarioDistribute.get(0);
        }
        return disScenario;
    }
}
