package com.ctrip.car.osd.notificationcenter.common;

import com.ctrip.car.osd.notificationcenter.po.CK2ArtQueryPO;
import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.spring.QConfig;

import java.util.List;

/**
 * Created by xiayx on 2022/8/29.
 */
@Component
public class QCCK2ArtQuery {
    @QConfig("ck2art-query.json")
    private static List<CK2ArtQueryPO> ck2artQuery;

    public static List<CK2ArtQueryPO> get() {
        return ck2artQuery;
    }
}
