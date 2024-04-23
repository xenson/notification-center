package com.ctrip.car.osd.notificationcenter.email.entity;

/**
 * Created by xiayx on 2019/10/30.
 */
public class MailAttachment {
    /**
     * support：rar,zip,7z,doc,xls,ppt,docx,xlsx,html,htm,pptx,mpp,vsd,pdf,txt,jpg,gif,png
     **/
    private String name;
    /**
     * attachment file stream
     **/
    private byte[] content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
