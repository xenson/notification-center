package com.ctrip.car.osd.notificationcenter.tracker;

import com.ctrip.car.osd.notificationcenter.config.QCHickwall;
import com.ctrip.car.osd.notificationcenter.tracker.components.HickwallTracker;
import com.ctrip.car.osd.notificationcenter.tracker.components.TrackerAct;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by xiayx on 2021/5/28.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TrackerAct.class, HickwallTracker.class, QCHickwall.class})
public class HickwallTrackerTest {
    //    @InjectMocks
    //    HickwallTracker hickwallTracker;//interface
    HickwallTracker hickwallTracker;//abstract

    @Before
    public void init() {
        hickwallTracker = spy(new HickwallTracker());
        //mock metrics list - 3 UT branch
        mockStatic(QCHickwall.class);
        List<String> metrics = Arrays.asList("recordOne_key_pageId_pageReady", "recordSize_key_pageId_renderCost",
                "addGauge_key_residency", "recordOne_key_pageId_resultSize");
        when(QCHickwall.getList(anyString())).thenReturn(metrics);

    }

    @Test
    public void track_test() {
        Map<String, String> trackInfo = new HashMap<>();
        trackInfo.put("key", "183828");
        trackInfo.put("pageId", "222017");
        trackInfo.put("isSuccess", "true");
        trackInfo.put("pageReady", "100");
        trackInfo.put("renderCost", "10");
        trackInfo.put("residency", "1");

        hickwallTracker.track(trackInfo);
    }
}
