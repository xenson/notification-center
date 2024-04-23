package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.TrackerRequestType;
import com.ctrip.car.osd.notificationcenter.dto.TrackerResponseType;
import com.ctrip.car.osd.notificationcenter.service.TrackService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
public class TrackerExecutor extends BasicExecutor<TrackerRequestType, TrackerResponseType> {

    @Autowired
    private TrackService trackService;

    @Override
    public TrackerResponseType processEntry(TrackerRequestType trackerRequestType) {
        //do some business and log
        return trackService.tracker(trackerRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<TrackerRequestType> validator) {
        //check request parameters if valid
        //validator.ruleFor("extendInfo").notNull();
    }

}