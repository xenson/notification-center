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
@Table(name = "sd_flowaggregate_persistence")
public class SDFlowAggregateEntity implements DalPojo {
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
     * 总计算
     */
	@Column(name = "all_count")
	@Type(value = Types.BIGINT)
	private Long all_count;

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

	public Long getAll_count() {
		return all_count;
	}

	public void setAll_count(Long all_count) {
		this.all_count = all_count;
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