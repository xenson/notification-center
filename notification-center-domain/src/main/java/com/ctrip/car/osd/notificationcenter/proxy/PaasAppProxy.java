package com.ctrip.car.osd.notificationcenter.proxy;

import com.ctrip.car.osd.framework.cache.Cacheable;
import com.ctrip.car.osd.notificationcenter.basic.HttpUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.entity.PaasAppDetailEntity;
import com.ctrip.car.osd.notificationcenter.entity.PaasAppInfoRootEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2021/10/18.
 */
@Repository
public class PaasAppProxy {
    private final int Cache_ONEMonth = 86400000 * 30;

    /**
     * search app detail from paas api
     * http://osgconsole.ops.ctripcorp.com/#/service/apiInfo/22853
     * Redis/mem cache
     *
     * @param appId
     * @return
     */
    @Cacheable(name = "PaasAppProxy.searchAppInfo", key = "appId_{1}", type = "mem", expiryMillis = Cache_ONEMonth)
    public PaasAppDetailEntity searchAppInfo(String appId) {
        String uri = QCAppsetting.get("PaasAppInfo_Uri");
        String params = "?format=json&page=1&page_size=5&search={0}&status=NORMAL";
        String reqParams = MessageFormat.format(params, appId);
        String reqUrl = uri + reqParams;

        String apiRes = HttpUtils.doGetProxy(reqUrl);
        if (StringUtils.isNotBlank(apiRes)) {
            PaasAppInfoRootEntity res = JsonUtils.parseObject(apiRes, PaasAppInfoRootEntity.class);
            if (res != null) {
                return res.getResults().get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * search all app detail from paas api
     *
     * @return
     */
    @Cacheable(name = "PaasAppProxy.searchCarAppsInfo", key = "all", expiryMillis = Cache_ONEMonth)
    public List<PaasAppDetailEntity> searchCarAppsInfo() {
        List<PaasAppDetailEntity> appsDetail = new ArrayList<>();
        List<String> appIds = QCAppsetting.getList("CarAppMonitor_AppIds");
        for (String appId : appIds) {
            PaasAppDetailEntity appDetail = searchAppInfo(appId);
            if (appDetail != null) {
                appsDetail.add(appDetail);
            }
        }
        return appsDetail;
    }

    /**
     * search app detail from paas api
     * http://osgconsole.ops.ctripcorp.com/#/service/apiInfo/22853
     *
     * @param appIds
     * @return
     */
    public Map<String, PaasAppDetailEntity> searchAppsInfo(List<String> appIds) {
        Map<String, PaasAppDetailEntity> details = new HashMap<>();
        for (String appId : appIds) {
            PaasAppDetailEntity res = searchAppInfo(appId);
            if (res != null) {
                details.put(appId, res);
            }
        }
        return details;
    }
}
