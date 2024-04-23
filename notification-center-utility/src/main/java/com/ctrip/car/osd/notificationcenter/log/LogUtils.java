package com.ctrip.car.osd.notificationcenter.log;

import com.ctrip.car.osd.framework.common.clogging.CatFactory;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.context.RequestContext;
import com.ctrip.car.osd.framework.common.context.RequestContextFactory;
import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import com.ctrip.car.osd.notificationcenter.basic.JsonUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Created by xiayx on 2019/7/31.
 */
public class LogUtils {
    private static final ExecutorService EXECUTOR_SERVICE = ThreadPoolUtil.callerRunsPool("LOG_%d", 3);
    private static final String LOG_TYPE = "logType";
    private static final String LOG_TITLE = "title";
    private static final String LOG_MSG = "msg";
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    /**
     * [info]: title + message
     *
     * @param title
     * @param msg
     */
    public static void info(String title, String msg) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            //tags.put("test", "tag");
            //tags.put("requestId", "LogUtil");
            LOGGER.info(title, msg, tags);
            tags.put(LOG_TYPE, "info");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, msg);
        }, EXECUTOR_SERVICE);
    }

    /**
     * [info]: title + message + tags
     *
     * @param title
     * @param msg
     */
    public static void info(String title, String msg, Map<String, String> clogTags) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            tags.put(LOG_TYPE, "info");
            tags.put(LOG_TITLE, title);
            tags.putAll(clogTags);
            LOGGER.info(title, msg, tags);
            CatFactory.logTags(tags, LOG_MSG, msg);
        }, EXECUTOR_SERVICE);
    }

    /**
     * [info]: title + tags
     *
     * @param title
     * @param objects
     */
    public static void info(String title, Object... objects) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            LOGGER.info(title, getObjsStr(objects), tags);
            tags.put(LOG_TYPE, "info");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getObjsStr(objects));
        }, EXECUTOR_SERVICE);
    }

    public static void info(Logger logger, String title, String msg) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            logger.info(title, msg, tags);
            tags.put(LOG_TYPE, "info");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, msg);
        }, EXECUTOR_SERVICE);
    }

    public static void info(Logger logger, String title, Objects... objects) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            logger.info(title, getObjsStr(objects), tags);
            tags.put(LOG_TYPE, "info");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getObjsStr(objects));
        }, EXECUTOR_SERVICE);
    }


    /**
     * [warn]: title + message
     *
     * @param title
     * @param msg
     */
    public static void warn(String title, String msg) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            LOGGER.warn(title, msg, tags);
            tags.put(LOG_TYPE, "warn");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, msg);
        }, EXECUTOR_SERVICE);
    }

    /**
     * [warn]: title + tags
     *
     * @param title
     * @param objects
     */
    public static void warn(String title, Object... objects) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            LOGGER.warn(title, getObjsStr(objects), tags);
            tags.put(LOG_TYPE, "warn");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getObjsStr(objects));
        }, EXECUTOR_SERVICE);
    }

    /**
     * [warn]: title + Exception
     *
     * @param title
     * @param e
     */
    public static void warn(String title, Exception e) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            LOGGER.warn(title, e, tags);
            tags.put(LOG_TYPE, "warn");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getStackTrace(e));
        }, EXECUTOR_SERVICE);
    }

    public static void warn(Logger logger, String title, String msg) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            logger.warn(title, msg, tags);
            tags.put(LOG_TYPE, "warn");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, msg);
        }, EXECUTOR_SERVICE);
    }

    public static void warn(Logger logger, String title, Object... objects) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            logger.warn(title, getObjsStr(objects), tags);
            tags.put(LOG_TYPE, "warn");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getObjsStr(objects));
        }, EXECUTOR_SERVICE);
    }

    public static void warn(Logger logger, String title, Exception e) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            logger.warn(title, e, tags);
            tags.put(LOG_TYPE, "warn");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getStackTrace(e));
        }, EXECUTOR_SERVICE);
    }


    /**
     * [error]: title + tags
     *
     * @param title
     * @param objects
     */
    public static void error(String title, Object... objects) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            LOGGER.warn(title, getObjsStr(objects), tags);
            tags.put(LOG_TYPE, "warn");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getObjsStr(objects));
        }, EXECUTOR_SERVICE);
    }

    /**
     * [error]: title + Exception
     *
     * @param title
     * @param e
     */
    public static void error(String title, Exception e) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            LOGGER.error(title, e, tags);
            tags.put(LOG_TYPE, "error");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getStackTrace(e));
        }, EXECUTOR_SERVICE);
    }

    public static void error(Logger logger, String title, Exception e) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            logger.error(title, e, tags);
            tags.put(LOG_TYPE, "error");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getStackTrace(e));
        }, EXECUTOR_SERVICE);
    }

    public static void error(Logger logger, String title, Object... objects) {
        CompletableFuture.runAsync(() -> {
            Map<String, String> tags = getTags();
            logger.error(title, getObjsStr(objects), tags);
            tags.put(LOG_TYPE, "error");
            tags.put(LOG_TITLE, title);
            CatFactory.logTags(tags, LOG_MSG, getObjsStr(objects));
        }, EXECUTOR_SERVICE);
    }


    private static Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        RequestContext currentRequestContext = RequestContextFactory.INSTANCE.getCurrent();
        if (currentRequestContext != null) {
            Map<String, String> requestItems = currentRequestContext.getRequestItems();
            tags.putAll(requestItems);
        }
        return tags;
    }

    private static Map<String, String> getTags(Map<String, String> tags) {
        RequestContext currentRequestContext = RequestContextFactory.INSTANCE.getCurrent();
        if (currentRequestContext != null) {
            Map<String, String> requestItems = currentRequestContext.getRequestItems();
            tags.putAll(requestItems);
        }
        return tags;
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter(1024);
        PrintWriter pw = new PrintWriter(sw);
        if (e != null) {
            e.printStackTrace(pw);
        }
        return sw.toString();
    }

    private static String getObjsStr(Object[] objects) {
        StringBuilder stringBuilder = new StringBuilder();
        if (objects.length % 2 != 0) {
            //throw new IllegalArgumentException("Number should be an even number of parameters");
            return stringBuilder.toString();
        } else {
            int obLen = objects.length / 2;

            for (int i = 0; i < obLen; ++i) {
                stringBuilder.append(objects[i * 2]);
                stringBuilder.append(":");
                Object value = objects[i * 2 + 1];
                if (value == null) {
                    stringBuilder.append("<null>");
                } else if (value instanceof String) {
                    stringBuilder.append(value);
                } else {
                    stringBuilder.append(JsonUtils.parseJsonClean(value));
                }

                stringBuilder.append("\r\n");
            }

            return stringBuilder.toString();
        }
    }
}
