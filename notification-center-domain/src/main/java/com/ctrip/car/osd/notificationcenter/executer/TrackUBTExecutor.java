package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.TrackUBTRequestType;
import com.ctrip.car.osd.notificationcenter.dto.TrackUBTResponseType;
import com.ctrip.car.osd.notificationcenter.service.TrackService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
public class TrackUBTExecutor extends BasicExecutor<TrackUBTRequestType, TrackUBTResponseType> {

    @Autowired
    private TrackService trackService;

    @Override
    public TrackUBTResponseType processEntry(TrackUBTRequestType trackerRequestType) {
        //do some business and log
        return trackService.trackUBT(trackerRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<TrackUBTRequestType> validator) {
        //check request parameters if valid
        //validator.ruleFor("extendInfo").notNull();
    }

}