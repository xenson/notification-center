package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.SearchCKRequestType;
import com.ctrip.car.osd.notificationcenter.dto.SearchCKResponseType;
import com.ctrip.car.osd.notificationcenter.service.SearchTrackDBService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
public class SearchCKExecutor extends BasicExecutor<SearchCKRequestType, SearchCKResponseType> {

    @Autowired
    private SearchTrackDBService searchTrackDBService;

    @Override
    public SearchCKResponseType processEntry(SearchCKRequestType searchRequestType) {
        //do some business and log
        return searchTrackDBService.searchCK(searchRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<SearchCKRequestType> validator) {
        //check request parameters if valid
        validator.ruleFor("db").notNull();
        validator.ruleFor("query").notNull();
    }

}