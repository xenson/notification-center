package com.ctrip.car.osd.notificationcenter.basicprocess;

import com.ctrip.car.osd.framework.soa.server.ServiceExecutor;
import com.ctrip.car.osd.framework.soa.server.validator.Validator;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Created by xiayx on 2020/4/29.
 */
@Component
public abstract class BasicExecutor<TRequest, TResponse> extends ServiceExecutor<TRequest, TResponse>
        implements Validator<TRequest> {
    @Override
    protected TResponse service(TRequest tRequest) throws Exception {
        //do some individual trace
        return processEntry(tRequest);
    }

    @Override
    public void validate(AbstractValidator<TRequest> abstractValidator) {
        requestValidate(abstractValidator);
    }

    /**
     * each api executor process entry
     *
     * @param tRequest
     * @return
     */
    public abstract TResponse processEntry(TRequest tRequest) throws SQLException;

    public abstract void requestValidate(AbstractValidator<TRequest> validator);
}