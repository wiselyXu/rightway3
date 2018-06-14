package com.swj.basic;


import com.swj.basic.helper.ListHelper;
import com.swj.basic.helper.StringHelper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 全局配置信息
 *
 * @author Chenjw
 * @since 2018/3/16
 **/
public class SwjConfig {
    public static final String SOA_SERVICE_NODE = "/soaservices";
    private final static Logger logger = LoggerFactory.getLogger(SwjConfig.class);
    private static final int DEFAULT_SESSION_TIMEOUT = 60 * 1000;
    private static final String REDIS_SWITCH = "redis-switch";
    private static final String REDIS_EXPIRE = "redis-expire";
    private static final String SVJIA_ZK_SERVER = "SVJIA_ZK_SERVER";
    private static final String K8S_SERVICE_IP = "K8S_SERVICE_IP";
    private static final String K8S = "k8s";
    private static String appName;
    private static Map<String, String> settings;
    private static ZkClient zkClient;
    private static SwjConfigListener swjConfigListener;
    private static boolean enableRedisCache;
    private static int redisExpireTime;
    private static Map<String, String> serviceAddress = new HashMap<>();
    private static String localK8SServiceIp;


    /**
     * 传入zk的app 节点名，初始化时根据这个获取该节点的下的所有节点信息，同时也作为soaservice的子节点名字
     *
     * @author Chenjw
     * @since 2018/6/12
     **/
    public static void setAppName(String name) throws Exception {
        if (StringHelper.isNullOrEmpty(name)) throw new Exception("AppName must be not null !");
        appName = name;
        if (!appName.startsWith("/")) {
            appName = "/" + appName;
        }
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initSoa();
    }

    /**
     * 获取所有SOA服务的所有信息节点
     *
     * @author Chenjw
     * @since 2018/6/12
     **/
    public static Map<String, String> getServiceAddress() {
        return serviceAddress;
    }

    public static String registerK8SServiceIp() {
        createPath();
        if (!zkClient.exists(SOA_SERVICE_NODE + appName + "/" + localK8SServiceIp))
            zkClient.create(SOA_SERVICE_NODE + appName + "/" + localK8SServiceIp, K8S, CreateMode.PERSISTENT);
        return localK8SServiceIp;
    }

    public static String registerK8SPodIp() {
        String ip = getLocalIPAddress();
        createPath();
        if (!zkClient.exists(SOA_SERVICE_NODE + appName + "/" + localK8SServiceIp + "/" + ip))
            zkClient.create(SOA_SERVICE_NODE + appName + "/" + localK8SServiceIp + "/" + ip, null, CreateMode.EPHEMERAL);
        return ip;
    }

    private static void createPath() {
        if (!zkClient.exists(SOA_SERVICE_NODE + appName))
            zkClient.createPersistent(SOA_SERVICE_NODE + appName);
    }

    public static void registerServiceIp() {
        if (!zkClient.exists(SOA_SERVICE_NODE + appName + "/" + getLocalIPAddress()))
            zkClient.create(SOA_SERVICE_NODE + appName + "/" + getLocalIPAddress(), null, CreateMode.EPHEMERAL);
    }

    public static String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void init() throws Exception {
        zkClient = new ZkClient(getZookeeperAddress(), Integer.MAX_VALUE, DEFAULT_SESSION_TIMEOUT, new SwjZkSerializer());
        if (!zkClient.exists(appName)) {
            throw new Exception("missing " + appName + " config,init fail!");
        }
        settings = new HashMap<String, String>();
        swjConfigListener = new SwjConfigListener();
        List<String> yardList = zkClient.getChildren(appName);
        //        for (String yard :yardList){
        //            getYardChildValue(contactKey(yard));
        //            settings.put(yard,zkClient.readData(contactKey(yard)));
        //        }
        for (String yard : yardList) {
            String value = zkClient.readData(contactKey(yard));
            String key = getKeyFromYard(yard);
            logger.info("read setting from zk,yard:{},key:{},value:{}", yard, key, value);
            settings.put(key, value);
            zkClient.subscribeDataChanges(contactKey(key), swjConfigListener);
        }
        if (settings.containsKey(REDIS_SWITCH)) {
            enableRedisCache = Boolean.valueOf(settings.get(REDIS_SWITCH));
        } else {
            enableRedisCache = true;
        }
        if (settings.containsKey(REDIS_EXPIRE)) {
            redisExpireTime = Integer.parseInt(settings.get(REDIS_EXPIRE));
        } else {
            redisExpireTime = 600;
        }
        logger.info("init redis cache status with:{}", enableRedisCache);
        zkClient.subscribeChildChanges(appName, swjConfigListener);
    }

    private static void getYardChildValue(String parentNode) {
        List<String> children = zkClient.getChildren(parentNode);
        for (String str : children) {
            getYardChildValue(parentNode + "/" + str);
            settings.put(str, zkClient.readData(parentNode + "/" + str));
        }
    }

    /**
     * 初始化 SOAService
     *
     * @author Chenjw
     * @since 2018/6/13
     **/
    public static void initSoa() {
        if (!zkClient.exists(SOA_SERVICE_NODE)) zkClient.createPersistent(SOA_SERVICE_NODE);
        List<String> yardList = zkClient.getChildren(SOA_SERVICE_NODE);
        serviceAddress.clear();
        for (String yard : yardList) {
            List<String> childYard = zkClient.getChildren(SOA_SERVICE_NODE + "/" + yard);
            childYard.forEach(s -> {
                String value = zkClient.readData(SOA_SERVICE_NODE + "/" + yard + "/" + s);
                if (!StringHelper.isNullOrEmpty(value) && K8S.equals(value)) {//k8s节点 检查是否含有pod 有则添加
                    List list = zkClient.getChildren(SOA_SERVICE_NODE + "/" + yard + "/" + s);
                    if (!ListHelper.isNullOrEmpty(list)) {
                        setServiceAddress(yard, s);
                    }
                } else {
                    setServiceAddress(yard, s);
                }
            });
        }
        zkClient.subscribeChildChanges(SOA_SERVICE_NODE, swjConfigListener);
    }

    private static void setServiceAddress(String yard, String s) {
        logger.info("read setting from zk,serivceName:{},serviceIp:{}", yard, s);
        if (serviceAddress.containsKey(yard)) {
            serviceAddress.put(yard, String.format("%s,%s", serviceAddress.get(yard), s));
        } else {
            serviceAddress.put(yard, s);
        }
        zkClient.subscribeChildChanges(SOA_SERVICE_NODE + "/" + yard + "/" + s, swjConfigListener);
    }

    private static String contactKey(String key) {
        return appName.concat("/").concat(key);
    }

    public static String getZookeeperAddress() {
        Map<String, String> env = System.getenv();
        String address = env.get(SVJIA_ZK_SERVER);
        localK8SServiceIp = env.get(K8S_SERVICE_IP);
        if (StringHelper.isNullOrEmpty(address)) {
            address = "172.18.3.66:2182,172.18.3.66:2183,172.18.3.66:2184";
            logger.info("can not get zookeeper server in environment variable,return default setting: {}", address);
        } else {
            logger.info("get zookeeper server in environment variable :{}", address);
        }
        return address;
    }

    private static String getKeyFromYard(String yard) {
        String[] temp = yard.split("/");
        return temp[temp.length - 1];
    }

    public static void put(String yard) {
        if (!zkClient.exists(yard)) {
            return;
        }
        String key = getKeyFromYard(yard);
        settings.put(key, (String) zkClient.readData(yard));
        if (key.equals(REDIS_SWITCH)) {
            enableRedisCache = Boolean.valueOf(settings.get(REDIS_SWITCH));
            logger.info("redis cache switch to:{}", enableRedisCache);
        } else if (key.equals(REDIS_EXPIRE)) {
            redisExpireTime = Integer.valueOf(settings.get(REDIS_EXPIRE));
            logger.info("redis expired time change to:{}", redisExpireTime);
        } else {
            logger.info("key : {} ,new value : {}", key, settings.get(key));
        }
        zkClient.subscribeDataChanges(yard, swjConfigListener);
    }

    public static String get(String key) {
        if (!settings.containsKey(key)) {
            return null;
        }
        return settings.get(key);
    }

    public static void add(List<String> keys) {
        for (String key : keys) {
            if (settings.containsKey(key)) {
                continue;
            }
            settings.put(key, (String) zkClient.readData(contactKey(key)));
            zkClient.subscribeDataChanges(contactKey(key), swjConfigListener);
        }
    }

    public static boolean enableRedisCache() {
        return enableRedisCache;
    }

    public static int getRedisExpireTime() {
        return redisExpireTime;
    }

    public static String getAppName() {
        return appName;
    }

    public static void removeSoaService(String parentPath) {
        String value = zkClient.readData(parentPath);
        if (!StringHelper.isNullOrEmpty(value) && K8S.equals(value)) {
            logger.info("remove soaSerive form ServiceAddress {}", parentPath);
            serviceAddress.remove(parentPath.split("/")[2]);
        }
        zkClient.subscribeChildChanges(parentPath, swjConfigListener);
    }

}
