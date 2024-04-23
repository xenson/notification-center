package com.ctrip.car.osd.notificationcenter.service;

import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import com.ctrip.car.osd.notificationcenter.basic.DateTimeUtils;
import com.ctrip.car.osd.notificationcenter.basic.HttpUtils;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;
import com.ctrip.car.osd.notificationcenter.config.QCHickwall;
import com.ctrip.car.osd.notificationcenter.dto.DemoApiRequestType;
import com.ctrip.car.osd.notificationcenter.dto.DemoApiResponseType;
import com.ctrip.car.osd.notificationcenter.entity.QscheduleParameter;
import com.ctrip.car.osd.notificationcenter.entity.TrackTypeEnum;
import com.ctrip.car.osd.notificationcenter.entity.demo.GPTRootDto;
import com.ctrip.car.osd.notificationcenter.jobs.CarNotificationConvergeWorker;
import com.ctrip.car.osd.notificationcenter.kafka.KafkaMQConsumer;
import com.ctrip.car.osd.notificationcenter.kafka.MqDisassemble;
import com.ctrip.car.osd.notificationcenter.module.DemoModule;
import com.ctrip.car.osd.notificationcenter.qmq.QmqProducerDemo;
import com.ctrip.car.osd.notificationcenter.sdk.qmq.TrackProducer;
import com.ctrip.car.osd.notificationcenter.sdk.qmq.TrackProducers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.producer.MessageProducerProvider;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xiayx on 2019/11/6.
 */
@Service
public class DemoApiService {
//    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DemoModule demoModule;
    @Autowired
    CarAppMonitorService carAppMonitorService;
    @Autowired
    QmqProducerDemo qmqProducerDemo;
    @Autowired
    FrontEndPerformanceService frontEndPerformanceService;
    @Autowired
    CarNotificationConvergeService carNotificationConvergeService;

    @Autowired
    //装配Bean不指定,竞争注入,@Primary注入优先
    private TrackProducer trackProducer;
    @Autowired
    //装配Bean指定 名称:messageProducerProvider
    //@Qualifier("QmqConfigTestProducer")
    private MessageProducerProvider QmqConfigTestProducer;

    public DemoApiResponseType demoApiProcess(DemoApiRequestType demoApiRequest) throws SQLException {
        DemoApiResponseType response = new DemoApiResponseType();
        QscheduleParameter qscheduleParameter = new QscheduleParameter();
        qscheduleParameter.setDateGap(demoApiRequest.getReqMsg());

        if (demoApiRequest.getTrackType().equals(TrackTypeEnum.trackSearching)) {
            carNotificationConvergeService.aggregationAnalysisDay(qscheduleParameter);
        }

        if (demoApiRequest.getReqMsg().equals("appmonitor")) {
            carAppMonitorService.constructCache();
            carAppMonitorService.sendAppMonitorMail();
        }


        trackProducer.sendMessage(demoApiRequest.getExtendInfo());

        Message message = QmqConfigTestProducer.generateMessage("ctrip.car.osd.vehicle.product.request.response.gzip.subject");
        message.setProperty("request", "test");
        //查询列表的结果比较大,放入redis
        QmqConfigTestProducer.sendMessage(message);

        TrackProducers.sendMessage(demoApiRequest.getExtendInfo());


        return response;

//        //start kafka listener
////        String json = "{\"meta\":{\"id\":\"34d39f27-397e-11ed-b7f4-fa163ee36391\",\"sendTime\":1663744973794,\"receiveTime\":1663744973330,\"userAccount\":\"ctrip\",\"eventType\":\"Custom\",\"version\":1,\"sender\":\"Mobile-SDK/3.5.3.0\",\"receiver\":\"mechanic-tcp/10.60.132.184\",\"sequenceNum\":543708,\"resendCount\":0,\"routingKeys\":[]},\"context\":{\"vid\":\"5945183075BA11ECEEBA0F1C66AA4FDC\",\"sid\":182,\"pvid\":5116,\"ts\":1663744973678,\"page\":\"222013\",\"url\":null,\"v\":null,\"fp\":null,\"clientId\":\"32001059110279682991\",\"tid\":null,\"userAgent\":null,\"ip\":\"117.136.88.60\",\"type\":\"NativeApp\",\"referrer\":null,\"fromPv\":null,\"fromPage\":null,\"newVisitor\":null,\"newSession\":null,\"appId\":\"481001\",\"moduleId\":null,\"callId\":null,\"duration\":null,\"requestId\":null,\"organizationId\":\"36\",\"businessTypeId\":\"14\",\"channelId\":\"2616\",\"launchId\":1559},\"agent\":{\"manufacturer\":\"HUAWEI\",\"deviceName\":\"TET-AN50\",\"deviceVer\":null,\"osName\":\"Android\",\"osVer\":\"12\",\"engineName\":null,\"engineVer\":null,\"browserName\":null,\"browserVer\":null,\"screen\":\"1160*2574\",\"colorDepth\":null,\"dpr\":null,\"language\":\"zh_CN_#Hans\",\"viewport\":null,\"platform\":\"32\",\"appName\":\"ctrip.android.view\",\"appVer\":\"8.52.0\",\"appInternalVer\":null,\"network\":\"4G\",\"carrier\":\"中国移动\",\"deviceType\":null,\"browserType\":null,\"generalType\":null,\"cracked\":false,\"emu\":false,\"deviceId\":{\"mac\":\"02:00:00:00:00:11\",\"imei\":\"\",\"idfa\":\"\",\"oaid\":\"00000000-0000-0000-0000-000000000000\",\"oaidTrackLimited\":null}},\"geo\":{\"isp\":\"中国移动\",\"longitude\":111.449451,\"latitude\":27.233938,\"countryIsoCode\":\"CN\",\"country\":\"中国\",\"region\":\"湖南\",\"city\":\"邵阳\",\"org\":\"\",\"timeZone\":\"UTC+8\",\"gpsLongitude\":113.150059,\"gpsLatitude\":28.911502,\"gpsCity\":\"汨罗\",\"deviceTimeZone\":\"Asia/Shanghai\",\"gisCountryIsoCode\":\"CN\",\"gisCity\":null,\"gpsCountry\":\"中国\",\"gpsRegion\":\"湖南\",\"gpsCityId\":\"7560\",\"countryId\":1,\"provinceId\":21,\"cityId\":1111},\"user\":{\"id\":\"_WeChat407959066\",\"token\":null,\"login\":null,\"loginName\":null,\"grade\":null,\"corp\":null},\"experiment\":null,\"custom\":{\"key\":\"c_car_trace_fetch_cost_time\",\"data\":{\"env_clientcode\":\"32001059110279682991\",\"env_imsi\":\"\",\"env_DUID\":\"u=A22D837F88CE550A1DA16517B9CEAF73F71F3B9C37186641CF0E12A395AFFBFA&v=0\",\"env_romVersion\":\"EmotionUI_13.0.0\",\"meta_timezone\":\"Asia/Shanghai\",\"env_appVersion\":\"852.000\",\"env_buildID\":\"15561881\",\"env_isEmulatorV2\":\"false\",\"meta_pageid_relation\":\"1\",\"env_extendsourceid\":\"\",\"env_ouid\":\"\",\"info\":\"{\\\"baseData\\\":{\\\"CRNModuleName\\\":\\\"rn_car_app\\\",\\\"ParentDistibutionChannelId\\\":\\\"\\\",\\\"abVersion\\\":\\\"220714_VAC_tab6|A,210323_DSJT_rlj|B,210413_DSJT_lbyxg|B,220727_DSJT_wafal|B,220902_DSJT_bqyhn|A\\\",\\\"age\\\":\\\"30~60\\\",\\\"alliSid\\\":\\\"1\\\",\\\"allianceId\\\":\\\"\\\",\\\"awakeTime\\\":\\\"\\\",\\\"beijingTime\\\":\\\"2022-09-21 15:22:53\\\",\\\"businessType\\\":\\\"35\\\",\\\"channelId\\\":\\\"\\\",\\\"countryCode\\\":\\\"\\\",\\\"countryName\\\":\\\"\\\",\\\"creditRentAbVersion\\\":\\\"B\\\",\\\"creditVersion\\\":\\\"\\\",\\\"crnVersion\\\":\\\"8.52.0\\\",\\\"currency\\\":\\\"CNY\\\",\\\"currentTime\\\":\\\"2022-09-21 15:22:53\\\",\\\"defaultAge\\\":\\\"1\\\",\\\"devicePlatform\\\":\\\"android\\\",\\\"distibutionChannelId\\\":\\\"17671\\\",\\\"distributionChannelId\\\":\\\"17671\\\",\\\"dropOffCityId\\\":510,\\\"dropOffCityName\\\":\\\"信阳\\\",\\\"dropOffDateTime\\\":\\\"2022-09-24 17:30:00\\\",\\\"dropOffLat\\\":\\\"32.143914\\\",\\\"dropOffLng\\\":\\\"114.159428\\\",\\\"dropOffLocationCode\\\":\\\"118267\\\",\\\"dropOffLocationName\\\":\\\"信阳东站\\\",\\\"dropOffLocationType\\\":2,\\\"dropOffPoiVersion\\\":\\\"3\\\",\\\"isDifferentLocation\\\":\\\"0\\\",\\\"isHomeCombine\\\":\\\"1\\\",\\\"isNewDetail\\\":\\\"1\\\",\\\"isPickupCar\\\":\\\"2\\\",\\\"isSendCar\\\":\\\"2\\\",\\\"language\\\":\\\"cn\\\",\\\"listInPage\\\":\\\"1\\\",\\\"listIsBatch\\\":\\\"0\\\",\\\"locale\\\":\\\"zh_cn\\\",\\\"logIdentification\\\":\\\"FRONT_END_APP\\\",\\\"openid\\\":\\\"\\\",\\\"orderId\\\":\\\"\\\",\\\"orderstatus\\\":\\\"\\\",\\\"pageId\\\":\\\"222013\\\",\\\"partialVersion\\\":\\\"20220920211058\\\",\\\"pickUpLat\\\":\\\"32.143914\\\",\\\"pickUpLng\\\":\\\"114.159428\\\",\\\"pickUpPoiVersion\\\":\\\"3\\\",\\\"pickupCityId\\\":510,\\\"pickupCityName\\\":\\\"信阳\\\",\\\"pickupDateTime\\\":\\\"2022-09-21 17:30:00\\\",\\\"pickupLocationCode\\\":\\\"118267\\\",\\\"pickupLocationName\\\":\\\"信阳东站\\\",\\\"pickupLocationType\\\":2,\\\"platHomeModuleInfo\\\":\\\"\\\",\\\"platform\\\":\\\"app_ctrip\\\",\\\"queryRecommendId\\\":\\\"\\\",\\\"queryVid\\\":\\\"e2c9ea9b-e314-488a-9f8f-c9d640698e9e\\\",\\\"residency\\\":\\\"1\\\",\\\"sId\\\":\\\"1\\\",\\\"scene\\\":\\\"\\\",\\\"serverRequestId\\\":\\\"92z7789UJ6f332146g5T\\\",\\\"site\\\":\\\"cn\\\",\\\"socactivityid\\\":\\\"\\\",\\\"sourceFrom\\\":\\\"ISD_C_APP\\\",\\\"sourceId\\\":\\\"8061\\\",\\\"standardLocale\\\":\\\"zh-CN\\\",\\\"staticQuery\\\":\\\"{}\\\",\\\"telephone\\\":\\\"4006329662\\\",\\\"trip-app-code\\\":\\\"1\\\",\\\"trip-business-code\\\":\\\"1\\\",\\\"trip-os-code\\\":\\\"1\\\",\\\"trip-subBusiness-code\\\":\\\"0\\\",\\\"trip-tech-code\\\":\\\"2\\\",\\\"uId\\\":\\\"_WeChat407959066\\\",\\\"visitortraceId\\\":\\\"\\\"},\\\"info\\\":{\\\"afterFetch\\\":1.663744973663E12,\\\"baseResponse\\\":{\\\"apiResCodes\\\":[],\\\"code\\\":\\\"0\\\",\\\"errorCode\\\":\\\"0\\\",\\\"extMap\\\":{},\\\"isSuccess\\\":true,\\\"message\\\":\\\"操作成功\\\",\\\"requestId\\\":\\\"913cd6fb-40c7-4b38-a8e3-0e6f98ad183d\\\",\\\"returnMsg\\\":\\\"操作成功\\\",\\\"showMessage\\\":\\\"操作成功\\\"},\\\"beforeFetch\\\":1.663744973172E12,\\\"cacheFetchCost\\\":0,\\\"cacheFrom\\\":\\\"\\\",\\\"environmentCost\\\":0,\\\"eventResult\\\":true,\\\"expCode\\\":\\\"0\\\",\\\"expMsg\\\":\\\"操作成功\\\",\\\"fetchCost\\\":491,\\\"isCacheValid\\\":true,\\\"isFromCache\\\":false,\\\"isSuccess\\\":true,\\\"networkCost\\\":491,\\\"parentRequestId\\\":\\\"\\\",\\\"request\\\":{\\\"baseRequest\\\":{\\\"allianceInfo\\\":{\\\"allianceId\\\":0,\\\"distributorUID\\\":\\\"1\\\",\\\"ouid\\\":\\\"1\\\",\\\"sid\\\":1},\\\"channelId\\\":17671,\\\"channelType\\\":7,\\\"clientVersion\\\":\\\"20220920211058\\\",\\\"clientid\\\":\\\"32001059110279682991\\\",\\\"currencyCode\\\":\\\"CNY\\\",\\\"extMap\\\":{},\\\"extraMaps\\\":{\\\"abVersion\\\":\\\"220714_VAC_tab6|A,210323_DSJT_rlj|B,210413_DSJT_lbyxg|B,220727_DSJT_wafal|B,220902_DSJT_bqyhn|A\\\",\\\"aboutToTravelVersion\\\":\\\"1\\\",\\\"batchVersion\\\":\\\"\\\",\\\"channelId\\\":\\\"17671\\\",\\\"commentVersion\\\":1,\\\"correctStatus\\\":\\\"\\\",\\\"creditVersion\\\":\\\"\\\",\\\"crnVersion\\\":\\\"33\\\",\\\"ctripVersion\\\":1,\\\"depositVersion\\\":\\\"1.0\\\",\\\"detailPageVersion\\\":\\\"2\\\",\\\"directRenewalVersion\\\":\\\"1\\\",\\\"ehaiDepositVersion\\\":\\\"1.0\\\",\\\"feeGroup\\\":\\\"1\\\",\\\"filterProject\\\":\\\"B\\\",\\\"goodsShelves\\\":\\\"2\\\",\\\"groupNameVersion\\\":\\\"1\\\",\\\"insVersion\\\":\\\"1\\\",\\\"insuranceDetail\\\":0,\\\"isFilterPrice\\\":\\\"1\\\",\\\"isNewRecommend\\\":\\\"1\\\",\\\"isNewSearchNoResult\\\":\\\"1\\\",\\\"karabiVersion\\\":\\\"1\\\",\\\"labelOptimizeVer\\\":\\\"\\\",\\\"lateDepositVer\\\":\\\"3\\\",\\\"membershipRightsV\\\":\\\"1\\\",\\\"newPayment\\\":1,\\\"onePost\\\":\\\"1\\\",\\\"orderDetailCallBack\\\":1,\\\"orderDetailRestStruct\\\":\\\"1\\\",\\\"orderId\\\":\\\"\\\",\\\"pageVersion\\\":\\\"true\\\",\\\"partialVersion\\\":\\\"20220920211058\\\",\\\"payOnlineVersion\\\":\\\"1\\\",\\\"platform\\\":\\\"app_ctrip\\\",\\\"poiNewVersion\\\":\\\"1\\\",\\\"poiProject\\\":\\\"B\\\",\\\"priceUnitedVersion\\\":\\\"2\\\",\\\"queryProductsVersion\\\":\\\"cashbackDemand\\\",\\\"queryVid\\\":\\\"a292b44b-457b-4200-8acb-50d5087a8665\\\",\\\"rankingVersion\\\":\\\"2\\\",\\\"receiveCoupon\\\":\\\"1\\\",\\\"rentCenter\\\":\\\"1\\\",\\\"serverRequestId\\\":\\\"92z7789UJ6f332146g5T\\\",\\\"snapshotVersion\\\":\\\"v4\\\",\\\"sourceFrom\\\":\\\"ISD_C_APP\\\",\\\"streamVersion\\\":true,\\\"tangramAbt\\\":\\\"B\\\",\\\"telVersion\\\":\\\"1\\\",\\\"trip-app-code\\\":1,\\\"trip-business-code\\\":1,\\\"trip-os-code\\\":1,\\\"trip-subBusiness-code\\\":0,\\\"trip-tech-code\\\":2,\\\"vehicleDamageAuditVersion\\\":\\\"1\\\"},\\\"extraTags\\\":{\\\"abVersion\\\":\\\"220714_VAC_tab6|A,210323_DSJT_rlj|B,210413_DSJT_lbyxg|B,220727_DSJT_wafal|B,220902_DSJT_bqyhn|A\\\",\\\"aboutToTravelVersion\\\":\\\"1\\\",\\\"commentVersion\\\":1,\\\"ctripVersion\\\":1,\\\"poiNewVersion\\\":\\\"1\\\",\\\"poiProject\\\":\\\"B\\\"},\\\"invokeFrom\\\":\\\"\\\",\\\"language\\\":\\\"cn\\\",\\\"locale\\\":\\\"zh_cn\\\",\\\"mobileInfo\\\":{\\\"customerGPSLat\\\":0,\\\"customerGPSLng\\\":0,\\\"mobileModel\\\":\\\"HUAWEI_TET-AN50\\\",\\\"wirelessVersion\\\":\\\"8.52.0\\\"},\\\"pageId\\\":\\\"\\\",\\\"parentRequestId\\\":\\\"\\\",\\\"patternType\\\":\\\"35\\\",\\\"platform\\\":\\\"app_ctrip\\\",\\\"requestId\\\":\\\"913cd6fb-40c7-4b38-a8e3-0e6f98ad183d\\\",\\\"site\\\":\\\"cn\\\",\\\"sourceCountryId\\\":1,\\\"sourceFrom\\\":\\\"ISD_C_APP\\\",\\\"vid\\\":\\\"1663744673915.ptwrc0\\\"},\\\"scenes\\\":0},\\\"requestId\\\":\\\"913cd6fb-40c7-4b38-a8e3-0e6f98ad183d\\\",\\\"rootMessageId\\\":\\\"921822-0a3d5164-462151-2195437\\\",\\\"setCacheCost\\\":0,\\\"url\\\":\\\"18631/getReceivePromotion\\\"}}\",\"env_serialNum\":\"unknown\",\"ubt_webserver_idc\":\"SHARB\",\"meta_sdkver\":\"3.5.3.0\",\"env_osVersion\":\"12\",\"meta_proxy\":\"NONE\",\"env_countryType\":\"0\",\"env_networkType\":\"4G\",\"meta_androidromver\":\"103.0.0.116C00\",\"$.ubt.hermes.topic.classifier\":\"DebugCustom\",\"__api_version\":\"v2\",\"meta_scale\":\"3.25\",\"env_country\":\"中国\",\"meta_pname\":\"ctrip.android.view\",\"log_from\":\"crn\",\"env_screenHeight\":\"2574\",\"env_carrier\":\"移动\",\"env_cpuSupportABI\":\"arm64-v8a;armeabi-v7a;armeabi\",\"env_sid\":\"509782\",\"env_mac\":\"020000000000\",\"meta_persist_vid\":\"82EB29C0147D11ECFEAE42DB39D141E9\",\"meta_loadDBTs\":\"1663744973792\",\"env_screenWidth\":\"1160\",\"env_cityID\":\"7560\",\"env_allianceid\":\"67751\",\"__product_name\":\"rn_car_app\",\"seq\":\"543708\",\"meta_screen_rel\":\"1160*2700\",\"env_androidID\":\"d10b974faf51453a\",\"env_deviceType\":\"TET-AN50\",\"sourceID\":\"8061\",\"meta_procedure_upgrade\":\"true\",\"meta_usb\":\"0\",\"env_root\":\"false\",\"env_preSourceId\":\"33440004\",\"env_isSupport\":\"true\",\"env_os\":\"Android\",\"meta_androidrom\":\"Harmony\",\"meta_retry_times\":\"0\",\"env_logtime\":\"2022-09-21 15:12:09\",\"meta_launchId\":\"1559\",\"env_city\":\"汨罗\",\"env_PushSwitch\":\"1\",\"env_isWechatWakeUp\":\"false\",\"meta_imei\":\"\",\"env_province\":\"湖南\",\"keyOrgId\":\"36\"},\"keyId\":129989,\"tiled\":\"true\"}}";
////        String topic = "car.rd.devtrace.dispatched.full";
////        MqDisassemble.disassembleKafkaMq(json, topic);
//
//        KafkaMQConsumer kafkaMQConsumer = new KafkaMQConsumer();
//        if (demoApiRequest.getReqMsg().equals("biplatform_Consumer")) {
//            kafkaMQConsumer.biplatform_Consumer();
//        } else if (demoApiRequest.getReqMsg().equals("rddevtrace_Consumer")) {
//            kafkaMQConsumer.rddevtrace_Consumer();
//        } else if (demoApiRequest.getReqMsg().equals("listenerConsumerStart")) {
//            kafkaMQConsumer.listenerConsumerStart();
//        }
//
//        try {
//            if (demoApiRequest.getTrackType().equals(TrackTypeEnum.trackPage)) {
//                if (StringUtils.isNotBlank(demoApiRequest.getReqMsg())) {
//                    String[] dataDates = demoApiRequest.getReqMsg().split(",");
//                    for (String dataDate : dataDates) {
//                        frontEndPerformanceService.sendAPMPerformanceMail(DateTimeUtils.tryParseLDate(dataDate).orElse(null));
//                    }
//                } else {
//                    frontEndPerformanceService.sendAPMPerformanceMail(null);
//                }
//            }
//        } catch (Exception ex) {
//
//        }

//        List<Map<String, String>> filters  = QCHickwall.getMapList("Blueprint_HickwallFilter");
//        String key = "UBT_FAIL_INFO";
//        Map<String, String> value = new HashMap<>();
//        value.put("requestId", "4e2a096d-5922-4a81-9fd4-d204a6471a4d");
//        value.put("serviceName", "CBSBookingServiceV3");
//        value.put("sourceFrom", "DISTRIBUTION_API");
//        value.put("uid", "_U2662376566");
//        value.put("vendorId", "68692");
//        value.put("isSuccess", "true");
//        Map<String, String> message = new HashMap<>();
//        message.put(key, JsonUtils.parseJson(value));
//        qmqProducerDemo.sendMessage("car.sd.notificationcenter.alert", message);

//        try {
//            String recipient = "xiayx@trip.com;lfsun@trip.com";
//            String recipientName = "xiayx@trip.com;lfsun@trip.com";
//            String sender = "xiayx@trip.com";
//            String senderName = "test sender";
//            String cc = "xiayx@trip.com;lfsun@trip.com";
//            String subject = "test title/subject";
//            String bodyContent = "test content/bodyContent";
//
//            EmailSend.sendMail(recipient, recipientName, sender, senderName, cc, subject, bodyContent, new ArrayList<>());
//        } catch (Exception ex){
//
//        }

//        List<KeyMapEntity> keyList = JsonUtils.parseArray(QConfigAppsetting.get("Trace_KeyId_Map"), KeyMapEntity.class);
//        Map<String, Integer> keyMap = keyList.stream().collect(Collectors.toMap(KeyMapEntity::getKey, KeyMapEntity::getKeyId));
//        String keyId = "";
//        String key = "USER_ORBIT";
//        if (StringUtils.isEmpty(keyId) && StringUtils.isNotEmpty(key)) {
//            keyId = keyMap.get(key).toString();
//        }
//
//
//        String plainText = "<?xml version='1.0'?><XML_DATA><Header><Version>1.0</Version><TransactionType>NSell</TransactionType><TransactionId>2011010100001</TransactionId><CallbackUrl><![CDATA[http://st.com/back]]></CallbackUrl></Header><Segment><PolicyIn><ProductCode>JDARVTBPN1I</ProductCode><OriginatingCity>LosAngeles</OriginatingCity><FurthestCity>LosAngeles</FurthestCity><TransactionApplDate>2020-01-13 10:00:00</TransactionApplDate><TransactionEffDate>2020-02-13 10:00:00</TransactionEffDate><TransactionExpDate>2020-02-14 09:59:59</TransactionExpDate><Remark>11844831073_74</Remark></PolicyIn><Insured><Relation>Policyholder</Relation><Name>LUO/JUNWEI</Name><Gender>Male</Gender><BirthDt>1990-01-01 10:00:00</BirthDt><InsuredIdNoType>6</InsuredIdNoType><InsuredIdNo>11844831073</InsuredIdNo><HomePhoneNo>15618532293</HomePhoneNo><EmailAddr>xia@ctrip.com</EmailAddr><IsInsuredFlag>1</IsInsuredFlag></Insured></Segment></XML_DATA>1268152202cb962ac59075b964b07152d234b70";
//        //String plainText = "<?xml version='1.0'?><XML_DATA><Header><Version>1.0</Version><TransactionType>Quote</TransactionType><TransactionId>2011010100001</TransactionId></Header><Segment><PolicyIn><ProductCode>JDARVTBPN1I</ProductCode><TransactionEffDate>2020-02-10 00:00:00</TransactionEffDate><TransactionExpDate>2020-02-17 23:59:59</TransactionExpDate><CoverageItem><CoverageCode>0510</CoverageCode><CoverageAmount>2000.00</CoverageAmount></CoverageItem><CoverageItem><CoverageCode>0539</CoverageCode><CoverageAmount>2000.00</CoverageAmount></CoverageItem></PolicyIn><Insured><Name>ctrip_test</Name><BirthDt>1980-03-15</BirthDt><IsInsuredFlag>2</IsInsuredFlag></Insured></Segment></XML_DATA>1268152202cb962ac59075b964b07152d234b70";
//
//        String secretText = MD5Util.getMD5ofStr(plainText).toLowerCase();
//
//
//        DemoApiResponseType response = new DemoApiResponseType();
//        String returnMsg = demoModule.constructResponseMsg(demoApiRequest.getReqMsg());
//        response.setReturnMsg(returnMsg);
//
//        Map<String, String> info = new HashMap<>();
//        info.put("test", "info");
//        LogUtils.info(logger, "test_demo", "demo.info", info);
//        LogUtils.error(logger, "test_demo", "demo.error", new Exception("demo.error log"));
//
////        //best
////        LogUtil.info("demo.info", "demo.info log");
////        LogUtil.warn("demo.error", "demo.error log");
////        LogUtil.error("demo.error", new Exception("demo.error log"));
//
//        LogUtility.info("demoApiProcess");
//        LogUtility.info("demoApiProcess", "info test");
//        LogUtility.error("demoApiProcess", new Exception("error test"));
//        LogUtility.error("demoApiProcess", "test");
//        LogUtility.warn("demoApiProcess", new Exception("warn test"));

//        DemoApiResponseType response = new DemoApiResponseType();
//        String returnMsg = demoModule.constructResponseMsg(demoApiRequest.getReqMsg());
//        response.setReturnMsg(returnMsg);

//        String res;
//        try {
//            String url = "https://user.qunar.com/webapi/contact/query";
//            Map<String, String> params = new HashMap<>();
//            params.put("qcookie", "U.jlkajdd8217");
//            params.put("vcookie", "Ob8-LZak8Znzgznm4W1mMlyNyp_PLkXFADdpOLTFlJYXD1MDpF9g6LoT28kblp0U2UF70Z4GobwL5OlIFoYLvyGHOX1LFLjS_SCEvUSOuCCw-Q62eHxxPFgzEWqKvNmDfS3Lop3BTxnzymuGMD67JLmgbX9_xLcd-bmUvyN96AcB");
//            params.put("tcookie", "26828802");
//            params.put("attribute", "credentials,travelcard");
//
//            //res = HttpUtils.doGet(url, params);
//            //res = HttpUtils.doPost(url, params);
//            res = HttpUtils.doPostProxy(url, null, params);
//
//        } catch (Exception ex) {
//            res = ex.toString();
//        }
//        response.setReturnMsg(res);

//        azureOpenAIServiceDemo(demoApiRequest, response);

//        return response;
    }

//    public void azureOpenAIServiceDemo(DemoApiRequestType demoApiRequest, DemoApiResponseType response) {
//        try {
//            //一共有几条规则,休息日加班时间上限是多少
//            String overTimeRule = "加班管理规定,内容如下:第一条（目的） 为了明确加班的实施细节，维护公司和员工的合法权益，特制订本规定。 第二条（原则） 公司提倡高效率工作，鼓励员工在日常工作时间内完成工作任务。 确因工作需要，公司依法要求员工加班的，员工无紧急事务应当尽量服从安排，公司依法给予补休或支付加班报酬。 第三条（标准工时制的加班） 执行标准工时制的员工在工作日得到主管领导同意，延长工作时间的（下称加点），可以在未来三个月内换休。单个工作日加点不得超过3小时。 执行标准工时制的员工在休息日得到主管领导同意加班的，可以在未来三个月内换休。单个休息日加班不得超过11小时。 执行标准工时制的员工在法定节假日得到主管领导同意加班的，公司依法支付加班费。 员工因工作原因无法在指定期间内换休的，公司依法支付加班费。 员工加班加点每月累计不得超过36小时。";
//
//            String url = "http://aiproxy.infosec.ctripcorp.com/openai/deployments/gpt-35-turbo/chat/completions?api-version=2023-03-15-preview";
//            String content = "{\"messages\":[{\"role\":\"user\",\"content\":\"现在你是携程HR,请记住:" + overTimeRule + "; 请问:" + demoApiRequest.getReqMsg() + "\"}]}";
//
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/json; charset=utf-8");
//            headers.put("api-type", "azure_ad");
//            headers.put("Authorization", "Bearer g68K7cDT5YxN0X5Y");
//
//            Map<String, String> config = null;
//            String apiRes = HttpUtils.doPost(url, content, headers, config);
//
//            GPTRootDto gptRes = new GPTRootDto();
//            if (StringUtils.isNotBlank(apiRes)) {
//                gptRes = JsonUtils.parseObject(apiRes, GPTRootDto.class);
//            }
//            if (gptRes != null && CollectionUtils.isNotEmpty(gptRes.getChoices())) {
//                response.setReturnMsg(gptRes.getChoices().get(0).getMessage().getContent());
//            } else {
//                response.setReturnMsg("你的问题有点复杂,我暂时回答不上来");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
