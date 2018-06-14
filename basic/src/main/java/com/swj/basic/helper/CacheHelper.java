package com.swj.basic.helper;

import com.swj.basic.SwjConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 缓存帮助类
 *
 * @author Chenjw
 * @since 2018/3/15
 **/

public class CacheHelper {

    private static Logger logger = LoggerFactory.getLogger(CacheHelper.class);

    private static final String SINGLE = "single";

    private static final String REDIS_TYPE_KEY = "redis-type";

    static {
        if (SINGLE.equals(SwjConfig.get(REDIS_TYPE_KEY))) {
            initRedis();
        } else {
            initJedisCluster();
        }
    }

    private static JedisPool jedisPool;//单机连接池
    private static JedisCluster jedisCluster; // 集群

    private static void initRedis() {
        if (jedisPool == null) {
            if (SwjConfig.get("redis") != null) {
                Map<String, String> parm = redisConfigSubString(SwjConfig.get("redis"));

                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(Integer.valueOf(parm.get("maxtotal")));
                config.setMaxIdle(Integer.valueOf(parm.get("maxidle")));
                config.setMaxWaitMillis(Integer.valueOf(parm.get("maxwait")));
                config.setTestOnBorrow(Boolean.valueOf(parm.get("testonborrow")));
                jedisPool = new JedisPool(config, parm.get("host"), Integer.valueOf(parm.get("port")));
            } else {
                logger.error("init redis pool fail : no redis configuration in zookeeper");
            }
        }
    }

    /**
     * 集群redies 服务器注册
     * @author Chenjw
     * @since 2018/4/10
     **/
    public static void initJedisCluster() {
        if (SwjConfig.get("redis-cluster") != null) {
            Set<HostAndPort> nodes = new HashSet<HostAndPort>();
            Map<String, String> map = redisConfigSubString(SwjConfig.get("redis-cluster"));
            String[] rediss = map.get("host").toString().split("\\;");
            String[] re = new String[2];
            for (String str : rediss) {
                re = str.split("\\:");
                nodes.add(new HostAndPort(re[0], Integer.valueOf(re[1])));
            }
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(Integer.valueOf(map.get("maxtotal")));
            poolConfig.setMaxIdle(Integer.valueOf(map.get("maxidle")));
            poolConfig.setMinIdle(Integer.valueOf(map.get("minidle")));
            poolConfig.setMaxWaitMillis(Integer.valueOf(map.get("maxwait")));
            jedisCluster = new JedisCluster(nodes, poolConfig);
        } else {
            logger.error("init redis cluster fail : no redis configuration in zookeeper");
        }
    }


    public static Map<String, String> redisConfigSubString(String redisconfig) {
        Map<String, String> map = new HashMap<String, String>();
        String[] str = redisconfig.split("\\|");
        for (String s : str) {
            map.put(s.split("=")[0], s.split("=")[1]);
        }
        return map;
    }

    public static void setValue(String key, String value) {
        setValue(key, value, SwjConfig.getRedisExpireTime());
    }

    /**
     * 保存String
     *
     * @param key
     * @param value
     */
    public static void setValue(String key, String value, Integer second) {
        if (SwjConfig.enableRedisCache()) {
            if (StringHelper.isNullOrEmpty(key)) {
                return;
            }
            if (StringHelper.isNullOrEmpty(value)) {
                return;
            }
            Jedis jedis = null;
            try {
                if (jedisPool != null) {
                    jedis = jedisPool.getResource();
                    jedis.setex(key, second , value);
                } else {
                    jedisCluster.setex(key, second , value);
                }
            } catch (Exception e) {
                logger.error("redis set value fail,key:{},value:{}", key, value, e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static void setJsonValue(String key, Object obj, Integer second) {
        if (!SwjConfig.enableRedisCache() || StringHelper.isNullOrEmpty(key)) {
            return;
        }
        if (obj != null) {
            setValue(key, JsonHelper.toJsonString(obj), second);
        }
    }

    public static void setJsonValue(String key, Object obj) {
        setJsonValue(key, obj, SwjConfig.getRedisExpireTime());
    }

    /**
     * @param key 获取String 数据
     * @return
     */
    public static String getValue(String key) {
        if (!SwjConfig.enableRedisCache() || StringHelper.isNullOrEmpty(key)) {
            return null;
        }
        String value = null;
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null) {
                    value = jedis.get(key);
                }
            } else {
                value = jedisCluster.get(key);
            }
        } catch (Exception e) {
            logger.error("redis get key fail key:{}", key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public static <T> T getJsonObject(String key, Class<T> clz) {
        return JsonHelper.parseBean(getValue(key), clz);
    }

    public static void deleteCache(String... keys){
        Jedis jedis=null ;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
                if (jedis != null) {
                    jedis.del(keys);
                }
            } else {
                jedisCluster.del(keys);
            }
        }catch (Exception e){
            logger.error("redis delete key faile :key{}",keys,e);
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
