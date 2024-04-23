package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.TrackAPIRequestType;
import com.ctrip.car.osd.notificationcenter.dto.TrackAPIResponseType;
import com.ctrip.car.osd.notificationcenter.service.TrackService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
public class TrackAPIExecutor extends BasicExecutor<TrackAPIRequestType, TrackAPIResponseType> {

    @Autowired
    private TrackService trackService;

    @Override
    public TrackAPIResponseType processEntry(TrackAPIRequestType trackAPIRequestType) {
        //do some business and log
        return trackService.trackAPI(trackAPIRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<TrackAPIRequestType> validator) {
        //check request parameters if valid
        //validator.ruleFor("extendInfos").notNull();
    }

}