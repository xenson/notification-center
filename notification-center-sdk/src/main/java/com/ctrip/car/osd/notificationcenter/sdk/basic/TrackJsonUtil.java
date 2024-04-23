package com.ctrip.car.osd.notificationcenter.sdk.basic;

import com.dianping.cat.Cat;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrackJsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    static {
        //在反序列化时忽略在 json 中存在但 Java 对象不存在的属性 - 或标注@JsonAnySetter Map接收不可反序字段
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        //时区设置
        MAPPER.setTimeZone(TimeZone.getDefault());
        //日期格式化
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        MAPPER.setFilterProvider(new SimpleFilterProvider().addFilter("Schema", SimpleBeanPropertyFilter.serializeAllExcept("Schema")));

        // 单引号
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 特殊字符
        MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        // 在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略大小写
        MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        // 防止jackson使用缓存
        MAPPER.getFactory().configure(JsonFactory.Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING, false);
        // 在序列化时忽略值为 null 的属性
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 日期格式化
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 时区设置
        MAPPER.setTimeZone(TimeZone.getDefault());
        // 过滤Schema
        MAPPER.setFilterProvider(new SimpleFilterProvider().addFilter("Schema", SimpleBeanPropertyFilter.serializeAllExcept("Schema")));

//        //在序列化时日期格式默认为 yyyy-MM-dd'T'HH:mm:ss.SSSZ
//        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
//        //忽略值为默认值的属性 - false的bool类型字段会被过滤
//        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);

//        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
//        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    /**
     * object to xml
     *
     * @param obj
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> String parseXml(T obj) throws JsonProcessingException {
        return XML_MAPPER.writeValueAsString(obj);
    }

    /**
     * object to json
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String parseJson(T obj) {
        if (obj != null) {
            try {
                return MAPPER.writeValueAsString(obj);
            } catch (Exception ex) {
                //log
                Cat.logError("parseJson", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * object to json - ignore "schema"
     *
     * @param obj
     * @return
     */
    public static String parseJsonClean(Object obj) {
        return toJsonIgnore(obj, "schema");
    }

    /**
     * object to json - pretty format
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String parseJsonPretty(T obj) {
        if (obj != null) {
            try {
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } catch (Exception ex) {
                //log
                Cat.logError("parseJsonPretty", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to object
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, Class<T> tClass) {
        if (StringUtils.isNotBlank(json)) {
            try {
                return MAPPER.readValue(json, tClass);
            } catch (Exception ex) {
                //log
                Cat.logError("parseObject", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to object - from file stream
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T parseObject(InputStream json, Class<T> tClass) {
        if (json != null) {
            try {
                return MAPPER.readValue(json, tClass);
            } catch (Exception ex) {
                //log
                Cat.logError("parseObject", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to object - with complex type
     * ex type: List<Map<String, List<String>>>
     * ex json: [{"appId":["100009572"],"eventType":["99999",""]},{"appId":["100025206"],"eventType":["52501",""]}]
     *
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        if (json != null) {
            try {
                return MAPPER.readValue(json, typeReference);
            } catch (Exception ex) {
                //log
                Cat.logError("parseObject", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to map - default Map<String, String>
     *
     * @param json
     * @return
     */
    public static Map<String, String> parseMap(String json) {
        if (json != null) {
            try {
                return MAPPER.readValue(json, Map.class);
            } catch (Exception ex) {
                //log
                Cat.logError("parseMap", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to map - default Map<String, String>
     *
     * @param json
     * @return
     */
    public static Map<String, String> parseObjMap(Object json) {
        if (json != null) {
            try {
                Class objClass = json.getClass();
                if (objClass.getTypeName().equals("java.util.LinkedHashMap")) {
                    return (Map) json;
                } else {
                    return parseMap(json.toString());
                }
            } catch (Exception ex) {
                //log
                Cat.logError("parseObjMap", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to map - default Map<String, String>
     *
     * @param json
     * @param defKey
     * @return
     */
    public static Map<String, String> parseObjMap(Object json, String defKey) {
        if (json != null) {
            Map<String, String> resMap = new HashMap<>();
            try {
                Class objClass = json.getClass();
                if (objClass.getTypeName().equals(LinkedHashMap.class.getTypeName())) {
                    return (Map) json;
                } else {
                    resMap = MAPPER.readValue(json.toString(), Map.class);
                    if (resMap == null) {
                        resMap = new HashMap<>();
                        resMap.put(defKey, json.toString());
                    }
                    return resMap;
                }
            } catch (Exception ex) {
                //log
                Cat.logError("parseObjMap", ex.toString());
                resMap.put(defKey, json.toString());
                return resMap;
            }
        } else {
            return null;
        }
    }


    /**
     * json to object list
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> parseObjectList(String json, Class<T> tClass) {
        if (StringUtils.isNotBlank(json)) {
            try {
                JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, tClass);
                return MAPPER.readValue(json, javaType);
            } catch (Exception ex) {
                //log
                Cat.logError("parseObjectList", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to object set
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Set<T> parseObjectSet(String json, Class<T> tClass) {
        if (StringUtils.isNotBlank(json)) {
            try {
                JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(Set.class, tClass);
                return MAPPER.readValue(json, javaType);
            } catch (Exception ex) {
                //log
                Cat.logError("parseObjectList", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * json to object list
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> parseObjectList(InputStream json, Class<T> tClass) {
        if (json != null) {
            try {
                JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, tClass);
                return MAPPER.readValue(json, javaType);
            } catch (Exception ex) {
                //log
                Cat.logError("parseObjectList", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * json to map list - default Map<String, String>
     *
     * @param json
     * @return
     */
    public static List<Map<String, String>> parseMapList(String json) {
        if (StringUtils.isNotBlank(json)) {
            try {
                JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, Map.class);
                return MAPPER.readValue(json, javaType);
            } catch (Exception ex) {
                //log
                Cat.logError("parseObjectList", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }


    private static final String DYNC_FILTER = "DYNC_FILTER";
    private static final String DYNC_INCLUDE = "DYNC_INCLUDE";

    @JsonFilter(DYNC_FILTER)
    private interface DynamicFilter {
    }

    @JsonFilter(DYNC_INCLUDE)
    private interface DynamicInclude {
    }

    /**
     * ignore fields to json
     *
     * @param obj
     * @param properties
     * @return
     */
    private static String toJsonIgnore(Object obj, String properties) {
        if (obj != null) {
            try {
                MAPPER.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_FILTER,
                        SimpleBeanPropertyFilter.serializeAllExcept(properties.split(","))));
                MAPPER.addMixIn(obj.getClass(), DynamicFilter.class);

//                MAPPER.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_INCLUDE,
//                        SimpleBeanPropertyFilter.filterOutAllExcept(property.split(","))));
//                MAPPER.addMixIn(tClass, DynamicInclude.class);

                return MAPPER.writeValueAsString(obj);
            } catch (Exception ex) {
                //log
                Cat.logError("toJsonIgnore", ex.toString());
                return null;
            }
        } else {
            return null;
        }
    }
}
