package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.SearchHWRequestType;
import com.ctrip.car.osd.notificationcenter.dto.SearchHWResponseType;
import com.ctrip.car.osd.notificationcenter.service.SearchTrackDBService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
public class SearchHWExecutor extends BasicExecutor<SearchHWRequestType, SearchHWResponseType> {

    @Autowired
    private SearchTrackDBService searchTrackDBService;

    @Override
    public SearchHWResponseType processEntry(SearchHWRequestType searchRequestType) {
        //do some business and log
        return searchTrackDBService.searchHW(searchRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<SearchHWRequestType> validator) {
        //check request parameters if valid
        validator.ruleFor("db").notNull();
        validator.ruleFor("query").notNull();
    }
}