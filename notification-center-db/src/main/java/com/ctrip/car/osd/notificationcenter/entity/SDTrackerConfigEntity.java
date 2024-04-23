package com.ctrip.car.osd.notificationcenter.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "carnotificationdb")
@Table(name = "sd_tracker_config")
@Getter
@Setter
public class SDTrackerConfigEntity implements DalPojo {
    @Id
    @Column(name = "id")
    @Type(value = Types.BIGINT)
    private Long id;

    @Column(name = "title")
    @Type(value = Types.VARCHAR)
    private String title;

    @Column(name = "model")
    @Type(value = Types.VARCHAR)
    private String model;

    @Column(name = "db")
    @Type(value = Types.VARCHAR)
    private String db;

    @Column(name = "field")
    @Type(value = Types.VARCHAR)
    private String field;

    @Column(name = "query")
    @Type(value = Types.VARCHAR)
    private String query;

    @Column(name = "department")
    @Type(value = Types.VARCHAR)
    private String department;

    @Column(name = "source")
    @Type(value = Types.VARCHAR)
    private String source;

    @Column(name = "isActive")
    @Type(value = Types.BIT)
    private Boolean isActive;

    @Column(name = "utc")
    @Type(value = Types.INTEGER)
    private Integer utc;

    @Column(name = "DataChange_LastTime", insertable = false, updatable = false)
    @Type(value = Types.TIMESTAMP)
    private Timestamp datachangeLasttime;

}
