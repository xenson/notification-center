package com.ctrip.car.osd.notificationcenter.proxy;

import com.ctrip.car.osd.framework.cache.CacheType;
import com.ctrip.car.osd.framework.cache.Cacheable;
import com.ctrip.car.osd.notificationcenter.basic.HttpUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.entity.CKReqBodyEntity;
import com.ctrip.car.osd.notificationcenter.entity.CKReqRootEntity;
import com.ctrip.car.osd.notificationcenter.entity.CKRootEntity;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xiayx on 2021/11/3.
 */
@Repository
public class ClickHouseProxy {
    private final int Cache_ONEHour = 3600000;

    /**
     * ck db access from redis cache
     *
     * @param cacheKey
     * @param queryKey
     * @param params
     * @return
     */
    @Cacheable(name = "ClickHouseProxy.searchClickHouse", key = "{1}", type = CacheType.REDIS, options = {"carosdcommoncache", "gzip"}, expiryMillis = Cache_ONEHour)
    public CKRootEntity searchClickHouse(String cacheKey, String queryKey, List<String> params) {
        return searchClickHouse(queryKey, params);
    }

    /**
     * common ck db access
     *
     * @param queryKey
     * @param params
     * @return
     */
    public CKRootEntity searchClickHouse(String queryKey, List<String> params) {
        try {
            String reqUrl = QCAppsetting.get("ClickHouse_OSGUri");
            String accessToken = QCAppsetting.get("ClickHouse_AccessToken");
            CKReqRootEntity reqRoot = new CKReqRootEntity();
            reqRoot.setAccess_token(accessToken);
            CKReqBodyEntity reqBody = new CKReqBodyEntity();
            reqBody.setQuery_key(queryKey);
            reqBody.setParams(params);
            reqRoot.setRequest_body(reqBody);
            String reqContents = JsonUtils.parseJson(reqRoot);

            String apiRes = HttpUtils.doPostProxy(reqUrl, reqContents, null);
            if (StringUtils.isNotBlank(apiRes)) {
                CKRootEntity res = JsonUtils.parseObject(apiRes, CKRootEntity.class);
                return res;
            } else {
                LogUtils.warn("searchClickHouse", reqContents);
                return null;
            }
        } catch (Exception ex) {
            LogUtils.error("searchClickHouse", ex);
            return null;
        }
    }

}
