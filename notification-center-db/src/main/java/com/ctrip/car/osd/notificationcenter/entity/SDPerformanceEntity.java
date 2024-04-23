package com.ctrip.car.osd.notificationcenter.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "carnotificationdb")
@Table(name = "sd_performance_persitence")
public class SDPerformanceEntity implements DalPojo {
    /**
     * 行id
     */
    @Id
    @Column(name = "id")
    @Type(value = Types.BIGINT)
    private Long id;

    /**
     * 统计名称
     */
    @Column(name = "title")
    @Type(value = Types.VARCHAR)
    private String title;

    /**
     * 平均值
     */
    @Column(name = "_avg")
    @Type(value = Types.FLOAT)
    private Double _avg;

    /**
     * 20线
     */
    @Column(name = "_20")
    @Type(value = Types.FLOAT)
    private Double _20;

    /**
     * 50线
     */
    @Column(name = "_50")
    @Type(value = Types.FLOAT)
    private Double _50;

    /**
     * 75线
     */
    @Column(name = "_75")
    @Type(value = Types.FLOAT)
    private Double _75;

    /**
     * 85线
     */
    @Column(name = "_85")
    @Type(value = Types.FLOAT)
    private Double _85;

    /**
     * 95线
     */
    @Column(name = "_95")
    @Type(value = Types.FLOAT)
    private Double _95;

    /**
     * 99线
     */
    @Column(name = "_99")
    @Type(value = Types.FLOAT)
    private Double _99;

    /**
     * 计数
     */
    @Column(name = "count")
    @Type(value = Types.BIGINT)
    private Long count;

    /**
     * 数据时间
     */
    @Column(name = "d_date")
    @Type(value = Types.VARCHAR)
    private String d_date;

    /**
     * 修改时间戳
     */
    @Column(name = "DataChange_LastTime", insertable = false, updatable = false)
    @Type(value = Types.TIMESTAMP)
    private Timestamp datachangeLasttime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double get_avg() {
        return _avg;
    }

    public void set_avg(Double _avg) {
        this._avg = _avg;
    }

    public Double get_20() {
        return _20;
    }

    public void set_20(Double _20) {
        this._20 = _20;
    }

    public Double get_50() {
        return _50;
    }

    public void set_50(Double _50) {
        this._50 = _50;
    }

    public Double get_75() {
        return _75;
    }

    public void set_75(Double _75) {
        this._75 = _75;
    }

    public Double get_85() {
        return _85;
    }

    public void set_85(Double _85) {
        this._85 = _85;
    }

    public Double get_95() {
        return _95;
    }

    public void set_95(Double _95) {
        this._95 = _95;
    }

    public Double get_99() {
        return _99;
    }

    public void set_99(Double _99) {
        this._99 = _99;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getD_date() {
        return d_date;
    }

    public void setD_date(String d_date) {
        this.d_date = d_date;
    }

    public Timestamp getDatachangeLasttime() {
        return datachangeLasttime;
    }

    public void setDatachangeLasttime(Timestamp datachangeLasttime) {
        this.datachangeLasttime = datachangeLasttime;
    }
}