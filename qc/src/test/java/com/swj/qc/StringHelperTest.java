package com.swj.qc;

import com.swj.basic.helper.StringHelper;

import org.junit.Assert;
import org.junit.Test;


/**
 * @Author 曾利娟【zenglj@3vjia.com】
 * @Description
 * @Datetime 2018/5/18-11:36
 */
public class StringHelperTest {
    @Test
    public void isObjectNullOrEmpty() throws Exception {
        boolean result = StringHelper.isObjectNullOrEmpty(null);
        Assert.assertTrue(result);

        result = StringHelper.isObjectNullOrEmpty("");//空字符串
        Assert.assertTrue(result);
    }

    @Test
    public void isObjectNullOrEmpty1() throws Exception {//todo 值得修改
        boolean result = StringHelper.isObjectNullOrEmpty("  ");
        Assert.assertTrue(!result);
    }



    @Test
    public void isNullOrWhiteSpace() throws Exception {
        boolean result = StringHelper.isNullOrWhiteSpace(null);
        Assert.assertTrue(result);

        result = StringHelper.isNullOrWhiteSpace("");
        Assert.assertTrue(result);

        result = StringHelper.isObjectNullOrEmpty("  ");
        Assert.assertTrue(!result);
    }

    /**
     * 比较两个字符串是否相等
     * @Param1 待比较参数1(String)
     * @Param2 待比较参数2(String)
     */
    @Test
    public void ignoreCaseEquals(){
        boolean result = false;

        //判断两个null
        result = StringHelper.ignoreCaseEquals(null,null);
        Assert.assertTrue(result);

        //判断两个空字符串
        result = StringHelper.ignoreCaseEquals("","");
        Assert.assertTrue(result);

        //判断一个空字符串和一个多空格字符串(失败)  //todo 应该成功
        result = StringHelper.ignoreCaseEquals(""," ");
        Assert.assertTrue(!result);

        //判断两个正常的字符串
        result = StringHelper.ignoreCaseEquals("111","111");
        Assert.assertTrue(result);

        //判断两个特殊符号的字符串
        result = StringHelper.ignoreCaseEquals("@#$%^&*","@#$%^&*");
        Assert.assertTrue(result);
    }

    @Test
    public void toLowerCaseFirstOne(){
        String value="Abc";
        String result = StringHelper.toLowerCaseFirstOne(value);
        Assert.assertEquals("abc",result);
    }
}