package com.swj.qc;

import com.alibaba.fastjson.JSON;
import com.swj.basic.PageResult;
import com.swj.basic.helper.CacheHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * com.swj.framework.helper
 *
 * @DESCRIPTION
 * @AUTHOR Chenjw
 * @DATE 2018/3/21
 * @EMAIL chenjw@3vjia.com
 **/
public class CacheHelperTest {
    @Test
    public void initRedis(){
        CacheHelper cacheHelper = new CacheHelper();
        CacheHelper.getValue("key1");
        //CacheHelper.
        //System.out.println();
    }

    @Test
    public void deleteCache() throws Exception {
        String key="test100";
        CacheHelper.setValue(key,"test1");
        Assert.assertEquals("test1",CacheHelper.getValue(key));
        CacheHelper.deleteCache(key);
        Assert.assertNull(CacheHelper.getValue(key));
    }



    @Test
    public void setJsonValue() throws Exception {
        List list = new ArrayList();
        PageResult userTest=new PageResult();
        userTest.setRecordCount(0);
        userTest.setResult(list);
        CacheHelper.setJsonValue("testJsonValue1",userTest);
        String testJsonValue1 = CacheHelper.getValue("testJsonValue1");
        PageResult entity = JSON.parseObject(testJsonValue1,PageResult.class);
        Assert.assertEquals(0,entity.getRecordCount());
        Assert.assertEquals(0,entity.getResult().size());
    }

    @Test
    public void setJsonValue1() throws Exception {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        PageResult userTest=new PageResult();
        userTest.setRecordCount(0);
        userTest.setResult(list);
        CacheHelper.setJsonValue("testJsonValue1",userTest,60000);
        String testJsonValue1 = CacheHelper.getValue("testJsonValue1");
        PageResult entity = JSON.parseObject(testJsonValue1,PageResult.class);
        Assert.assertEquals(0,entity.getRecordCount());
        Assert.assertEquals(2,entity.getResult().size());
    }

    @Test
    public void getJsonObject() throws Exception {
    }


    @Test
    public void setValue() throws Exception {
        CacheHelper.setValue("test1","test1");
        CacheHelper.setValue("test4","test4");

        Assert.assertEquals("test1",CacheHelper.getValue("test1"));
        Assert.assertEquals("test4",CacheHelper.getValue("test4"));

    }

    @Test
    public void setValue1() throws Exception {
        CacheHelper.setValue(null,"null1");
        Assert.assertNull(CacheHelper.getValue(null));

        CacheHelper.setValue("","null2");
        Assert.assertNull(CacheHelper.getValue(""));

        CacheHelper.setValue("   ","null3");//todo 没用trim()
        //Assert.assertNull(CacheHelper.getValue("   "));

        CacheHelper.setValue("null","null4");//todo null字符串是否需要排除
        Assert.assertEquals("null4",CacheHelper.getValue("null"));

    }

    @Test
    public void setValue2() throws Exception {
        CacheHelper.setValue("v1",null);
        Assert.assertNull(CacheHelper.getValue("v1"));

        CacheHelper.setValue("v2","null");
        Assert.assertEquals("null",CacheHelper.getValue("v2"));

        CacheHelper.setValue("v3","");
        Assert.assertNull(CacheHelper.getValue("v3"));

        CacheHelper.setValue("v4","   ");
        Assert.assertNull(CacheHelper.getValue("v4"));

    }

    @Test
    public void setValueWhenNotExists() throws Exception {
        CacheHelper.setValue("test2","test3_1");
    }


    @Test
    public  void getValue() throws Exception {
        CacheHelper.setValue("t1","t1");
        CacheHelper.setValue("t2","t2");
        Assert.assertEquals("t1",CacheHelper.getValue("t1"));
        Assert.assertEquals("t2",CacheHelper.getValue("t2"));
    }
}