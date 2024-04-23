package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
//import com.ctrip.car.osd.notificationcenter.alert.AlertConditionEntity;
//import com.ctrip.car.osd.notificationcenter.alert.QCAlertConditions;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
//import com.ctrip.car.osd.notificationcenter.qmq.QMQProxy;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2021/12/31.
 */
public class AlertTracker extends TrackerAct {
    private static AlertTracker instance;

    /**
     * Singleton
     *
     * @return
     */
    public static AlertTracker getInstance() {
        if (instance == null) {
            instance = new AlertTracker();
        }
        return instance;
    }

    @Override
    public boolean open(Map<String, String> trackInfo) {
        if (!QCSwitch.get("Blueprint_OpenAlert")) {
            return false;
        }
        return true;
    }

    @Override
    public void track(Map<String, String> trackInfo) {
//        List<AlertConditionEntity> conditions = QCAlertConditions.get();
//        if (CollectionUtils.isEmpty(conditions)) {
//            return;
//        }
//
//        for (AlertConditionEntity condition : conditions) {
//            //解析配置查询SQL - Druid
//            boolean sendMsg = true;
//            String dbType = JdbcConstants.MYSQL;
//            String sqlFormat = SQLUtils.format(condition.getQuery(), dbType);
//            List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sqlFormat, dbType);
//            //默认为一条sql语句
//            SQLStatement stmt = sqlStatementList.get(0);
//            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
//            stmt.accept(visitor);
//
//            //解析是否命中条件
//            List<TableStat.Condition> sqlConditions = visitor.getConditions();
//            for (TableStat.Condition sqlCondition : sqlConditions) {
//                String condColName = sqlCondition.getColumn().getName();
//                List<Object> condColVals = sqlCondition.getValues();
//
//                if (!trackInfo.containsKey(condColName)) {
//                    sendMsg = false;
//                    break;
//                } else {
//                    if (!condColVals.contains(trackInfo.get(condColName))) {
//                        sendMsg = false;
//                        break;
//                    }
//                }
//            }
//
//            //是否命中键值
//            boolean allColumns = false;
//            List<String> selectCols = new ArrayList<>();
//            for (TableStat.Column column : visitor.getColumns()) {
//                if (column.getName().equals("*")) {
//                    allColumns = true;
//                }
//                selectCols.add(column.getName());
//            }
//
//            //QMQ消息推送 - 消息名称,消息所需字段(来源SQL)
//            if (sendMsg) {
//                Map<String, String> msgContent = new HashMap<>(8);
//                if (allColumns) {
//                    msgContent.putAll(trackInfo);
//                } else {
//                    for (String col : selectCols) {
//                        msgContent.put(col, trackInfo.getOrDefault(col, ""));
//                    }
//                }
//                msgContent.put("title", condition.getTitle());
//
//                Map<String, String> qmqMessage = new HashMap<>();
//                qmqMessage.put(condition.getQmq_key(), JsonUtils.parseJson(msgContent));
//                QMQProxy.sendMessage(condition.getQmq_subject(), qmqMessage);
//            }
//        }
//
//        //是否去重 - Redis
//
//        //消息队列处理 - 累积,延时...
    }

}
