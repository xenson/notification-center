package com.ctrip.car.osd.notificationcenter.module;

import org.springframework.stereotype.Service;

/**
 * Created by xiayx on 2019/11/6.
 */
@Service
public class DemoModule {
    /**
     * construct response msg
     *
     * @param input
     * @return
     */
    public String constructResponseMsg(String input) {
        String result = "request is:" + input + "; response is:" + input;
        return result;
    }
}
