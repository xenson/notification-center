package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.basebiz.accounts.mobile.request.filter.util.AccountsMobileRequestUtils;
import com.ctrip.car.osd.notificationcenter.analyzer.entity.HitFilterResult;
import com.ctrip.car.osd.notificationcenter.api.CarOsdNotificationCenterClient;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCClickhouse;
import com.ctrip.car.osd.notificationcenter.dto.*;
import com.ctrip.car.osd.notificationcenter.entity.ExtendInfoMap;
import com.ctrip.car.osd.notificationcenter.qmq.QmqProducerDemo;
import com.ctrip.car.osd.notificationcenter.tracker.TrackerUnite;
import com.ctrip.car.osd.notificationcenter.tracker.common.Constants;
import com.ctrip.car.osd.notificationcenter.tracker.common.TrackerUtils;
import com.ctrip.car.osd.notificationcenter.tracker.components.CKTracker;
import com.ctrip.car.osd.notificationcenter.tracker.enums.TrackerFromEnum;
import com.ctrip.car.osd.notificationcenter.tracker.enums.TrackerType;
import com.ctrip.model.TrackByScenarioRequestType;
import com.ctrip.model.TrackByScenarioResponseType;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import com.ctriposs.baiji.rpc.common.util.ServiceUtils;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xiayx on 2020/5/7.
 */
@Service
public class TrackService {
    @Autowired
    QmqProducerDemo qmqProducerDemo;

    private static final CarOsdNotificationCenterClient trackClient = CarOsdNotificationCenterClient.getInstance();

    /**
     * 接口埋点-批量
     *
     * @param trackAPIRequest
     * @return
     */
    public TrackAPIResponseType trackAPI(TrackAPIRequestType trackAPIRequest) {
        TrackAPIResponseType response = new TrackAPIResponseType();
        response.setBaseResponse(new BaseResponse());
        if (CollectionUtils.isEmpty(trackAPIRequest.getExtendInfos())) {
            response.getBaseResponse().setIsSuccess(false);
            response.getBaseResponse().setCode("0");
            return response;
        }

        boolean result = true;
        for (ExtendInfoMap extendInfo : trackAPIRequest.getExtendInfos()) {
            if (!extendInfo.getExtendInfo().containsKey("keyfrom")) {
                extendInfo.getExtendInfo().put("keyfrom", TrackerFromEnum.trackAPI.name());
            }
            result = result & TrackerUnite.tracker(extendInfo.getExtendInfo());
        }

        response.getBaseResponse().setIsSuccess(result);
        response.getBaseResponse().setCode(result ? "1" : "0");
        return response;
    }

    /**
     * 页面埋点-批量
     *
     * @param trackPageRequest
     * @return
     */
    public TrackPageResponseType trackPage(TrackPageRequestType trackPageRequest) {
        TrackPageResponseType response = new TrackPageResponseType();
        response.setBaseResponse(new BaseResponse());
        if (CollectionUtils.isEmpty(trackPageRequest.getExtendInfos())) {
            response.getBaseResponse().setIsSuccess(false);
            response.getBaseResponse().setCode("0");
            return response;
        }

        //get uid form front-end auth
        String uid = ServiceUtils.getExtensionData(trackPageRequest, AccountsMobileRequestUtils.MobileUserIdExtensionKey);

        boolean result = true;
        for (ExtendInfoMap extendInfo : trackPageRequest.getExtendInfos()) {
            if (!extendInfo.getExtendInfo().containsKey("keyfrom")) {
                extendInfo.getExtendInfo().put("keyfrom", TrackerFromEnum.trackPage.name());
            }

            String trackuid = extendInfo.getExtendInfo().getOrDefault("uid", "");
            String trackuId = extendInfo.getExtendInfo().getOrDefault("uId", "");
            if (StringUtils.isBlank(trackuid) && StringUtils.isBlank(trackuId) && StringUtils.isNotBlank(uid)) {
                extendInfo.getExtendInfo().put("uId", uid);
            }
            result = result & TrackerUnite.tracker(extendInfo.getExtendInfo());
        }

        response.getBaseResponse().setIsSuccess(result);
        response.getBaseResponse().setCode(result ? "1" : "0");
        return response;
    }

    /**
     * 接口埋点-逐条
     *
     * @param trackerRequest
     * @return
     */
    public TrackerResponseType tracker(TrackerRequestType trackerRequest) {
        TrackerResponseType response = new TrackerResponseType();
        response.setBaseResponse(new BaseResponse());
        if (trackerRequest.getExtendInfo().isEmpty()) {
            response.getBaseResponse().setIsSuccess(false);
            response.getBaseResponse().setCode("0");
            return response;
        }

        if (!trackerRequest.getExtendInfo().containsKey("keyfrom")) {
            trackerRequest.getExtendInfo().put("keyfrom", TrackerFromEnum.trackAPI.name());
        }
        boolean result = TrackerUnite.tracker(trackerRequest.getExtendInfo());
//        //qmq改造
//        qmqProducerDemo.sendMessage("car.osdnotificationcenter.server.tracker", trackerRequest.getExtendInfo());

        response.getBaseResponse().setIsSuccess(result);
        response.getBaseResponse().setCode(result ? "1" : "0");
        return response;
    }

    /**
     * 接口UBT埋点-逐条
     *
     * @param trackerRequest
     * @return
     */
    public TrackUBTResponseType trackUBT(TrackUBTRequestType trackerRequest) {
        TrackUBTResponseType response = new TrackUBTResponseType();
        response.setBaseResponse(new BaseResponse());
        if (trackerRequest.getExtendInfo().isEmpty()) {
            response.getBaseResponse().setIsSuccess(false);
            response.getBaseResponse().setCode("0");
            return response;
        }

        if (!trackerRequest.getExtendInfo().containsKey("keyfrom")) {
            trackerRequest.getExtendInfo().put("keyfrom", TrackerFromEnum.trackAPI.name());
        }
        boolean result = TrackerUnite.tracker(trackerRequest.getExtendInfo(), TrackerType.trackUBT, TrackerType.trackCK);

        response.getBaseResponse().setIsSuccess(result);
        response.getBaseResponse().setCode(result ? "1" : "0");
        return response;
    }

    public TrackByScenarioResponseType trackByScenario(TrackByScenarioRequestType request) {
        TrackByScenarioResponseType response = new TrackByScenarioResponseType();
        String scenario = request.getScenario();
        Map<String, String> indexTags = request.getTrackMap();
        Map<String, String> storeTags = new HashMap<>();

        try {
            Cat.logTags(scenario, indexTags, storeTags);
            response.setTrackSuccess(true);
        } catch (Exception e) {
            response.setTrackSuccess(false);
            return response;
        }
        return response;
    }
}
