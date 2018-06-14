package com.swj.mustang;

import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/3 9:36
 **/
class SwjTransaction {

    private static final Logger log = LoggerFactory.getLogger(SwjTransaction.class);

    private Map<String,DruidPooledConnection> connectionMap;

    private String uuid;

    private int joinTimes = 0;


    boolean isFinish() {
        return joinTimes < 0;
    }


    /**
     * 开启事务
     */
    void open() {
        if (!isOpenTran()) {
            connectionMap = new HashMap<>();
            uuid = UUID.randomUUID().toString();
            log.debug("transaction open,uuid:{}", uuid);
        } else {
            joinTimes++;
            log.debug("joinTimes add,uuid:{}", uuid);
        }
    }

    private boolean isOpenTran() {
        return connectionMap != null;
    }

    void add(String key,DruidPooledConnection connection) throws SQLException {
        connection.setAutoCommit(false);
        connectionMap.put(key,connection);
        log.debug("connection join,key:{},uuid:{}",key, uuid);
    }

    DruidPooledConnection get(String key)
    {
        if( connectionMap.containsKey(key))
        {
            return connectionMap.get(key);
        }
        return null;
    }

    /**
     * 提交事务
     */
    void commit() throws SQLException {
        if (isOpenTran()) {
            if (joinTimes == 0) {
                log.debug("transaction commit,uuid:{}", uuid);
                for (DruidPooledConnection connection : connectionMap.values()) {
                    if (connection != null) {
                        connection.commit();
                        connection.close();
                    }
                }
            }
            joinTimes--;
            log.debug("joinTimes dec,uuid:{}", uuid);

        }
    }

    /**
     * 回滚事务
     */
    void rollback() throws SQLException {
        if (isOpenTran()) {
            if (joinTimes == 0) {
                log.debug("transaction rollback,uuid:{}", uuid);
                for (DruidPooledConnection connection : connectionMap.values()) {
                    if (connection != null) {
                        connection.rollback();
                        connection.close();
                    }
                }
            }
            joinTimes--;
            log.debug("joinTimes dec,uuid:{}", uuid);
        }
    }

}
