package com.ctrip.car.osd.notificationcenter.entity;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by xiayx on 2021/10/13.
 */
public class PaasAppDetailEntity {
    private int id;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String status;
    private String category;
    private String container;

    private List<PaasAppOwnerEntity> owner_list;
    private PaasAppOrganizationEntity organization;
    private String name;
    private String chinese_name;
    private String soa_type;
    private String job_type;
    private String owner;
    private String owner_email;

    private String app_importance;
    private PaasAppProductEntity product;
    private PaasAppProductLineEntity product_line;
    private boolean new_build;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public List<PaasAppOwnerEntity> getOwner_list() {
        return owner_list;
    }

    public void setOwner_list(List<PaasAppOwnerEntity> owner_list) {
        this.owner_list = owner_list;
    }

    public PaasAppOrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(PaasAppOrganizationEntity organization) {
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChinese_name() {
        return chinese_name;
    }

    public void setChinese_name(String chinese_name) {
        this.chinese_name = chinese_name;
    }

    public String getSoa_type() {
        return soa_type;
    }

    public void setSoa_type(String soa_type) {
        this.soa_type = soa_type;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner_email() {
        return owner_email;
    }

    public void setOwner_email(String owner_email) {
        this.owner_email = owner_email;
    }

    public String getApp_importance() {
        return app_importance;
    }

    public void setApp_importance(String app_importance) {
        this.app_importance = app_importance;
    }

    public PaasAppProductEntity getProduct() {
        return product;
    }

    public void setProduct(PaasAppProductEntity product) {
        this.product = product;
    }

    public PaasAppProductLineEntity getProduct_line() {
        return product_line;
    }

    public void setProduct_line(PaasAppProductLineEntity product_line) {
        this.product_line = product_line;
    }

    public boolean isNew_build() {
        return new_build;
    }

    public void setNew_build(boolean new_build) {
        this.new_build = new_build;
    }
}
