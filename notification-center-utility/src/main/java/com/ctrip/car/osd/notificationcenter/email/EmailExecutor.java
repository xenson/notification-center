package com.ctrip.car.osd.notificationcenter.email;

import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.basic.StreamUtil;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.email.entity.MailBody;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.soa.platform.basesystem.emailservice.v1.*;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiayx on 2019/11/5.
 */
public class EmailExecutor implements Runnable, Serializable {
    private final EmailServiceClient client;
    private final MailBody mailBody;
    private final String senderReq;

    public EmailExecutor(final MailBody msg, final String sender) {
        this.mailBody = msg;
        this.senderReq = StringUtils.isNotBlank(sender) ? sender : QCAppsetting.get("AlertEmail_DefaultSedder");
        this.client = EmailServiceClient.getInstance();
    }

    private static String generateBodyContent(String content) {
        return ("<entry><content><![CDATA[" + content + "]]></content></entry>").replace("\r\n", "<br>");
    }

    @Override
    public void run() {
        try {
            final SendEmailRequest request = new SendEmailRequest();
            Integer appId = Integer.valueOf(Foundation.app().getAppId());
            String templateID = QCAppsetting.get("AlertEmail_TemplateID");

            //请改为自己应用的APPID
            request.setAppID(appId);
            //request.setAppID(100008077);
            //发送邮箱地址
            request.setSender(senderReq);
            request.setSenderName(mailBody.getSenderName());
            //收件人邮箱地址
            request.setRecipient(Arrays.asList(mailBody.getRecipient().split(";")));

            //抄送邮箱地址
            Optional.ofNullable(mailBody.getCc()).filter(StringUtils::isNotEmpty)
                    .map(cc -> StreamUtil.streamNullable(StringUtils.split(cc, ";")).collect(Collectors.toList()))
                    .ifPresent(request::setCc);
            //SendCode、Charset、TemplateID

            request.setSendCode(templateID);
            request.setBodyTemplateID(Integer.valueOf(templateID));
            //UTF-8
            request.setCharset("UTF-8");
            request.setSubject(String.format("[%s] %s", Foundation.server().getEnv(),
                    mailBody.getSubject()));
            request.setBodyContent(generateBodyContent(mailBody.getBodyContent()));
            request.setIsBodyHtml(true);
            request.setSourceID(0);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            request.setExpiredTime(calendar);

            request.setAttachmentList(uploadAttachments());

            final SendEmailResponse response = client.sendEmail(request);

            String logInfo = JsonUtils.parseJson(request) + "\r\n" + JsonUtils.parseJson(response);
            LogUtils.info("SendMail_Success", logInfo);
            if (!Objects.equals(response.getResponseStatus().getAck(), AckCodeType.Success)) {
                LogUtils.warn("SendMail_Fail", logInfo);
            }
        } catch (Exception ex) {
            LogUtils.error("EmailExecutor", ex);
        }
    }

    private List<Attachment> uploadAttachments() throws Exception {
        if (CollectionUtils.isEmpty(mailBody.getAttachmentList())) {
            return null;
        }
        final UploadAttachmentRequest uploadRequest = new UploadAttachmentRequest();
        uploadRequest.setAttachmentList(mailBody.getAttachmentList().stream().map(origin -> {
            Attachment attachment = new Attachment();
            attachment.setAttachmentContent(origin.getContent());
            attachment.setAttachmentName(origin.getName());
            return attachment;
        }).collect(Collectors.toList()));
        final UploadAttachmentResponse uploadResponse = client.uploadAttachment(uploadRequest);
        if (uploadResponse.getResultCode() == 1) {
            return uploadResponse.getAttachmentList();
        }
        return null;
    }
}
