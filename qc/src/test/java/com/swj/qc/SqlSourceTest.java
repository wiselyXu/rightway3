package com.swj.qc;

import com.swj.mustang.SqlSource;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;


/**
 * com.swj.framework.dao
 *
 * @Deacription
 * @Author :xug
 * @Date :2018/3/16
 */

public class SqlSourceTest {

    String statement;
    Map<String,Object> param=new HashMap<>();

    @Test
    public void findSqlByMsId() throws SQLException{

        statement= SqlSource.findSqlByMsId("select * from srb_flow",false,null);
        Assert.assertEquals("select * from srb_flow",statement);

    }

    @Test
    public void findSqlByMsId2() throws SQLException{

        statement=SqlSource.findSqlByMsId("user.delete",false,null);
        Assert.assertEquals(" delete from srb_bx where bxid=#bxid#",statement);

    }

    @Test
    public void findSqlByMsId3() throws SQLException{


        statement=SqlSource.findSqlByMsId("user.test",false,null);
        Assert.assertEquals(" select * from srb_flow where 1=3",statement);

    }

    @Test
    public void findSqlByMsId4() throws SQLException{

        statement=SqlSource.findSqlByMsId("user.get",false,null);
        Assert.assertEquals(" select * from srb_flow where 1=0",statement);

    }

    @Test
    public void findSqlByMsId5() throws SQLException{

        statement=SqlSource.findSqlByMsId("user.get",true,null);
        Assert.assertEquals(" select * from srb_flow where 1=1",statement);

    }

    @Test
    public void findSqlByMsId6() throws SQLException{

        param.put("flowid1","abcdefg");
        statement=SqlSource.findSqlByMsId("user.get",false,param);
        Assert.assertEquals(" select * from srb_flow where 1=0  or flowid=#flowid1#",statement);

    }

    @Test
    public void findSqlByMsId7() throws SQLException{

        param.put("flowid","abcdefg");
        statement=SqlSource.findSqlByMsId("user.get",false,param);
        Assert.assertEquals(" select * from srb_flow where 1=0  or flowid=#flowid#",statement);


    }
}