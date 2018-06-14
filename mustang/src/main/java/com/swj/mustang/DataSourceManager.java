package com.swj.mustang;

import com.alibaba.druid.pool.DruidDataSource;
import com.swj.basic.SwjConfig;
import com.swj.basic.helper.StringHelper;

import java.util.HashMap;
import java.util.Map;

class DataSourceManager {

    private static final Map<String,DruidDataSource> dataSourceMap=new HashMap<>();

    static DruidDataSource createDataSource(String defaultKey,boolean isQuery) {
        String key = defaultKey;
        if (isQuery) {
            key = String.format("%s-read", key);
            if (!dataSourceMap.containsKey(key) && SwjConfig.get(key) == null) {
                key = defaultKey;
            }
        }
        if (!dataSourceMap.containsKey(key)) {
            synchronized (dataSourceMap) {
                if (!dataSourceMap.containsKey(key)) {
                    String conn = SwjConfig.get(key);
                    if (StringHelper.isNullOrEmpty(conn)) {
                        throw new IllegalArgumentException(String.format("connectionString with key:'%s' was not found", key));
                    }
                    String[] tempArr = conn.split(";");
                    String[] tempArr1;
                    Map<String, Object> connKV = new HashMap<>();
                    for (int i = 0; i < tempArr.length; i++) {
                        tempArr1 = tempArr[i].split("=", 2);
                        connKV.put(tempArr1[0], tempArr1[1]);
                    }
                    String user = connKV.get("user").toString();
                    String password = connKV.get("password").toString();
                    String url = connKV.get("url").toString();
                    String driver = connKV.get("driver").toString();
                    int initSize = Integer.valueOf(connKV.get("initSize").toString().trim());
                    int maxSize = Integer.valueOf(connKV.get("maxSize").toString().trim());
                    int timeout = Integer.valueOf(connKV.get("timeout").toString().trim());
                    DruidDataSource dataSource = new DruidDataSource();
                    dataSource.setUsername(user);
                    dataSource.setDriverClassName(driver);
                    dataSource.setPassword(password);
                    dataSource.setUrl(url);
                    dataSource.setInitialSize(initSize);
                    dataSource.setMaxActive(maxSize);
                    dataSource.setOracle(driver.equals("oracle.jdbc.driver.OracleDriver"));
                    dataSource.setQueryTimeout(timeout);
                    dataSource.setLoginTimeout(timeout);
                    dataSourceMap.put(key, dataSource);
                }
            }
        }
        return dataSourceMap.get(key);
    }
}
