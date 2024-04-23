package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.DemoApiRequestType;
import com.ctrip.car.osd.notificationcenter.dto.DemoApiResponseType;
import com.ctrip.car.osd.notificationcenter.service.DemoApiService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Created by xiayx on 2019/11/6.
 */
@Component
public class DemoApiExecutor
        extends BasicExecutor<DemoApiRequestType, DemoApiResponseType> {
    @Autowired
    private DemoApiService demoApiService;

    @Override
    public DemoApiResponseType processEntry(DemoApiRequestType demoApiRequestType) throws SQLException {
        //do some business and log
        return demoApiService.demoApiProcess(demoApiRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<DemoApiRequestType> validator) {
        //check request parameters if valid
        //validator.ruleFor("requestId").notNull();
    }
}
