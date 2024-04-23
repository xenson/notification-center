package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.service.TrackService;
import com.ctrip.model.TrackByScenarioRequestType;
import com.ctrip.model.TrackByScenarioResponseType;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackByScenarioExecutor extends BasicExecutor<TrackByScenarioRequestType, TrackByScenarioResponseType> {
    @Autowired
    private TrackService trackService;
    @Override
    public TrackByScenarioResponseType processEntry(TrackByScenarioRequestType trackByScenarioRequestType) {
        return trackService.trackByScenario(trackByScenarioRequestType);
    }

    @Override
    public void requestValidate(AbstractValidator<TrackByScenarioRequestType> validator) {
        validator.ruleFor("scenario").notNull();
    }
}
