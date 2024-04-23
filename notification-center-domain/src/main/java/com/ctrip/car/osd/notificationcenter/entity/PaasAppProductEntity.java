package com.ctrip.car.osd.notificationcenter.entity;

/**
 * Created by xiayx on 2021/10/13.
 */
public class PaasAppProductEntity {
    private int id;
    private String name;
    private String english_name;
    private String status;
    private String description;
    private String cp4code;
    private String code;
    private String domain_name_keywords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCp4code() {
        return cp4code;
    }

    public void setCp4code(String cp4code) {
        this.cp4code = cp4code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDomain_name_keywords() {
        return domain_name_keywords;
    }

    public void setDomain_name_keywords(String domain_name_keywords) {
        this.domain_name_keywords = domain_name_keywords;
    }
}
