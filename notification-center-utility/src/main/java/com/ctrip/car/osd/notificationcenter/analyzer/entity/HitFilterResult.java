package com.ctrip.car.osd.notificationcenter.analyzer.entity;

import java.util.Map;
import java.util.Set;

/**
 * Created by xiayx on 2022/7/7.
 */
public class HitFilterResult {
    /**
     * 源数据是否需要丢弃
     */
    private Boolean isDiscard;
    /**
     * 是否命中过滤规则
     */
    private Boolean isHitRule;
    /**
     * 命中次数
     */
    private Integer hitCount;
    /**
     * 规则Id
     */
    private String ruleId;
    /**
     * 命中缓存Key
     */
    private String hitCacheKey;

    /**
     * 规则类型 TrackerFilterType
     */
    private String ruleType;
    /**
     * 规则配置详情
     */
    private String ruleDetail;
    /**
     * 过滤字段命中个数
     */
    private Integer hitKeyCount;
    /**
     * 过滤字段命中键值
     */
    private String hitKeyVal;


    public Boolean getDiscard() {
        return isDiscard;
    }

    public void setDiscard(Boolean discard) {
        isDiscard = discard;
    }

    public Boolean getHitRule() {
        return isHitRule;
    }

    public void setHitRule(Boolean hitRule) {
        isHitRule = hitRule;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getHitCacheKey() {
        return hitCacheKey;
    }

    public void setHitCacheKey(String hitCacheKey) {
        this.hitCacheKey = hitCacheKey;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public Integer getHitKeyCount() {
        return hitKeyCount;
    }

    public void setHitKeyCount(Integer hitKeyCount) {
        this.hitKeyCount = hitKeyCount;
    }

    public String getHitKeyVal() {
        return hitKeyVal;
    }

    public void setHitKeyVal(String hitKeyVal) {
        this.hitKeyVal = hitKeyVal;
    }
}
