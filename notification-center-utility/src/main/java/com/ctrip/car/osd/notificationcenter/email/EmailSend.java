package com.ctrip.car.osd.notificationcenter.email;

import com.ctrip.car.osd.notificationcenter.email.entity.MailAttachment;
import com.ctrip.car.osd.notificationcenter.email.entity.MailBody;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiayx on 2019/10/29.
 */
public class EmailSend extends EmailService {
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 2, 1L,
            TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(1000),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    //邮件发送--多线程,异步,计划,日志
    public EmailSend() {

    }

    public EmailSend(String serviceUrl, String mailAccout, String mailPassword, String proxyHost, Integer proxyPort) throws Exception {
        super(serviceUrl, mailAccout, mailPassword, proxyHost, proxyPort);
    }


    public static void sendMail(final String recipient, final String recipientName,
                                final String sender, final String senderName,
                                final String cc, final String subject,
                                final String bodyContent,
                                final List<MailAttachment> attrList) throws Exception {
        MailBody mailBody = new MailBody();
        mailBody.setRecipient(recipient);
        mailBody.setSenderName(senderName);
        mailBody.setCc(cc);
        mailBody.setSubject(subject);
        mailBody.setBodyContent(bodyContent);
        mailBody.setAttachmentList(attrList);
        executorService.execute(new EmailExecutor(mailBody, sender));
        //EmailExecutor testEmail = new EmailExecutor(mailBody, sender);
        //testEmail.run();
    }
}
