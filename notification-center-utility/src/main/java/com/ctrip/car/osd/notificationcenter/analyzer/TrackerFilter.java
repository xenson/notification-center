package com.ctrip.car.osd.notificationcenter.analyzer;

import cn.hutool.core.lang.Tuple;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.ctrip.car.osd.notificationcenter.analyzer.entity.HitFilterResult;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.basic.RedisUtil;

import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiayx on 2022/8/15.
 */
public class TrackerFilter {
    /**
     * check filter rule if hitting
     *
     * @param trackInfo
     * @param rule
     * @return
     */
    public static HitFilterResult hitFilterRule(Map<String, String> trackInfo, Map<String, String> rule) {
        HitFilterResult hitResult = new HitFilterResult();
        StringBuilder hitCacheKey = new StringBuilder();
        Map<String, String> mapHitKeyVal = new HashMap<>();
        Integer hitKeyCount = 0;
        Boolean isHitRule = false;

        for (Map.Entry<String, String> filter : rule.entrySet()) {
            String filterHitVal = trackInfo.getOrDefault(filter.getKey(), "");
            if (StringUtils.isBlank(filterHitVal)) {
                break;
            }
            //regular match filter value
            Pattern p = Pattern.compile(filter.getValue());
            if (p == null) {
                break;
            }
            Matcher m = p.matcher(filterHitVal);
            if (m.matches()) {
                hitKeyCount++;
                //cache key
                hitCacheKey.append(filter.getKey() + "#" + filterHitVal + "_");
                mapHitKeyVal.put(filter.getKey(), filterHitVal);
            }
        }
        if (rule.size() == hitKeyCount) {
            isHitRule = true;
        }

        hitResult.setHitRule(isHitRule);
        hitResult.setHitKeyCount(hitKeyCount);
        hitResult.setHitCacheKey(hitCacheKey.toString());
        hitResult.setHitKeyVal(JsonUtils.parseJson(mapHitKeyVal));
        return hitResult;
    }


    //redis cluster
    private static RedisUtil redisUtil = RedisUtil.getInstance("CarCache6");

    /**
     * if key duplicate request - by the limiting config
     *
     * @param key
     * @param limitingConf
     * @return
     */
    public static Tuple isDuplicateTrack(String key, List<Integer> limitingConf) {
        try {
            if (StringUtils.isBlank(key)) {
                return new Tuple(false, 0);
            }
            String keyCount = redisUtil.get(key);
            Integer incrCount = 0;
            if (StringUtils.isNotBlank(keyCount)) {
                incrCount = Integer.valueOf(keyCount);
            }
            incrCount++;

            redisUtil.setnofresh(key, incrCount.toString(), limitingConf.get(0));
            //if hit key - [时间s,次数]限制,hit count
            return new Tuple(incrCount > limitingConf.get(1), incrCount);
        } catch (Exception ex) {
            LogUtils.warn("TrackerFilter_isDuplicateTrack", ex);
            return new Tuple(false, 0);
        }
    }

    /**
     * filter trackinfo fields by query condition
     *
     * @param trackInfo
     * @param query
     * @return
     */
    public static Map<String, String> conditionQuery(Map<String, String> trackInfo, String query) {
        if (trackInfo == null || trackInfo.isEmpty()) {
            return null;
        }
        //解析配置查询SQL - Druid
        boolean hitCondition = true;
        String dbType = JdbcConstants.MYSQL;
        String sqlFormat = SQLUtils.format(query, dbType);
        List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sqlFormat, dbType);
        //默认为一条sql语句
        SQLStatement stmt = sqlStatementList.get(0);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        //解析是否命中条件
        List<TableStat.Condition> sqlConditions = visitor.getConditions();
        for (TableStat.Condition sqlCondition : sqlConditions) {
            String condColName = sqlCondition.getColumn().getName();
            List<Object> condColVals = sqlCondition.getValues();

            if (!trackInfo.containsKey(condColName)) {
                hitCondition = false;
                break;
            } else {
                if (!condColVals.contains(trackInfo.get(condColName))) {
                    hitCondition = false;
                    break;
                }
            }
        }

        //是否命中键值
        boolean allColumns = false;
        List<String> selectCols = new ArrayList<>();
        for (TableStat.Column column : visitor.getColumns()) {
            //select all fields
            if (column.getName().equals("*")) {
                allColumns = true;
            }
            selectCols.add(column.getName());
        }

        if (hitCondition) {
            //hit sql condition result map
            Map<String, String> queryContent = new HashMap<>(8);
            if (allColumns) {
                queryContent.putAll(trackInfo);
            } else {
                for (String col : selectCols) {
                    queryContent.put(col, trackInfo.getOrDefault(col, ""));
                }
            }
            return queryContent;
        } else {
            return null;
        }
    }

    /**
     * json message disassemble from json path to data map
     *
     * @param json
     * @param rootPath
     * @param jsonPath
     * @return
     */
    public static Map<String, String> jsonDisassembleMap(String json, String rootPath, String jsonPath) {
        Object rootObj;
        Map<String, String> disMap = null;
        try {
            if (StringUtils.isNotBlank(rootPath)) {
                rootObj = JsonPath.read(json, rootPath);
                if (rootObj != null) {
                    disMap = JsonUtils.parseObjMap(rootObj);
                }
            } else {
                disMap = JsonUtils.parseObjMap(json);
            }
            if (StringUtils.isBlank(jsonPath)) {
                return disMap;
            }

            List<String> jsonKeys = Arrays.asList(jsonPath.split("\\."));
            for (int index = 0; index < jsonKeys.size(); index++) {
                String key = jsonKeys.get(index);
                if (StringUtils.isBlank(key) || disMap == null || !disMap.containsKey(key)) {
                    return disMap;
                }
                Object nodeObj = disMap.get(key);
                disMap = JsonUtils.parseObjMap(nodeObj);
            }
            //LogUtils.info("TrackerFilter_jsonDisassembleMap", rootPath + "." + jsonPath + "\r\n" + disMap);
        } catch (Exception ex) {
            LogUtils.warn("TrackerFilter_jsonDisassembleMap", ex);
            return disMap;
        }
        return disMap;
    }

    /**
     * multiple condition disassemble from json root - evolution
     *
     * @param json
     * @param rootPaths
     * @return
     */
    public static Map<String, String> jsonPreDisassemble(String json, List<String> rootPaths) {
        Map<String, String> preDisMap = new HashMap<>();
        if (StringUtils.isBlank(json) || CollectionUtils.isEmpty(rootPaths)) {
            return null;
        }

        for (String root_path_key : rootPaths) {
            try {
                String[] rootpathkey = root_path_key.split("_");
                String rootPath = rootpathkey[0];
                String jsonPath = rootpathkey[1];
                String keyName = rootpathkey[2];

                Map<String, String> disMap = jsonDisassemble(json, rootPath, jsonPath, keyName);
                if (disMap != null) {
                    preDisMap.putAll(disMap);
                }
            } catch (Exception ex) {
                LogUtils.warn("TrackerFilter_jsonPreDisassemble", ex);
                continue;
            }
        }
        return preDisMap;
    }

    /**
     * condition disassemble from json root - evolution
     *
     * @param json
     * @param rootPath
     * @param jsonPath
     * @param keyName
     * @return
     */
    public static Map<String, String> jsonDisassemble(String json, String rootPath, String jsonPath, String keyName) {
        Object rootObj;
        Map<String, String> disMap = null;

        try {
            if (StringUtils.isNotBlank(rootPath)) {
                rootObj = JsonPath.read(json, rootPath);
                if (rootObj != null) {
                    disMap = JsonUtils.parseObjMap(rootObj);
                }
            } else {
                disMap = JsonUtils.parseObjMap(json);
            }
            if (StringUtils.isBlank(jsonPath)) {
                return disMap;
            }

            List<String> jsonKeys = Arrays.asList(jsonPath.split("\\."));
            for (int index = 0; index < jsonKeys.size(); index++) {
                String key = jsonKeys.get(index);
                if (StringUtils.isBlank(key) || disMap == null || !disMap.containsKey(key)) {
                    return disMap;
                }
                Object nodeObj = disMap.get(key);
                disMap = JsonUtils.parseObjMap(nodeObj, keyName);
            }
        } catch (Exception ex) {
            LogUtils.warn("TrackerFilter_jsonDisassemble", ex);
            return disMap;
        }
        //LogUtils.info("TrackerFilter_jsonDisassemble", rootPath + "_" + jsonPath + "_" + keyName + "\r\n" + disMap);
        return disMap;
    }

}