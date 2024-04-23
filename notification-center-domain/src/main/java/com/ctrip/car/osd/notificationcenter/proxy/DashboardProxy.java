package com.ctrip.car.osd.notificationcenter.proxy;

import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.basic.HttpUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.entity.DashboardRootEntity;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by xiayx on 2021/10/15.
 */
public class DashboardProxy {

    public static DashboardRootEntity searchDashboardMeric(String metricName, String appIds, Timestamp start,
                                                           Timestamp end, String aggregator, String groupKeys) {
        String uri = "http://engine.dashboard.fx.ctripcorp.com/data";
        String paramsFormat = "?env=PROD&interval=1h&metric-name=%s&tags={appid:[%s]}&start-time=%s&end-time=%s&aggregator=%s&downsampler=%s&group-by=[%s]";

        String startTime = DateTimeUtils.parseStr(start, "yyyy-MM-dd HH:mm:ss");
        String endTime = DateTimeUtils.parseStr(end, "yyyy-MM-dd HH:mm:ss");

        try {
            String reqParams = String.format(paramsFormat, metricName, appIds, URLEncoder.encode(startTime, "UTF-8"),
                    URLEncoder.encode(endTime, "UTF-8"), aggregator, aggregator, groupKeys);
            String reqUrl = uri + reqParams;

            String apiRes = HttpUtils.doGetProxy(reqUrl);
            if (StringUtils.isNotBlank(apiRes)) {
                DashboardRootEntity res = JsonUtils.parseObject(apiRes, DashboardRootEntity.class);
                if (res.getResult_code().equals("0")) {
                    return res;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LogUtils.error("searchHickwallVM", ex);
            return null;
        }
    }
}
