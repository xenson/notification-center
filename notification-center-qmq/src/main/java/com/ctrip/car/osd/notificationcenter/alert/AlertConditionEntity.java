package com.ctrip.car.osd.notificationcenter.alert;

/**
 * Created by xiayx on 2022/1/4.
 */
public class AlertConditionEntity {
    private String title;
    private String query;
    private String qmq_key;
    private String qmq_subject;
    private String duration;
    private String repeat;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQmq_key() {
        return qmq_key;
    }

    public void setQmq_key(String qmq_key) {
        this.qmq_key = qmq_key;
    }

    public String getQmq_subject() {
        return qmq_subject;
    }

    public void setQmq_subject(String qmq_subject) {
        this.qmq_subject = qmq_subject;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
