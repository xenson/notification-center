package com.ctrip.car.osd.notificationcenter.tracker.common;

import cn.hutool.core.lang.Tuple;
import com.ctrip.car.osd.notificationcenter.analyzer.TrackerFilter;
import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCHickwall;
import com.ctrip.car.osd.notificationcenter.config.QCKeyContent;
import com.ctrip.car.osd.notificationcenter.analyzer.entity.HitFilterResult;
import com.ctrip.car.osd.notificationcenter.tracker.entity.KeyConfigEntity;
import com.ctrip.car.osd.notificationcenter.tracker.enums.TrackerFilterType;
import com.ctrip.car.osd.notificationcenter.tracker.enums.TrackerFromEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2021/3/5.
 */
public class TrackerUtils {
    /**
     * clip track info fields
     *
     * @param trackInfo
     */
    public static void adjustTrackInfo(Map<String, String> trackInfo) {
        //reform key
        trackInfo.put("title", Constants.Scenario_Title);
        if (!trackInfo.containsKey("appId")) {
            trackInfo.put("appId", Constants.AppId);
        }
        if (!trackInfo.containsKey("timeStamp")) {
            trackInfo.put("timeStamp", DateTimeUtils.dateNowStr());
        }
        //默认cost为基本性能埋点-无论有无值-防止无度量字段导致无Hickwall埋点问题
        if (!trackInfo.containsKey("cost")) {
            trackInfo.put("cost", "0");
        }
        //默认分表-防止CK分表异常
        if (!trackInfo.containsKey("keyfrom")) {
            trackInfo.put("keyfrom", TrackerFromEnum.trackAPI.name());
        }
    }

    /**
     * clip track info fields
     *
     * @param trackInfo
     */
    public static void adjustUBTTrackInfo(Map<String, String> trackInfo) {
        KeyConfigEntity keyConfig = getKeyConfig(trackInfo.get("key"), trackInfo.get("keyId"), trackInfo.get("keyName"));
        if (keyConfig != null) {
            trackInfo.put("key", keyConfig.getKey());
            trackInfo.put("keyId", keyConfig.getKeyId());
            trackInfo.put("keyName", keyConfig.getKeyName());
        } else {
            String key = trackInfo.getOrDefault("key", Constants.TackUBTKey);
            Integer keyId = Integer.valueOf(trackInfo.getOrDefault("keyId", Constants.TackUBTKeyId));
            String keyName = trackInfo.getOrDefault("keyName", key);
            trackInfo.put("key", key);
            trackInfo.put("keyId", keyId.toString());
            trackInfo.put("keyName", keyName);
        }
    }

    /**
     * fixed key/keyId config
     *
     * @param key
     * @param keyId
     * @param keyName
     * @return
     */
    public static KeyConfigEntity getKeyConfig(String key, String keyId, String keyName) {
        List<KeyConfigEntity> keyConfigs = JsonUtils.parseObjectList(QCKeyContent.getKeyContents(), KeyConfigEntity.class);
        if (CollectionUtils.isEmpty(keyConfigs)) {
            return null;
        }

        KeyConfigEntity keyConfig = keyConfigs.stream().filter(p -> p.getKeyId().equals(key)
                || p.getKeyId().equals(keyId)
                || p.getKeyName().equals(keyName)).findFirst().orElse(null);

        return keyConfig;
    }

    /**
     * check Hickwall filter rules if hitting
     *
     * @param trackInfo
     * @return
     */
    public static List<HitFilterResult> hitHickwallFilterRules(Map<String, String> trackInfo) {
        List<HitFilterResult> hitResults = new ArrayList<>();
        List<Map<String, String>> rules = QCHickwall.getMapList("Blueprint_HickwallFilter");

        for (Map<String, String> rule : rules) {
            TrackerFilterType ruleType = TrackerFilterType.convert(rule.getOrDefault("Hickwall_FilterType", "1"));
            rule.remove("Hickwall_FilterType");
            String ruleId = rule.getOrDefault("Hickwall_FilterType", "1");
            rule.remove("Hickwall_RuleId");

            HitFilterResult hitResult = TrackerFilter.hitFilterRule(trackInfo, rule);
            hitResult.setRuleId(ruleId);
            hitResult.setRuleType(ruleType.name());
            hitResult.setRuleDetail(JsonUtils.parseJson(rule));
            //initial pure parameters
            hitResult.setDiscard(false);
            hitResult.setHitCount(0);

            switch (ruleType) {
                case duplication:
                    if (hitResult.getHitRule()) {
                        List<Integer> limitingConf = QCHickwall.getIntList("Blueprint_HickwallFilterLimiting");
                        Tuple hitDuplicateRes = TrackerFilter.isDuplicateTrack(hitResult.getHitCacheKey(), limitingConf);
                        hitResult.setDiscard(hitDuplicateRes.get(0));
                        hitResult.setHitCount(hitDuplicateRes.get(1));
                    }
                    break;
                case filtering:
                    hitResult.setDiscard(hitResult.getHitRule());
                    hitResult.setHitCount(1);
                    break;
                default:
                    break;
            }
            hitResults.add(hitResult);
        }
//        System.out.println("=========================================");
//        System.out.println(Thread.currentThread().getName() + "__" + JsonUtils.parseJson(hitResults));
//        System.out.println("=========================================");
        return hitResults;
    }


}
