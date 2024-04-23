package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.SearchDashboardRequestType;
import com.ctrip.car.osd.notificationcenter.dto.SearchDashboardResponseType;
import com.ctrip.car.osd.notificationcenter.service.SearchTrackDBService;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by xiayx on 2020/5/7.
 */
@Component
public class SearchDashboardExecutor extends BasicExecutor<SearchDashboardRequestType, SearchDashboardResponseType> {

    @Autowired
    private SearchTrackDBService searchTrackDBService;

    @Override
    public SearchDashboardResponseType processEntry(SearchDashboardRequestType searchRequestType) {
        //do some business and log
        return searchTrackDBService.searchDashboard(searchRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<SearchDashboardRequestType> validator) {
        //check request parameters if valid
        validator.ruleFor("metricName").notNull();
    }
}