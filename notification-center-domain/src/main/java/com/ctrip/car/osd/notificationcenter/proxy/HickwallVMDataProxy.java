package com.ctrip.car.osd.notificationcenter.proxy;

import com.ctrip.car.osd.notificationcenter.basic.HttpUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.entity.HickwallVMDataRootEntity;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.MessageFormat;

/**
 * Created by xiayx on 2021/10/12.
 */
public class HickwallVMDataProxy {
    /**
     * search Hickwall db by target
     *
     * @param qurery ex:sum(sum_over_time(app.appid.idc.error.count_value{bu='租车',appid='100020270'}))
     * @param db     ex:APM-CAR
     * @param start  ex:1633968000
     * @param end
     * @param step   second:60
     * @return
     */
    public static HickwallVMDataRootEntity searchHickwallVM(String qurery, String db, Timestamp start, Timestamp end, Integer step) {
        String token = QCAppsetting.get("HickwallVMData_Token");
        String uri = QCAppsetting.get("HickwallVMData_Uri");
        String body = "{\"access_token\":\"%s\",\"request_body\":{}}";
        String reqBody = String.format(body, token);
        String params = "?query={0}&start={1}&end={2}&step={3}&db={4}";

        String startStr = String.valueOf(start.getTime() / 1000);
        String endStr = String.valueOf(end.getTime() / 1000);
        String stepStr = String.valueOf(step);

        try {
            String reqParams = MessageFormat.format(params, URLEncoder.encode(qurery, "UTF-8"), startStr, endStr, stepStr, db);
            String reqUrl = uri + reqParams;

            String apiRes = HttpUtils.doPostProxy(reqUrl, reqBody, null);
            if (StringUtils.isNotBlank(apiRes)) {
                HickwallVMDataRootEntity res = JsonUtils.parseObject(apiRes, HickwallVMDataRootEntity.class);
                return res;
            } else {
                LogUtils.warn("searchHickwallVM", qurery);
                return null;
            }
        } catch (Exception ex) {
            LogUtils.error("searchHickwallVM", ex);
            return null;
        }
    }

}
