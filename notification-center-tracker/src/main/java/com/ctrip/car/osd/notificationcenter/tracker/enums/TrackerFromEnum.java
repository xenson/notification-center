package com.ctrip.car.osd.notificationcenter.tracker.enums;

/**
 * Created by xiayx on 2020/5/18.
 */
public enum TrackerFromEnum {
    /**
     * track from page
     */
    trackPage(0),
    /**
     * track from unknown api
     */
    trackAPI(1),
    /**
     * track from Restful
     */
    trackRestful(2),
    /**
     * track from Searching
     */
    trackSearching(3),
    /**
     * track from Ordering
     */
    trackOrdering(4),
    /**
     * track from VC
     */
    trackVC(5),
    /**
     * track from Crawler
     */
    trackCrawler(6),
    /**
     * track from framework - for top alert
     */
    trackFramework(7),

    /**
     * track from marketing
     */
    trackMarketing(8),
    /**
     * track from operation
     */
    trackOperation(9),
    /**
     * track from middle component
     */
    trackMiddle(10),
    /**
     * track from PMS
     */
    trackPMS(11),
    /**
     * track from distributions
     */
    trackDistribution(12),
    /**
     * track from Scrapy(Spider)
     */
    trackScrapy(13);


    private int value;

    TrackerFromEnum(int value) {
        this.value = value;
    }

    public TrackerFromEnum getByName(String name) {
        for (TrackerFromEnum obj : values()) {
            if (obj.name().equals(name)) {
                return obj;
            }
        }
        return trackAPI;
    }

}
