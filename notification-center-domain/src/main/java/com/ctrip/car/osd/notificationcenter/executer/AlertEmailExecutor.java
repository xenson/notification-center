package com.ctrip.car.osd.notificationcenter.executer;

import com.ctrip.car.osd.notificationcenter.basicprocess.BasicExecutor;
import com.ctrip.car.osd.notificationcenter.dto.AlertEmailRequestType;
import com.ctrip.car.osd.notificationcenter.dto.AlertEmailResponseType;
import com.ctrip.car.osd.notificationcenter.email.EmailSend;
import com.ctrip.car.osd.notificationcenter.enums.ResultCodes;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by xiayx on 2020/12/24.
 */
@Component
public class AlertEmailExecutor
        extends BasicExecutor<AlertEmailRequestType, AlertEmailResponseType> {

    @Override
    public AlertEmailResponseType processEntry(AlertEmailRequestType request) {
        AlertEmailResponseType response = new AlertEmailResponseType();
        setResult(request, response, ResultCodes.Server_Unknown, "");

        try {
            EmailSend.sendMail(request.getRecipients(), request.getRecipients(), request.getSender(), request.getSender(),
                    request.getCc(), request.getSubject(), request.getBodyContent(), new ArrayList<>());

            setResult(request, response, ResultCodes.Server_Success, "");
        } catch (Exception ex) {
            setResult(request, response, ResultCodes.Server_Error, "");
            LogUtils.error("AlertEmailExecutor", ex);
        }
        return response;
    }

    @Override
    public void requestValidate(AbstractValidator<AlertEmailRequestType> validator) {
        //check request parameters if valid
        validator.ruleFor("recipients").notNull();
    }

    /**
     * set response result code
     *
     * @param request
     * @param response
     * @param resultCode
     */
    private void setResult(AlertEmailRequestType request, AlertEmailResponseType response,
                           ResultCodes resultCode, String reMsg) {
        if (null == response.getBaseResponse()) {
            response.setBaseResponse(new BaseResponse());
        }
        boolean isSuccess = false;
        if (resultCode == ResultCodes.Server_Success) {
            isSuccess = true;
        }
        if (request.getBaseRequest() != null && StringUtils.isNotBlank(request.getBaseRequest().getRequestId())) {
            response.getBaseResponse().setRequestId(request.getBaseRequest().getRequestId());
        }

        response.getBaseResponse().setIsSuccess(isSuccess);
        response.getBaseResponse().setCode(String.valueOf(resultCode.getValue()));

        response.getBaseResponse().setReturnMsg(resultCode.getDesc());
        response.getBaseResponse().setReturnMsg(resultCode.getDesc());
        if (StringUtils.isNotBlank(reMsg)) {
            response.getBaseResponse().setReturnMsg(reMsg);
        }
    }
}
