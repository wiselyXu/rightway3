package com.swj.qc;


import com.swj.mustang.SqlProvider;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * com.swj.framework.dao
 *
 * @DESCRIPTION
 * @AUTHOR Chenjw
 * @DATE 2018/3/13
 * @EMAIL chenjw@3vjia.com
 **/
public class SqlProviderTest {

    private static SqlProvider provider;

    @BeforeClass
    public static void init()
    {
        provider=new SqlProvider("mysql");
    }

    @Test
    public void selectOne() throws Exception {
        String sql = provider.selectOne(UserTest.class);
       Assert.assertEquals("select `bxid`,`ts`,`aa` from `srb_bx` where `bxid` = #bxid#",sql);
    }

    @Test
    public void selectOneFlow(){
        String sql = provider.selectOne(FlowDaoModel.class);
        Assert.assertEquals("select `flowdetailid`,`flowid`,`flowtypeid`,`amount` from `dao_flow_detail` where `flowdetailid` = #flowdetailid#",sql);
    }


    @Test
    public void selectAll() throws Exception {
        String sql = provider.selectAll(UserTest.class);
        Assert.assertEquals("select `bxid`,`ts`,`aa` from `srb_bx`",sql);
    }

    @Test
    public void selectAllFlow() throws Exception {
        String sql = provider.selectAll(FlowDaoModel.class);
        Assert.assertEquals("select `flowdetailid`,`flowid`,`flowtypeid`,`amount` from `dao_flow_detail`",sql);
    }


    @Test
    public void insert() throws Exception {
        String sql =  provider.insert(UserTest.class);
        Assert.assertEquals("insert into `srb_bx` (`bxid`,`name`,`ts`,`aa`) values (#bxid#,#name#,#ts#,#os#)",sql);
    }

    @Test
    public void insertFlow() throws Exception {
        String sql =  provider.insert(FlowDaoModel.class);
        Assert.assertEquals("insert into `dao_flow_detail` (`flowdetailid`,`flowid`,`flowtypeid`,`amount`) values (#flowdetailid#,#flowid#,#flowtypeid#,#amount#)",sql);
    }

    @Test
    public void updateBySelective() throws Exception {
        String sql = provider.update(UserTest.class);
        Assert.assertEquals("update `srb_bx` set `name` = #name# {?, `aa` = #os# } where `bxid` = #bxid#",sql);
    }

    @Test
    public void updateFlow() throws Exception {
        String sql = provider.update(FlowDaoModel.class);
        Assert.assertEquals("update `dao_flow_detail` set `flowid` = #flowid# {?, `flowtypeid` = #flowtypeid# }{?, `amount` = #amount# } where `flowdetailid` = #flowdetailid#",sql);
    }

    @Test
    public void delete() throws Exception {
        String sql =  provider.delete(UserTest.class);
        Assert.assertEquals("delete from `srb_bx` where `bxid` = #bxid#",sql);
    }

    @Test
    public void deleteFlow() throws Exception {
        String sql =  provider.delete(FlowDaoModel.class);
        Assert.assertEquals("delete from `dao_flow_detail` where `flowdetailid` = #flowdetailid#",sql);
    }

    @Test
    public void updateFieldWithSpecColumn()
    {
        String sql = provider.updateWithSepcColumn(FlowDaoModel.class,"flowid","flowtypeid");
        Assert.assertEquals("update `dao_flow_detail` set `flowid` = #flowid#,`flowtypeid` = #flowtypeid# where `flowdetailid` = #flowdetailid#",sql);
    }

    @Test
    public void updateFieldWithSpecColumn1()
    {
        try {
            String sql = provider.updateWithSepcColumn(FlowDaoModel.class, "flowd", "flowtypeid");
        }
        catch (Exception ex) {
            Assert.assertEquals(IllegalArgumentException.class,ex.getClass());
        }
    }


    @Test
    public void updateFieldWithSpecColumn2()
    {
        try {
            String sql = provider.updateWithSepcColumn(FlowDaoModel.class, "flowdetailid", "flowtypeid");
        }
        catch (Exception ex) {
            Assert.assertEquals(IllegalArgumentException.class,ex.getClass());
        }
    }

    @Test
    public void updateFieldWithSpecColumn3()
    {
        try {
            String sql = provider.updateWithSepcColumn(FlowDaoModel.class);
        }
        catch (Exception ex) {
            Assert.assertEquals(IllegalArgumentException.class,ex.getClass());
        }
    }

    @Test
    public void getpkFromClassTest()
    {
        Map<String,Object> map = SqlProvider.getPkFromClass(FlowDaoModel.class,"1");
        Assert.assertEquals(1,map.size());
        Assert.assertEquals(true,map.containsKey("flowdetailid"));
        Assert.assertEquals("1",map.get("flowdetailid"));
    }

    @Test
    public void getpkFromClassTest1()
    {
        Map<String,Object> map = SqlProvider.getPkFromClass(UnPKClass.class,"1");
        Assert.assertEquals(null,map);
    }
}