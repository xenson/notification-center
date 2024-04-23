package com.ctrip.car.osd.notificationcenter.kafka;

import com.ctrip.car.osd.notificationcenter.analyzer.TrackerFilter;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiayx on 2022/8/3.
 */
public class MqDisassemble {
    /**
     * Kafka消费解析规则 - 字段匹配,落ck表
     *
     * @param mqMsg
     * @param fromTopic
     * @return
     */
    public static Boolean disassembleKafkaMq(String mqMsg, String fromTopic) {
        List<KafkaConditionEntity> topicConditions = QCKafkaConditions.get().stream().filter(p -> p.getTopic().equals(fromTopic)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(topicConditions)) {
            return false;
        }

        for (KafkaConditionEntity condition : topicConditions) {
            Map<String, String> trackAll = new HashMap<>();

            Map<String, String> trackPreInfo = TrackerFilter.jsonPreDisassemble(mqMsg, condition.getPrekeys());
            if (trackPreInfo != null && !trackPreInfo.isEmpty()) {
                if (!isKeyWhiteList(trackPreInfo.getOrDefault("customKey", ""), condition.getTrustkeys())) {
                    //temp key name 'customKey'
                    return false;
                } else {
                    trackAll.putAll(trackPreInfo);
                }
            }

            Map<String, String> trackInfo = TrackerFilter.jsonDisassembleMap(mqMsg, condition.getRoot(), condition.getPath());
            if (trackInfo != null && !trackInfo.isEmpty()) {
                trackAll.putAll(trackInfo);
            }

            if (trackAll != null && !trackAll.isEmpty()) {
                Map<String, String> trackCondition = TrackerFilter.conditionQuery(trackAll, condition.getQuery());
                if (trackCondition == null || trackCondition.isEmpty()) {
                    return false;
                }
                //map value force to string
                Map<String, String> indexTags = new HashMap<>();
                for (Map.Entry trackEntry : trackCondition.entrySet()) {
                    if (trackEntry.getValue() == null || trackEntry.getKey() == null) {
                        continue;
                    }
                    indexTags.put(trackEntry.getKey().toString(), trackEntry.getValue().toString());
                }

                if (StringUtils.isNotBlank(condition.getTarget())) {
                    Cat.logTags(condition.getTarget(), indexTags, new HashMap<>());
                } else {
                    //default redundancy cluster - all track
                    Cat.logTags("car-rental-ubtdevtrace", indexTags, new HashMap<>());
                }

                if (QCSwitch.get("KafkaConsumer_CatLog")) {
                    //记录成功解析的消息及解析条件
                    Map<String, String> logTags = new HashMap<>();
                    logTags.put("msg", mqMsg);
                    logTags.put("isSuccess", "1");
                    logTags.put("fromTopic", fromTopic);
                    logTags.put("condition", JsonUtils.parseJson(condition));
                    logTags.put("method", "disassembleKafkaMq");
                    logTags.put("trackPreInfo", JsonUtils.parseJson(trackPreInfo));
                    logTags.put("trackInfo", JsonUtils.parseJson(trackInfo));
                    logTags.put("indexTags", JsonUtils.parseJson(indexTags));
                    logTags.put("customKey", trackAll.getOrDefault("customKey", "no"));
                    Cat.logTags("car-rental-ubtdevtrace", logTags, new HashMap<>());
                }
            } else {
                if (QCSwitch.get("KafkaConsumer_CatLog")) {
                    //记录不能解析的消息及解析条件
                    Map<String, String> logTags = new HashMap<>();
                    logTags.put("msg", mqMsg);
                    logTags.put("isSuccess", "0");
                    logTags.put("fromTopic", fromTopic);
                    logTags.put("condition", JsonUtils.parseJson(condition));
                    logTags.put("method", "disassembleKafkaMq");
                    logTags.put("trackPreInfo", JsonUtils.parseJson(trackPreInfo));
                    logTags.put("trackInfo", JsonUtils.parseJson(trackInfo));
                    logTags.put("indexTags", "no");
                    logTags.put("customKey", trackAll.getOrDefault("customKey", "no"));
                    Cat.logTags("car-rental-ubtdevtrace", logTags, new HashMap<>());
                }
            }
        }
        return true;
    }

    //解析key前置过滤,不在白名单列表中key不解析
    public static Boolean isKeyWhiteList(String customKey, Set<String> trustKeys) {
        if (trustKeys == null || trustKeys.isEmpty()) {
            return true;
        }
        if (StringUtils.isBlank(customKey)) {
            return false;
        }
        return trustKeys.stream().anyMatch(p -> customKey.matches(p));
    }

}