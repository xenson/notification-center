package com.ctrip.car.osd.notificationcenter.kafka;

import java.util.List;
import java.util.Set;

/**
 * Created by xiayx on 2022/1/4.
 */
public class KafkaConditionEntity {
    private String title;
    private String root;
    private String path;
    private String query;
    private String topic;
    private String consumer;
    private String source;
    private String target;
    private List<String> prekeys;
    private Set<String> trustkeys;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getPrekeys() {
        return prekeys;
    }

    public void setPrekeys(List<String> prekeys) {
        this.prekeys = prekeys;
    }

    public Set<String> getTrustkeys() {
        return trustkeys;
    }

    public void setTrustkeys(Set<String> trustkeys) {
        this.trustkeys = trustkeys;
    }
}
