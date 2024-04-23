package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.config.QCSwitch;
import com.ctrip.car.osd.notificationcenter.config.QCUbt;
import com.ctrip.car.osd.notificationcenter.tracker.common.Constants;
import com.ctrip.car.osd.notificationcenter.tracker.common.TrackerUtils;
import com.ctrip.tech.ubt.servertrace.sdk.collector.ServerEventCollector;
import com.ctrip.tech.ubt.servertrace.sdk.domain.ServerKey;
import com.dianping.cat.Cat;
import hermes.ubt.server.custom.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xiayx on 2021/2/5.
 * <p>
 * 接口声明：public void collectTrace(ServerKey key, ServerCustomEvent event);
 * <p>
 * 1.	ServerKey对象的属性都是必填的。当前包含如下属性：
 * private String orgId;   此埋点所属的组织BUID，可从cms/系统查询到
 * private String keyName; 申请注册的埋点key名称
 * private String appId;   采集埋点数据所属的应用ID，可从cms/系统查询到
 * private String serviceCode;   应用服务号，可以是接口名称、soa方法名称或者服务名称等标识采集埋点数据的具体服务方法
 * <p>
 * 2.	ServerCustomEvent对象包括三部分：DefaultEventMeta，ServerContext和Custom。
 * 详细属性解析参考：http://portal.ubt.ctripcorp.com/sites/schema/latest/ - ServerCustomEvent
 * DefaultEventMeta对象中的属性，可以不用填写。
 * ServerContext对象中的属性，appId和serviceCode属性必填。
 * Custom对象中的属性，是业务方具体的埋点内容，keyName，data属性必填。
 * <p>
 * 3.	ServerKey和ServerCustomEvent对象中相同的属性，其值会已ServerKey中的为标准。
 */
public class UBTTracker extends TrackerAct {
    /**
     * server trace sdk singleton
     */
    private static ServerEventCollector ServerTraceInstance = ServerEventCollector.getInstance();

    public static UBTTracker instance;

    /**
     * Singleton
     *
     * @return
     */
    public static UBTTracker getInstance() {
        if (instance == null) {
            instance = new UBTTracker();
        }
        return instance;
    }

    /**
     * UBT tracker switch
     *
     * @param trackInfo
     * @return
     */
    @Override
    public boolean open(Map<String, String> trackInfo) {
        List<String> hiveExcludeKeys = QCUbt.getList("Blueprint_HiveExcludeKeys");
        if (hiveExcludeKeys.contains(trackInfo.get("key"))) {
            return false;
        }
        return true;
    }

    /**
     * UBT tracker action
     *
     * @param trackInfo
     */
    @Override
    public void track(Map<String, String> trackInfo) {
        try {
            Boolean openKeycontent = QCSwitch.get("Blueprint_OpenUBTkeycontent");

            HashMap<String, String> ubtTrackInfo = new HashMap<>();
            String appId = trackInfo.getOrDefault("appId", Constants.AppId);
            String key = trackInfo.getOrDefault("key", Constants.TackUBTKey);
            Integer keyId = Integer.valueOf(trackInfo.getOrDefault("keyId", Constants.TackUBTKeyId));
            String keyName = trackInfo.getOrDefault("keyName", key);

            if (openKeycontent) {
                //clip track info fields-map deep copy
                ubtTrackInfo.putAll(trackInfo);
                TrackerUtils.adjustUBTTrackInfo(ubtTrackInfo);

                key = ubtTrackInfo.get("key");
                keyId = Integer.valueOf(ubtTrackInfo.get("keyId"));
                keyName = ubtTrackInfo.get("keyName");
            }

            ServerKey serverKey = new ServerKey();
            serverKey.setAppId(appId);
            serverKey.setServiceCode(Constants.ServiceCode);
            serverKey.setKeyName(keyName);
            serverKey.setOrgId(Constants.OrganizationId);

            if (openKeycontent) {
                ServerCustomEvent event = buildServerCustomEvent(ubtTrackInfo, key, keyId);
                ServerTraceInstance.collectTrace(serverKey, event);
            } else {
                ServerCustomEvent event = buildServerCustomEvent(trackInfo, key, keyId);
                ServerTraceInstance.collectTrace(serverKey, event);
            }
        } catch (Exception ex) {
            Cat.logError("TrackUBT_track", ex);
        }
    }

    private static ServerCustomEvent buildServerCustomEvent(Map<String, String> trackInfos, String ubtKey, Integer ubtKeyId) {
        ServerCustomEvent.Builder builder = ServerCustomEvent.newBuilder();
        builder.setMeta(buildEventMeta(trackInfos));
        builder.setContext(buildContextTranslator(trackInfos));
        builder.setCustom(buildCustomTranslator(trackInfos, ubtKey, ubtKeyId));
        return builder.build();
    }

    private static DefaultEventMeta buildEventMeta(Map<String, String> trackInfos) {
        String id = UUID.randomUUID().toString();
        long sendTime = System.currentTimeMillis();
        long receiveTime = System.currentTimeMillis();
        String userAccount = "";
        EventType eventType = EventType.Custom;
        int version = 1;
        String sender = "";
        String receiver = "";
        long sequenceNum = 0;
        return new DefaultEventMeta(id, sendTime, receiveTime, userAccount, eventType, version, sender, receiver, sequenceNum);
    }

    private static ServerContext buildContextTranslator(Map<String, String> trackInfos) {
        String tid = "";
        String serverIp = "";
        String clientIp = "";
        long ts = 0;
        String requestId = trackInfos.getOrDefault("requestId", "");
        String organizationId = "36";
        Map<CharSequence, CharSequence> serverMeta = new HashMap<>();
        return new ServerContext(Constants.AppId, Constants.ServiceCode, tid, serverIp, clientIp, ts, requestId, organizationId, serverMeta);
    }


    private static Custom buildCustomTranslator(Map<String, String> trackInfos, String ubtKey, Integer ubtKeyId) {
        Map<CharSequence, CharSequence> customMap = getLogMapInCharSequence(trackInfos);
        return new Custom(ubtKey, customMap, ubtKeyId);
    }

    private static Map<CharSequence, CharSequence> getLogMapInCharSequence(Map<String, String> trackInfos) {
        Map<CharSequence, CharSequence> customMap = new HashMap<CharSequence, CharSequence>();
        customMap.put("logServerTime", DateTimeUtils.dateNowStr());

        if (trackInfos != null && !trackInfos.isEmpty()) {
            customMap.putAll(trackInfos);
        }
        return customMap;
    }

}
