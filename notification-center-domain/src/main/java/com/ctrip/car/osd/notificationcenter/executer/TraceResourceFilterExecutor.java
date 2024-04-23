package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basic.ObjectUtils;
import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.TraceResourceFilterRequestType;
import com.ctrip.car.osd.notificationcenter.dto.TraceResourceFilterResponseType;
import com.ctrip.car.osd.notificationcenter.dto.TrackerRequestType;
import com.ctrip.car.osd.notificationcenter.dto.TrackerResponseType;
import com.ctrip.car.osd.notificationcenter.service.TrackService;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lh on 2022/9/27.
 */
@Component
public class TraceResourceFilterExecutor extends BasicExecutor<TraceResourceFilterRequestType, TraceResourceFilterResponseType> {

    @Autowired
    private TrackService trackService;

    @Override
    public TraceResourceFilterResponseType processEntry(TraceResourceFilterRequestType request) {
        if (StringUtils.isEmpty(request.getKeyId()) || StringUtils.isEmpty(request.getRequestId()) || StringUtils.isEmpty(request.getFilterSource())) {
            TraceResourceFilterResponseType response = new TraceResourceFilterResponseType();
            response.setBaseResponse(new BaseResponse(false, "500", "keyId, requestId, filterSource can not be null", request.getRequestId(), 0L));
            return response;
        }
        return convertTraceResourceFilterResponseType(trackService.tracker(convertTrackerRequestType(request)));
    }


    public TrackerRequestType convertTrackerRequestType(TraceResourceFilterRequestType request) {
        TrackerRequestType requestType = new TrackerRequestType();
        Map<String, String> data = ObjectUtils.objectToMap(request, getJsonList());
        data.put("key", data.get("keyId"));
        requestType.setExtendInfo(data);
        return requestType;
    }

    public TraceResourceFilterResponseType convertTraceResourceFilterResponseType(TrackerResponseType responseType) {
        TraceResourceFilterResponseType response = new TraceResourceFilterResponseType();
        response.setBaseResponse(responseType.getBaseResponse());
        response.setResponseStatus(responseType.getResponseStatus());
        return response;
    }

    private List<String> getJsonList () {
        List<String> list = new ArrayList<>();
        list.add("filterContent");
        list.add("vendorPickStoreIds");
        list.add("osdVendorFilterContent");
        list.add("vendorStoreIds");
        list.add("osdVcFilterContent");
        list.add("invalidVendorIds");
        return list;
    }

    @Override
    public void requestValidate(AbstractValidator<TraceResourceFilterRequestType> validator) {
    }

}