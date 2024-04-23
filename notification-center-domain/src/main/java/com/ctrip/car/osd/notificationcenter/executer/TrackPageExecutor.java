package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.basebiz.accounts.mobile.request.filter.WithAccountsMobileRequestFilter;
import com.ctrip.basebiz.accounts.mobile.request.filter.bean.AuthenticationModeEnum;
import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.TrackPageRequestType;
import com.ctrip.car.osd.notificationcenter.dto.TrackPageResponseType;
import com.ctrip.car.osd.notificationcenter.service.TrackService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
@WithAccountsMobileRequestFilter(authenticationMode = AuthenticationModeEnum.OnDemand)
public class TrackPageExecutor extends BasicExecutor<TrackPageRequestType, TrackPageResponseType> {

    @Autowired
    private TrackService trackService;

    @Override
    public TrackPageResponseType processEntry(TrackPageRequestType tackPageRequestType) {
        //do some business and log
        return trackService.trackPage(tackPageRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<TrackPageRequestType> validator) {
        //check request parameters if valid
        //validator.ruleFor("extendInfos").notNull();
    }

}