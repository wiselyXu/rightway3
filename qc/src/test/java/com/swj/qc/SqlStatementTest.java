package com.swj.qc;


import com.swj.basic.helper.ObjectHelper;
import com.swj.mustang.SqlSource;
import com.swj.mustang.SqlStatement;

import org.junit.Assert;
import org.junit.Test;


/**
 * com.swj.framework.dao
 *
 * @Deacription
 * @Author :xug
 * @Date :2018/3/16
 */

public class SqlStatementTest {

    @Test
    public void getClauses() {
        SqlStatement statement= new SqlStatement();
        //statement.getClauses();
        Assert.assertNull(statement.getClauses());
    }

    @Test
    public void setClauses() {
    }

    @Test
    public void getStatement() {
        String sql = SqlSource.findSqlByMsId("user.doubleparameter",true, ObjectHelper.getParameter("bxid","aa"));
        Assert.assertEquals(" select * from srb_bx where 1=1  and bxid=#bxid# and bxid1=#bxid#",sql);
    }
}