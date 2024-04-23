package com.ctrip.car.osd.notificationcenter.basic;

import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import credis.java.client.CacheProvider;
import credis.java.client.pipeline.CachePipeline;
import credis.java.client.setting.RAppSetting;
import credis.java.client.util.CacheFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RedisUtil {
    public static Map<String, RedisUtil> clusterMap = new ConcurrentHashMap<>();

    public static RedisUtil getInstance(String clusterName) {
        if (!clusterMap.containsKey(clusterName)) {
            synchronized (clusterMap) {
                if (!clusterMap.containsKey(clusterName)) {
                    clusterMap.put(clusterName, new RedisUtil(clusterName));
                }
            }
        }
        return clusterMap.get(clusterName);
    }

    private CacheProvider provider;

    private RedisUtil(String clusterName) {
        try {
            RAppSetting.setClientPoolMaxSize(10);
            this.provider = CacheFactory.getProvider(clusterName);
        }catch (Exception ex){
            LogUtils.warn("RedisUtil_getProvider", ex);
        }
    }

    public Long sadd(String key, String... value) {
        return this.provider.sadd(key, value);
    }

    public Set<String> smembers(String key) {
        return this.provider.smembers(key);
    }

    public String hget(String key, String filed) {
        return this.provider.hget(key, filed);
    }

    public Boolean hset(String key, String filed, String value) {
        return this.provider.hset(key, filed, value);
    }

    public String get(String key) {
        return this.provider.get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        String str = this.provider.get(key);
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return JsonUtils.parseObject(str, clazz);
    }


    public <T> boolean set(String key, T val, int time) {
        if (time <= 0) {
            return false;
        }
        String jsonVal = JsonUtils.parseJsonClean(val);
        boolean flag = provider.set(key, jsonVal);
        provider.expire(key, time);

        return flag;
    }

    public Long incr(String key) {
        return provider.incr(key);
    }

    public <T> boolean setnofresh(String key, T val, int time) {
        if (time <= 0) {
            return false;
        }

        if (provider.exists(key)) {
            time = Long.valueOf(provider.ttl(key)).intValue();
        }
        String jsonVal = JsonUtils.parseJsonClean(val);
        boolean flag = provider.set(key, jsonVal);
        provider.expire(key, time);

        return flag;
    }

    public boolean setnx(String key, String uid, int time) {
        if (time <= 0) {
            return false;
        }
        boolean flag = provider.setnx(key, uid);
        if (flag) {
            provider.expire(key, time);
        }
        return flag;
    }

    public <T> boolean setObj(String key, T val, int time) {
        if (time <= 0) {
            return false;
        }
        String jsonVal = JsonUtils.parseJsonClean(val);
        boolean flag = provider.set(key, jsonVal);
        provider.expire(key, time);
        return flag;
    }

    public CachePipeline getPipeline() {
        return this.provider.getPipeline();
    }


    public <T> List<T> mget(List<String> keys, Class<T> clazz) {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> strs = this.provider.mget(keys.toArray(new String[keys.size()]));
        return strs.stream().map(g -> JsonUtils.parseObject(g, clazz)).collect(Collectors.toList());
    }

    public List<String> mget(List<String> keys) {
        return this.provider.mget(keys.toArray(new String[keys.size()]));
    }

    public String mset(int time, String... keysvalues) {

        return this.provider.mset(time, keysvalues);
    }

    public long ttl(String key) {
        return provider.ttl(key);
    }

    public boolean exist(String key) {
        return provider.exists(key);
    }

    public boolean expire(String key, Long seconds) {
        return provider.expire(key, seconds);
    }

    public boolean del(String key) {
        return provider.del(key);
    }

    public <T> List<T> getOrSetData(String key, Supplier<List<T>> supplier, Class classz, Integer expireSecond) {
        List<T> list;
        try {
            if (exist(key)) {
                list = JsonUtils.parseObjectList(get(key), classz);
            } else {
                list = supplier.get();
                if (CollectionUtils.isNotEmpty(list)) {
                    set(key, JsonUtils.parseJson(list), expireSecond * 1000);
                }
            }
        } catch (Exception ex) {
            list = new ArrayList<>();
        }
        return list;
    }

    public <T> List<T> getArray(String key, Class<T> clazz) {
        String str = this.provider.get(key);
        if (StringUtils.isBlank(str)) {
            return new ArrayList<>();
        }
        return JsonUtils.parseObjectList(str, clazz);
    }
}
