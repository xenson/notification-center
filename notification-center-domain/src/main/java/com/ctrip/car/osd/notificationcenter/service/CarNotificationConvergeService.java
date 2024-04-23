package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.dao.SDFlowAggregateDao;
import com.ctrip.car.osd.notificationcenter.dao.SDHitandsuccessDao;
import com.ctrip.car.osd.notificationcenter.dao.SDPerformanceDao;
import com.ctrip.car.osd.notificationcenter.dao.SDTrackerConfigDao;
import com.ctrip.car.osd.notificationcenter.dto.SearchCKRequestType;
import com.ctrip.car.osd.notificationcenter.dto.SearchCKResponseType;
import com.ctrip.car.osd.notificationcenter.entity.*;
import com.ctrip.car.osd.notificationcenter.enums.SearchModels;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by xiayx on 2022/8/25.
 */
@Service
public class CarNotificationConvergeService {
    /**
     * tracker threads pool
     */
    private final static ExecutorService ThreadPool = ThreadPoolUtil.callerRunsPool("blueprint-track-%d", 10);

    @Autowired
    private SearchTrackDBService searchTrackDBService;

    @Autowired
    private SDTrackerConfigDao sdTrackerConfigDao;

    /**
     * aggregation result from ck analysis by data day
     *
     */
    public Boolean aggregationAnalysisDay(QscheduleParameter qscheduleParameter) throws SQLException {
        LocalDateTime dataDay = DateTimeUtils.localDateNow().minusDays(1);
        String startForm = DateTimeUtils.parseStr(dataDay, "yyyy-MM-dd 00:00:00");
        String endForm = DateTimeUtils.parseStr(dataDay, "yyyy-MM-dd 23:59:59");

        Optional<LocalDateTime> fixDDate = DateTimeUtils.tryParseLDate(qscheduleParameter.getDateGap());
        if (fixDDate.isPresent()) {
            startForm = DateTimeUtils.parseStr(fixDDate.get(), "yyyy-MM-dd 00:00:00");
            endForm = DateTimeUtils.parseStr(fixDDate.get(), "yyyy-MM-dd 23:59:59");
        }
        return aggregationAnalysis(startForm, endForm, qscheduleParameter.getIds());
    }

    /**
     * aggregation result from ck analysis
     *
     * @param start
     * @param end
     * @return
     */
    public Boolean aggregationAnalysis(String start, String end, String[] ids) throws SQLException {
        //query condition config
        List<SDTrackerConfigEntity> ck2ArtQuerys;
        if (Objects.nonNull(ids)) {
            ck2ArtQuerys = sdTrackerConfigDao.queryById(ids);
        } else {
            ck2ArtQuerys = sdTrackerConfigDao.queryAllConfig();
        }
        if (CollectionUtils.isEmpty(ck2ArtQuerys)) {
            return false;
        }
        //executor analysis by bucketing
        Integer bucketVolume = Integer.valueOf(QCAppsetting.get("CarNotification_BucketVolume"));
        List<List<SDTrackerConfigEntity>> buckets = Lists.partition(ck2ArtQuerys, bucketVolume);

        for (List<SDTrackerConfigEntity> bucket : buckets) {
            //multiple thread by buckets
            ThreadPool.submit(() -> {
                try {
                    for (SDTrackerConfigEntity ck2artPo : bucket) {
                        try {
                            SearchCKRequestType searchCKReq = getSearchCKRequestType(start, end, ck2artPo);

                            //获取CK统计结果,表结构对应
                            SearchCKResponseType ckRes = searchTrackDBService.searchCK(searchCKReq);
                            if (ckRes == null || CollectionUtils.isEmpty(ckRes.getDataset())) {
                                continue;
                            }
                            //multiple fields in one ck search insert to ck&db
                            String[] fields = ck2artPo.getField().split(",");

                            if (SearchModels.Perf.name().equalsIgnoreCase(ck2artPo.getModel())) {
                                persistentPerformance(ck2artPo.getTitle(), start, ckRes.getDataset(), fields);
                            } else if (SearchModels.Rate.name().equalsIgnoreCase(ck2artPo.getModel())) {
                                persistentHitrate(ck2artPo.getTitle(), start, ckRes.getDataset(), fields);
                            } else if (SearchModels.Flow.name().equalsIgnoreCase(ck2artPo.getModel())) {
                                persistentFlowAggregate(ck2artPo.getTitle(), start, ckRes.getDataset(), fields);
                            }
                        } catch (Exception ex) {
                            LogUtils.error("CarNotificationConvergeService_aggregationAnalysis", ex + "" + JsonUtils.parseJson(ck2artPo));
                        }
                    }
                } catch (Exception ex) {
                    LogUtils.error("CarNotificationConvergeService_aggregationAnalysis", ex);
                }
            });
        }
        return true;
    }

    public List<SDTrackerConfigEntity> getCK2ArtQuerys() throws SQLException {
        return sdTrackerConfigDao.queryAllConfig();
    }

    public List<SDTrackerConfigEntity> getCK2ArtQuerys(String[] ids) throws SQLException {
        return sdTrackerConfigDao.queryById(ids);
    }

    private static SearchCKRequestType getSearchCKRequestType(String start, String end, SDTrackerConfigEntity ck2artPo) {
        SearchCKRequestType searchCKReq = new SearchCKRequestType();
        searchCKReq.setStart(start);
        searchCKReq.setEnd(end);

        searchCKReq.setModel(ck2artPo.getModel());
        searchCKReq.setDb(ck2artPo.getDb());
        searchCKReq.setFields(ck2artPo.getField());
        searchCKReq.setQuery(ck2artPo.getQuery());
        searchCKReq.setUtc(ck2artPo.getUtc());

        searchCKReq.setBatchRows(10);
        searchCKReq.setPageSize(10);
        searchCKReq.setPageIndex(1);
        return searchCKReq;
    }

    /**
     * persistent performance analysis data to db
     *
     * @param title
     * @param dDate
     * @param dataMaps
     * @param fields
     * @return
     */
    public Integer persistentPerformance(String title, String dDate, List<DataMap> dataMaps, String[] fields) {
        Integer resCount = 0;
        Map<String, String> data = dataMaps.get(0).getData();
        for (String field : fields) {
            SDPerformanceEntity pojo = new SDPerformanceEntity();
            pojo.setTitle(title);
            if (fields.length > 1) {
                pojo.setTitle(title + "_" + field);
            }
            pojo.setD_date(dDate);
            pojo.set_20(getDoubleVal(data, field + "_20", "0"));
            pojo.set_50(getDoubleVal(data, field + "_50", "0"));
            pojo.set_75(getDoubleVal(data, field + "_75", "0"));
            pojo.set_85(getDoubleVal(data, field + "_85", "0"));
            pojo.set_95(getDoubleVal(data, field + "_95", "0"));
            pojo.set_99(getDoubleVal(data, field + "_99", "0"));
            pojo.set_avg(getDoubleVal(data, field + "_avg", "0"));
            pojo.setCount(getLongVal(data, field + "_count", "0"));

            resCount += persistentToDB(pojo);
        }
        return resCount;
    }

    /**
     * persistent hitrate analysis data to db
     *
     * @param title
     * @param dDate
     * @param dataMaps
     * @param fields
     * @return
     */
    public Integer persistentHitrate(String title, String dDate, List<DataMap> dataMaps, String[] fields) {
        Integer resCount = 0;
        Set<String> hitCodes = QCAppsetting.getSet("CK2Art_HitRateTrue");

        for (String field : fields) {
            Long sumCount = 0L;
            Map<String, Long> codeHit = new HashMap<>();
            Map<String, Long> codeCounts = new HashMap<>();

            for (DataMap dataMap : dataMaps) {
                if (dataMap.getData() == null) {
                    continue;
                }
                Map<String, String> data = dataMap.getData();
                String code = data.getOrDefault("code", "");
                Long codeCount = getLongVal(data, field + "_count", "0");

                if (hitCodes.contains(code)) {
                    codeHit.put(code, codeCount);
                }
                codeCounts.put(code, codeCount);
                sumCount += codeCount;
            }

            if (!codeHit.isEmpty()) {
                //only one code - hit rate
                for (Map.Entry<String, Long> entry : codeHit.entrySet()) {
                    SDHitandsuccessEntity pojo = new SDHitandsuccessEntity();
                    pojo.setTitle(title);
                    if (fields.length > 1 && codeHit.size() > 1) {
                        pojo.setTitle(title + "_" + field + "_" + entry.getKey());
                    } else if (fields.length > 1) {
                        pojo.setTitle(title + "_" + field);
                    } else if (codeHit.size() > 1) {
                        pojo.setTitle(title + "_" + entry.getKey());
                    }

                    pojo.setS_count(entry.getValue());
                    pojo.setF_count(sumCount - entry.getValue());
                    pojo.setCode(entry.getKey());
                    pojo.setAll_count(sumCount);
                    Double hitRate = BigDecimal.valueOf(entry.getValue() * 1.0 / sumCount).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    pojo.setS_rate(hitRate);
                    pojo.setD_date(dDate);

                    resCount += persistentToDB(pojo);
                }
            } else {
                //many specific code - distribute rate
                for (Map.Entry<String, Long> entry : codeCounts.entrySet()) {
                    SDHitandsuccessEntity pojo = new SDHitandsuccessEntity();
                    if (fields.length > 1) {
                        title += "_" + field;
                    }
                    pojo.setTitle(title + "_" + entry.getKey());
                    pojo.setS_count(entry.getValue());
                    pojo.setF_count(0L);
                    pojo.setCode(entry.getKey());
                    pojo.setAll_count(sumCount);
                    Double hitRate = BigDecimal.valueOf(entry.getValue() * 1.0 / sumCount).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    pojo.setS_rate(hitRate);
                    pojo.setD_date(dDate);

                    resCount += persistentToDB(pojo);
                }
            }
        }
        return resCount;
    }

    /**
     * performance data insert analysis result to db&ck
     *
     * @param pojo
     * @return
     */
    public Integer persistentToDB(SDPerformanceEntity pojo) {
        Integer intRes = 0;
        try {
            Map<String, String> trackInfo = JsonUtils.parseMap(JsonUtils.parseJson(pojo));
            Cat.logTags("car-rental-performance", trackInfo, null);

            intRes = updateMySQLDB(pojo);
        } catch (Exception ex) {
            LogUtils.error("CarNotificationConvergeService_insertToDB", ex);
        }
        return intRes;
    }

    /**
     * performance data delete expired and insert new
     *
     * @param pojo
     * @return
     */
    public Integer updateMySQLDB(SDPerformanceEntity pojo) {
        Integer intRes = 0;
        try {
            SDPerformanceDao sdPerformanceDao = new SDPerformanceDao();

            SDPerformanceEntity delPojo = new SDPerformanceEntity();
            delPojo.setTitle(pojo.getTitle());
            delPojo.setD_date(pojo.getD_date());
            List<SDPerformanceEntity> delItems = sdPerformanceDao.queryBy(delPojo);
            sdPerformanceDao.delete(delItems);

            intRes = sdPerformanceDao.insert(pojo);
        } catch (Exception ex) {
            LogUtils.error("CarNotificationConvergeService_updateMySQLDB", ex);
        }
        return intRes;
    }

    /**
     * htirate analysis result to db&ck
     *
     * @param pojo
     * @return
     */
    public Integer persistentToDB(SDHitandsuccessEntity pojo) {
        Integer intRes = 0;
        try {
            Map<String, String> trackInfo = JsonUtils.parseMap(JsonUtils.parseJson(pojo));
            Cat.logTags("car-rental-hitandsuccess", trackInfo, null);

            intRes = updateMySQLDB(pojo);
        } catch (Exception ex) {
            LogUtils.error("CarNotificationConvergeService_insertToDB", ex);
        }
        return intRes;
    }

    /**
     * htirate data delete expired and insert new
     *
     * @param pojo
     * @return
     */
    public Integer updateMySQLDB(SDHitandsuccessEntity pojo) {
        Integer intRes = 0;
        try {
            SDHitandsuccessDao sdHitandsuccessDao = new SDHitandsuccessDao();

            SDHitandsuccessEntity delPojo = new SDHitandsuccessEntity();
            delPojo.setTitle(pojo.getTitle());
            delPojo.setD_date(pojo.getD_date());
            List<SDHitandsuccessEntity> delItems = sdHitandsuccessDao.queryBy(delPojo);
            sdHitandsuccessDao.delete(delItems);

            intRes = sdHitandsuccessDao.insert(pojo);
        } catch (Exception ex) {
            LogUtils.error("CarNotificationConvergeService_updateMySQLDB", ex);
        }
        return intRes;
    }


    /**
     * persistent flow gross result to db
     *
     * @param title
     * @param dDate
     * @param dataMaps
     * @param fields
     * @return
     */
    public Integer persistentFlowAggregate(String title, String dDate, List<DataMap> dataMaps, String[] fields) {
        Integer resCount = 0;
        Map<String, String> data = dataMaps.get(0).getData();
        for (String field : fields) {
            SDFlowAggregateEntity pojo = new SDFlowAggregateEntity();
            pojo.setTitle(title);
            if (fields.length > 1) {
                pojo.setTitle(title + "_" + field);
            }
            pojo.setD_date(dDate);
            pojo.setAll_count(getLongVal(data, field + "_count", "0"));

            resCount += persistentToDB(pojo);
        }
        return resCount;
    }

    /**
     * flow gross result insert to db&ck
     *
     * @param pojo
     * @return
     */
    public Integer persistentToDB(SDFlowAggregateEntity pojo) {
        Integer intRes = 0;
        try {
            Map<String, String> trackInfo = JsonUtils.parseMap(JsonUtils.parseJson(pojo));
            Cat.logTags("car-rental-performance", trackInfo, null);

            intRes = updateMySQLDB(pojo);
        } catch (Exception ex) {
            LogUtils.error("CarNotificationConvergeService_insertToDB", ex);
        }
        return intRes;
    }

    /**
     * flow gross result delete expired and insert new
     *
     * @param pojo
     * @return
     */
    public Integer updateMySQLDB(SDFlowAggregateEntity pojo) {
        Integer intRes = 0;
        try {
            SDFlowAggregateDao sdFlowAggregateDao = new SDFlowAggregateDao();

            SDFlowAggregateEntity delPojo = new SDFlowAggregateEntity();
            delPojo.setTitle(pojo.getTitle());
            delPojo.setD_date(pojo.getD_date());
            List<SDFlowAggregateEntity> delItems = sdFlowAggregateDao.queryBy(delPojo);
            sdFlowAggregateDao.delete(delItems);

            intRes = sdFlowAggregateDao.insert(pojo);
        } catch (Exception ex) {
            LogUtils.error("CarNotificationConvergeService_updateMySQLDB", ex);
        }
        return intRes;
    }


    public Double getDoubleVal(Map<String, String> data, String key, String defVal) {
        return Double.valueOf(getCkData(data, key, defVal));
    }

    public Long getLongVal(Map<String, String> data, String key, String defVal) {
        return Long.valueOf(getCkData(data, key, defVal));
    }

    /**
     * if map key's value is null will occur NPE
     *
     * @param data
     * @param key
     * @param defVal
     * @return
     */
    public String getCkData(Map<String, String> data, String key, String defVal) {
        if (data == null) {
            return defVal;
        }
        //contain key but value null
        String dataVal = data.getOrDefault(key, defVal);
        if (StringUtils.isNotBlank(dataVal)) {
            return dataVal;
        } else {
            return defVal;
        }
    }
}
