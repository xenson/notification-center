package com.ctrip.car.osd.notificationcenter.tracker.components;

import com.ctrip.car.osd.notificationcenter.basic.ObjectUtils;
import com.ctrip.car.osd.notificationcenter.config.QCHickwall;
import com.ctrip.car.osd.notificationcenter.tracker.common.GaugeSupplier;
import com.ctrip.ops.hickwall.HickwallUDPReporter;
import io.dropwizard.metrics5.Gauge;
import io.dropwizard.metrics5.MetricName;
import io.dropwizard.metrics5.MetricRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xiayx on 2022/1/17.
 */
public class DemoTracker extends TrackerAct {
    private static DemoTracker instance;

    /**
     * Singleton
     *
     * @return
     */
    public static DemoTracker getInstance() {
        if (instance == null) {
            instance = new DemoTracker();
        }
        return instance;
    }

    /**
     * hickwall sdk config
     */
    private final static MetricRegistry metrics = new MetricRegistry();
    private final static long hickwallPeriod = 10;
    private final static String hickwallHost = "sink.hickwall.qa.nt.ctripcorp.com";
    private final static Integer hickwallPort = 8090;
    private final static String hickwallDBName = QCHickwall.get("Blueprint_HickwallDBName");

    static {
        //metrics initial
        HickwallUDPReporter.enable(
                metrics,            // singleton metrics registry
                hickwallPeriod,                 // interval, minimum 10s
                TimeUnit.SECONDS,   // interval time unit
                hickwallDBName              // BU CODE，例如FLT HTL
        );
    }


    @Override
    public boolean open(Map<String, String> trackInfo) {
        return true;
    }

    @Override
    public void track(Map<String, String> trackInfo) {
        //HickwallUDPReporter.enable(metrics,60,TimeUnit.SECONDS,"test");
        //MetricName metricName = MetricName.build("CAR.notificationcenter.888888_apiCostt");
        int random = new Random().nextInt(30);

//        metrics.gauge(metricName, () -> () ->  random);


//        //method 1------------------------------
//        Long metricValue = ObjectUtils.convertToLong(trackInfo.getOrDefault("apiCost", "0"));//
//        AtomicInteger atom = new AtomicInteger(metricValue.intValue());
//        System.out.println(atom.get() + "__" + atom.hashCode() + "++");
//
//        Gauge<Integer> gauge = new Gauge<Integer>() {
//            @Override
//            public Integer getValue() {
//                return atom.getAndSet(0);
//            }
//        };
//        MetricRegistry.MetricSupplier<Gauge> supplier = new MetricRegistry.MetricSupplier<Gauge>() {
//            @Override
//            public Gauge newMetric() {
//                return gauge;
//            }
//        };
//        metrics.gauge(metricName, supplier);


//        //method 2------------------------------
//        metrics.gauge(metricName, new MetricRegistry.MetricSupplier<Gauge>() {
//            @Override
//            public Gauge newMetric() {
//                return new Gauge<Integer>() {
//                    @Override
//                    public Integer getValue() {
//                        int aaa = atom.getAndSet(metricValue.intValue());
//                        System.out.println(aaa + "__" + atom.hashCode() + "--");
//
//                        return aaa;
//                    }
//                };
//            }
//        });
//
//        atom.getAndSet(metricValue.intValue());
//        System.out.println(atom.get() + "__" + atom.hashCode() + "++");


//        //method 3------------------------------
//        List list = new ArrayList();
//        for (int i = 0; i < random; i++) {
//            list.add(i);
//        }
//        System.out.println(list.size() + "__" + list.hashCode() + "--");
//
//        Gauge<Integer> gauge = new Gauge<Integer>() {
//            @Override
//            public Integer getValue() {
//                System.out.println(list.size() + "__" + list.hashCode() + "--");
//                return list.size();
//            }
//        };
//        MetricRegistry.MetricSupplier<Gauge> supplier = new MetricRegistry.MetricSupplier<Gauge>() {
//            @Override
//            public Gauge newMetric() {
//                return gauge;
//            }
//        };
//        metrics.gauge(metricName, supplier);


//        //method 4-单指标-----------------------------
//        GaugeSupplier.setTrackInfo(trackInfo,"apiCost");
//
//        Gauge<Integer> gauge = new Gauge<Integer>() {
//            @Override
//            public Integer getValue() {
//                Integer aaa = GaugeSupplier.getTrackValue();
//                System.out.println(aaa + "__" + aaa.hashCode() + "--");
//                return aaa;
//            }
//        };
//        MetricRegistry.MetricSupplier<Gauge> supplier = new MetricRegistry.MetricSupplier<Gauge>() {
//            @Override
//            public Gauge newMetric() {
//                return gauge;
//            }
//        };
//        metrics.gauge(metricName, supplier);


        //method 4-多指标-----------------------------
        //String[] metricNames = {"888888_apiCostt", "888888_getCountt"};
        String[] metricFields = {"apiCost", "getCount"};

        for (String metricField : metricFields) {
            String metricName = "888888_" + metricField;
            MetricName metricBuild = MetricName.build("CAR.notificationcenter." + metricName);
            Long metricValue = ObjectUtils.convertToLong(trackInfo.getOrDefault(metricField, "0"));

            GaugeSupplier.setTrackInfo(metricValue, metricName);
            Gauge<Long> gauge = new Gauge<Long>() {
                @Override
                public Long getValue() {
                    System.out.println(metricName + "__" + metricName.hashCode() + "++");
                    Long test = GaugeSupplier.getTrackValue(metricName);
                    System.out.println(test + "__" + test.hashCode() + "--");
                    System.out.println();
                    return test;
                }
            };

//            List<String> demonList = new ArrayList<>();
//            for (Integer i = 0; i <= random; i++) {
//                demonList.add(i.toString());
//            }
//            System.out.println(demonList.size() + "__" + demonList.hashCode() + "++");
//
//            Gauge<Long> gauge = new Gauge<Long>() {
//                @Override
//                public Long getValue() {
////                    System.out.println(demoEntity.getMetricValStr() + "__" + demoEntity.hashCode() + "++");
////                    Long gaugeVal = GaugeSupplier.getTrackValueTest(demoEntity);
//
//                    Long gaugeVal = Long.valueOf(demonList.size());
//                    System.out.println(gaugeVal + "__" + gaugeVal.hashCode() + "++");
//                    return gaugeVal;
//                }
//            };

            MetricRegistry.MetricSupplier<Gauge> supplier = new MetricRegistry.MetricSupplier<Gauge>() {
                @Override
                public Gauge newMetric() {
                    return gauge;
                }
            };
            metrics.gauge(metricBuild, supplier);
        }
//        String metricField1 = "apiCost";
//        MetricName metricName1 = MetricName.build("CAR.notificationcenter.888888_apiCostt");
//        GaugeSupplier.setTrackInfo(trackInfo, metricField1, "888888_apiCostt");
//
//        Gauge<Integer> gauge1 = new Gauge<Integer>() {
//            @Override
//            public Integer getValue() {
//                Integer aaa1 = GaugeSupplier.getTrackValue();
//                System.out.println(aaa1 + "__" + aaa1.hashCode() + "--");
//                return aaa1;
//            }
//        };
//        MetricRegistry.MetricSupplier<Gauge> supplier1 = new MetricRegistry.MetricSupplier<Gauge>() {
//            @Override
//            public Gauge newMetric() {
//                return gauge1;
//            }
//        };
//        metrics.gauge(metricName1, supplier1);
//
//
//        String metricField2 = "getCount";
//        MetricName metricName2 = MetricName.build("CAR.notificationcenter.888888_getCountt");
//        GaugeSupplier.setTrackInfo(trackInfo, metricField2, "888888_getCountt");
//
//        Gauge<Integer> gauge2 = new Gauge<Integer>() {
//            @Override
//            public Integer getValue() {
//                Integer aaa2 = GaugeSupplier.getTrackValue();
//                System.out.println(aaa2 + "__" + aaa2.hashCode() + "--");
//                return aaa2;
//            }
//        };
//        MetricRegistry.MetricSupplier<Gauge> supplier2 = new MetricRegistry.MetricSupplier<Gauge>() {
//            @Override
//            public Gauge newMetric() {
//                return gauge2;
//            }
//        };
//        metrics.gauge(metricName2, supplier2);

    }
}
