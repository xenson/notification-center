package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.basic.EncryptUtils;
import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.dto.*;
import com.ctrip.car.osd.notificationcenter.entity.*;
import com.ctrip.car.osd.notificationcenter.enums.SearchModels;
import com.ctrip.car.osd.notificationcenter.module.DashboardResultModule;
import com.ctrip.car.osd.notificationcenter.proxy.ClickHouseProxy;
import com.ctrip.car.osd.notificationcenter.proxy.HickwallVMDataProxy;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by xiayx on 2021/11/4.
 */
@Service
public class SearchTrackDBService {
    @Autowired
    private ClickHouseProxy clickHouseProxy;
    @Autowired
    private DashboardResultModule dashboardResultModule;

    /**
     * search clickhouse
     *
     * @param request
     * @return
     */
    public SearchCKResponseType searchCK(SearchCKRequestType request) {
        SearchCKResponseType response = new SearchCKResponseType();
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
        Integer utc = request.getUtc();
        if (Objects.nonNull(utc) && utc.equals(0)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }

        //model get querykey - 0.常规字段查询 1.字段性能聚合统计 2.字段数量分布聚合统计
        int model = SearchModels.findByAny(request.getModel()).getValue();
        String queryKey = QCAppsetting.getList("ClickHouse_QueryKeys").get(model);

        String select = request.getFields();
        String from = request.getDb();
        String start = DateTimeUtils.parseStr(
                DateTimeUtils.localToUTC(DateTimeUtils.tryParseLDate(request.getStart()).get()), dateFormat);
        String end = DateTimeUtils.parseStr(
                DateTimeUtils.localToUTC(DateTimeUtils.tryParseLDate(request.getEnd()).get()), dateFormat);
        String where = request.getQuery();
        String groupby = "";
        String orderby = "";
        String limit = request.getBatchRows() != null ? request.getBatchRows().toString() : "100";

        //log search
        if (model == 0) {
            orderby = QCAppsetting.get("ClickHouse_LogGroup");
        }
        //performance search
        if (model == 1) {
            String perfSelect = QCAppsetting.get("ClickHouse_PerfSelect");
            String[] selects = request.getFields().split(",");
            StringBuffer sbSelect = new StringBuffer();
            for (String filed : selects) {
                sbSelect.append(MessageFormat.format(perfSelect, filed) + ",");
            }
            select = sbSelect.substring(0, sbSelect.length() - 1);
        }
        //hit ratio search
        if (model == 2) {
            String ratioSelectFormat = QCAppsetting.get("ClickHouse_RateSelect");
            String groupByFormat = QCAppsetting.get("ClickHouse_RateGroup");

            String ratioSelect = MessageFormat.format(ratioSelectFormat, select);
            groupby = MessageFormat.format(groupByFormat, select);

            select = ratioSelect;
        }
        //flow gross search
        if (model == 3) {
            String flowSelectFormat = QCAppsetting.get("ClickHouse_FlowSelect");
            String flowSelect = MessageFormat.format(flowSelectFormat, select);

            select = flowSelect;
        }
        where = where + " " + groupby + " " + orderby;

        //参数顺序敏感,日志访问模板:http://hickwall.ctripcorp.com/pd/#/log/sql_audit/detail/2451
        /**
         * http://osgconsole.ops.ctripcorp.com/#/static/service/apiInfo/10917
         * http://osg.ops.ctripcorp.com/api/10917
         * OSG参数示例:
         * {
         *     "access_token": "fd5e4756484e0feb20a76f4af54e521c",
         *     "request_body": {
         *         "params": [
         *             "title,keyfrom,key,cost,cat_client_appid,timestamp,ip,requestId,sourceFrom",
         *             "log.car_rental_blueprint_all",
         *             "key='184339' AND apiName='preBookingV2' AND isSuccess='false'",
         *             "100"
         *         ],
         *         "query_key": "8fed3f08-aa9c-4c9a-9bac-4d7380d690c1"
         *     }
         * }
         */
        List<String> params = Arrays.asList(select, from, start, end, where, limit);
        String cacheKey = calculateCacheKey(request);
        CKRootEntity ckdata = clickHouseProxy.searchClickHouse(cacheKey, queryKey, params);
        if (ckdata == null || CollectionUtils.isEmpty(ckdata.getData())) {
            ckdata = clickHouseProxy.searchClickHouse(queryKey, params);
        }

        //CK结果字段拆分成map返回
        if (ckdata != null && ckdata.getRows() > 0 && CollectionUtils.isNotEmpty(ckdata.getData())) {
            List<DataMap> dataset = new ArrayList<>();
            response.setQueryRows(ckdata.getData().size());

            for (Map<String, String> item : ckdata.getData()) {
                DataMap dataMap = new DataMap();
                if (item.containsKey("timestamp")) {
                    item.put("timestamp", DateTimeUtils.parseStr(DateTimeUtils.utcToLocal(item.get("timestamp"))));
                }
                dataMap.setData(item);
                dataset.add(dataMap);
            }

            if (request.getPageSize() != null && request.getPageIndex() != null) {
                response.setDataset(pagingCKData(dataset, request.getPageSize(), request.getPageIndex()));
            } else {
                response.setDataset(dataset);
            }
        }
        return response;
    }

    /**
     * paging clickhouse result
     *
     * @param dataMapList
     * @param pageSize
     * @param pageIndex
     * @return
     */
    private List<DataMap> pagingCKData(List<DataMap> dataMapList, int pageSize, int pageIndex) {
        int metaSize = dataMapList.size();
        int maxPage = (metaSize % pageSize) > 0 ? (metaSize / pageSize) + 1 : (metaSize / pageSize);

        int fromIndex, toIndex;
        if (pageIndex >= maxPage) {
            fromIndex = (maxPage - 1) * pageSize;
            toIndex = metaSize;
        } else {
            fromIndex = (pageIndex - 1) * pageSize;
            toIndex = pageIndex * pageSize;
        }
        return dataMapList.subList(fromIndex, toIndex);
    }

    /**
     * construct CacheKey by request parameters
     *
     * @param request
     * @return
     */
    private String calculateCacheKey(SearchCKRequestType request) {
        StringBuffer sbKey = new StringBuffer(request.getModel() + "_");
        sbKey.append(request.getDb() + "_");
        sbKey.append(request.getFields() + "_");
        sbKey.append(request.getQuery() + "_");
        sbKey.append(request.getStart() + "_");
        sbKey.append(request.getEnd() + "_");
        sbKey.append(request.getBatchRows());

        return EncryptUtils.getMD5(sbKey.toString());
    }

    /**
     * search hickwall
     *
     * @param request
     * @return
     */
    public SearchHWResponseType searchHW(SearchHWRequestType request) {
        SearchHWResponseType response = new SearchHWResponseType();
        Timestamp start = DateTimeUtils.tryParseTimestamp(request.getStart()).get();
        Timestamp end = DateTimeUtils.tryParseTimestamp(request.getEnd()).get();

        HickwallVMDataRootEntity res = HickwallVMDataProxy.searchHickwallVM(request.getQuery(), request.getDb(), start, end, request.getStep());
        List<HWData> dataset = new ArrayList<>();
        response.setQueryRows(0);
        if (res != null && res.getData() != null && CollectionUtils.isNotEmpty(res.getData().getResult())) {
            if (res.getData().getResult().get(0) != null && CollectionUtils.isNotEmpty(res.getData().getResult().get(0).getValues())) {
                response.setQueryRows(res.getData().getResult().get(0).getValues().size());

                for (List<String> hwItem : res.getData().getResult().get(0).getValues()) {
                    HWData hwData = new HWData();
                    hwData.setTimeNode(DateTimeUtils.parseStr(DateTimeUtils.tryParseLDate(hwItem.get(0)).get()));
                    hwData.setValue(hwItem.get(1));

                    dataset.add(hwData);
                }
                response.setDataset(dataset);
            }
        }
        return response;
    }

    /**
     * search dashboard metric
     *
     * @param request
     * @return
     */
    public SearchDashboardResponseType searchDashboard(SearchDashboardRequestType request) {
        SearchDashboardResponseType response = new SearchDashboardResponseType();

        Timestamp start = DateTimeUtils.tryParseTimestamp(request.getStart()).get();
        Timestamp end = DateTimeUtils.tryParseTimestamp(request.getEnd()).get();

        List<DashboardAggregateEntity> dataList = dashboardResultModule.dashboardAggregateResults(request.getMetricName(),
                request.getAppId(), start, end, request.getAggregator(), request.getGroupBy());

        List<DashboardData> dataset = new ArrayList<>();
        response.setQueryRows(dataList.size());
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (DashboardAggregateEntity dashboardItem : dataList) {
                DashboardData dashboardData = new DashboardData();
                dashboardData.setTag(dashboardItem.getApiName());
                dashboardData.setValue(dashboardItem.getAggregate().toString());

                dataset.add(dashboardData);
            }
            response.setDataset(dataset);
        }
        return response;
    }

}
