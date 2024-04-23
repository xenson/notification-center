package com.ctrip.car.osd.notificationcenter.enums;

import com.ctrip.car.osd.framework.common.exception.BizException;

/**
 * Created by xiayx on 2019/7/31.
 */
public enum ResultCodes {
    //1为服务端返回提示、2可不提示、3需要提示
    //第1位:错误提示级别、2~3位:服务模块代码(01订单相关,02产品相关,03用户相关,)、4~7位:具体错误代码

    //服务端未知原因
    Server_Unknown(-1),
    //服务端异常或错误
    Server_Error(0),
    //服务端成功响应
    Server_Success(1);


    private final long value;

    ResultCodes(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public String getDesc() {
        switch (this) {
            case Server_Unknown:
                return "server unknown exception";
            case Server_Error:
                return "server failed or exception";
            case Server_Success:
                return "server response success";
            default:
                throw new BizException("unknown resultcode: " + this.toString());
        }
    }
}
