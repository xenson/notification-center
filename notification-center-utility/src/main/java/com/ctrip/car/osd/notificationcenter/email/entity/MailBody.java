package com.ctrip.car.osd.notificationcenter.email.entity;

import java.util.List;

/**
 * Created by xiayx on 2019/10/30.
 */
public class MailBody {
    /**
     * email recipients(split by ";" has more than one)
     */
    private String recipient;
    /**
     * email subject
     */
    private String subject;

    /**
     * email major content
     */
    private String bodyContent;
    /**
     * email sender
     */
    private String senderName;
    /**
     * email cc
     */
    private String cc;
    /**
     * email attachments
     */
    private List<MailAttachment> attachmentList;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public List<MailAttachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<MailAttachment> attachmentList) {
        this.attachmentList = attachmentList;
    }
}
