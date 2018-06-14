package com.swj.freeway.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * com.swj.freeway.zookeeper
 *
 * @Description 维护ZkClient连接
 * @Author 余焕【yuh@3vjia.com】
 * @Datetime 2018/5/11 11:07
 **/
public class ZkClients {

    // 会话超时时间
    public final static int sessionTimeout = 10000;

    // 连接的超时时间
    public final static int connectionTimeout = 10000;

    public final static Map<String, ZkClient> zkClients = new HashMap<>();

    public static ZkClient getZkClient(String zkUrl) {
        if (!zkClients.containsKey(zkUrl)) {
            synchronized (zkClients) {
                if (!zkClients.containsKey(zkUrl)) {
                    ZkClient zk = new ZkClient(zkUrl, sessionTimeout, connectionTimeout, new SerializableSerializer());
                    zkClients.put(zkUrl, zk);
                }
            }
        }
        return zkClients.get(zkUrl);
    }

}
