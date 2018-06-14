package com.swj.mustang;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.SQLException;
import java.util.Stack;

/**
 * 事务管理器
 * @author liuhf
 * @since 2018/5/22 17:58
 **/
public class TransactionManager {


    private static ThreadLocal<Stack<SwjTransaction>> transactionThreadLocal = new ThreadLocal<>();


    public static void open() {
        Stack<SwjTransaction> transactionStack = get();
        if (transactionStack == null) {
            transactionStack = new Stack<>();
            SwjTransaction transaction = new SwjTransaction();
            transaction.open();
            transactionStack.push(transaction);
            transactionThreadLocal.set(transactionStack);
        } else {
            SwjTransaction transaction = transactionStack.peek();
            transaction.open();
        }
    }

    public static void openNew() {
        Stack<SwjTransaction> transactionStack = get();
        if (transactionStack == null) {
            transactionStack = new Stack<>();
            transactionThreadLocal.set(transactionStack);
        }
        SwjTransaction transaction = new SwjTransaction();
        transaction.open();
        transactionStack.push(transaction);
    }

    public static DruidPooledConnection get(String key,boolean isOracle) throws SQLException
    {
        Stack<SwjTransaction> transactionStack = get();
        if (transactionStack == null) {
            return DataSourceManager.createDataSource(key, isOracle).getConnection();
        }
        else
        {
            DruidPooledConnection temp=transactionStack.peek().get(key);
            if(temp==null)
            {
                temp = DataSourceManager.createDataSource(key,false).getConnection();
                transactionStack.peek().add(key,temp);
            }
            //如果开了事务，那么所有的查询也会走到非查询的连接上，不再走只读库
            return temp;
        }
    }

    static void closeConnection(DruidPooledConnection connection) throws SQLException {
        Stack<SwjTransaction> transactionStack = get();
        if (transactionStack == null) {
            connection.close();
        }
    }

    private static Stack<SwjTransaction> get() {
        return transactionThreadLocal.get();
    }

    public static void commit() throws SQLException {
        Stack<SwjTransaction> transactionStack = get();
        if (transactionStack != null) {
            SwjTransaction swjTransaction = transactionStack.peek();
            swjTransaction.commit();
            if (swjTransaction.isFinish()) {
                transactionStack.pop();
                if (transactionStack.isEmpty()) {
                    transactionThreadLocal.remove();
                }
            }
        }
    }

    public static void rollback() throws SQLException {
        Stack<SwjTransaction> transactionStack = get();
        if (transactionStack != null) {
            SwjTransaction swjTransaction = transactionStack.peek();
            swjTransaction.rollback();
            if (swjTransaction.isFinish()) {
                transactionStack.pop();
                if (transactionStack.isEmpty()) {
                    transactionThreadLocal.remove();
                }
            }
        }

    }

    public static void finalCheck() throws Exception {
        Stack<SwjTransaction> transactionStack = get();
        if (transactionStack != null) {
            if (!transactionStack.isEmpty()) {
                throw new Exception("事务管理器检测到有未提交的事务");
            }
        }
    }
}
