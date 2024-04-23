package com.ctrip.car.osd.notificationcenter.basic;

import com.ctrip.car.osd.notificationcenter.config.QCHickwall;
import com.dianping.cat.Cat;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xiayx on 2020/9/27.
 */
public class ObjectUtils {
    private static Set<String> trueSet = new HashSet<>(QCHickwall.getList("Blueprint_HickwallTrue"));
    //private static Set<String> falseSet = new HashSet<String>(Arrays.asList("0", "false", "no"));
    private static Set<String> falseSet = new HashSet<>(QCHickwall.getList("Blueprint_HickwallFalse"));

    public static Long convertNumeric(String str) {
        Long num = 0L;
        try {
            if (str == null) {
                return num;
            }
            if (StringUtils.isNumeric(str)) {
                num = Long.valueOf(str);
            } else if (str.equalsIgnoreCase("true")) {
                num = 1L;
            }
        } catch (Exception ex) {
            Cat.logError("convertNumeric", ex.toString());
        }
        return num;
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    public static String boolToInt(String str) {
        try {
            if (str.equalsIgnoreCase("true")) {
                return "1";
            } else if (str.equalsIgnoreCase("false")) {
                return "0";
            } else {
                return str;
            }
        } catch (Exception ex) {
            Cat.logError("boolToInt", ex.toString());
        }
        return str;
    }

    /**
     * string convert to Long
     *
     * @param str
     * @return
     */
    public static Long convertToLong(String str) {
        Long num = 0L;
        // null or empty
        if (str == null || str.length() == 0) {
            return num;
        }

        try {
            num = new BigDecimal(str).longValue();
        } catch (Exception ex) {
            return num;
        }

        return num;
    }

    /**
     * boolean string convert to int
     *
     * @param str
     * @return
     */
    public static String convertToTag(String str) {
        try {
            if (trueSet.contains(str.toLowerCase())) {
                return "1";
            } else if (falseSet.contains(str.toLowerCase())) {
                return "0";
            } else {
                return str;
            }
        } catch (Exception ex) {
            Cat.logError("convertBoolToInt", ex.toString());
        }
        return str;
    }

    /**
     * boolean string convert to int with default value
     *
     * @param str
     * @return
     */
    public static String convertToTag(String str, String defaultStr) {
        try {
            if (trueSet.contains(str.toLowerCase())) {
                return "1";
            } else if (falseSet.contains(str.toLowerCase())) {
                return "0";
            } else {
                return defaultStr;
            }
        } catch (Exception ex) {
            Cat.logError("convertBoolToInt", ex.toString());
        }
        return str;
    }

    public static Map<String, String> objectToMap(Object object, List<String> toJsonList) {
        Map<String, String> map = new HashMap<>();
        try {
            Class<?> cls = object.getClass();
            for (Field field : cls.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (value != null && !Objects.equals(field.getName(), "SCHEMA") && !Objects.equals(field.getName(), "serialVersionUID")) {
                        if (toJsonList.contains(field.getName())) {
                            map.put(field.getName(), JsonUtils.parseJson(value));
                        }else {
                            map.put(field.getName(), String.valueOf(field.get(object)));
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            }
        } catch (Exception e) {
        }
        return map;
    }
}
