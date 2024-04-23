package com.ctrip.car.osd.notificationcenter.tracker.common;

import com.ctrip.car.osd.notificationcenter.config.QCClickhouse;
import com.ctrip.framework.foundation.Foundation;
import org.apache.commons.lang.StringUtils;

/**
 * Created by xiayx on 2021/2/8.
 */
public class Constants {
    /**
     * default appid & service Code
     */
    public final static String AppId = StringUtils.isNotEmpty(Foundation.app().getAppId()) ? Foundation.app().getAppId() : "100025206";
    public final static String ServiceCode = Foundation.getProperty("app.servicecode", "0");

    //request default key demeter/ or http://traceinfo.ctripcorp.com/
    //key="car_trace_ubt_default"
    //keyId="186092"
    //keyName="租车服务端默认埋点Key"
    public final static String TackUBTKey = "car_tace_ubt_default";
    public final static String TackUBTKeyId = "186092";
    public final static String OrganizationId = "36";

    /**
     * "car-rental-blueprint"
     */
    public final static String Scenario_Default = QCClickhouse.get("Blueprint_Scenario");
    /**
     * "blueprint_track"
     */
    public final static String Scenario_Title = QCClickhouse.get("Blueprint_ScenarioTitle");
    /**
     * cat&es config prefix
     */
    public final static String Scenario_Prefix = "car-rental-";

}
