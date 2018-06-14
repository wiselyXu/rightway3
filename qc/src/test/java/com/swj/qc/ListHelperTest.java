package com.swj.qc;

import com.swj.basic.helper.ListHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description 集合帮助类测试用例
 * @Author 曾利娟【zenglj@3vjia.com】
 * @Datetime 2018/5/18-11:16
 */
public class ListHelperTest {

    @Test
    public void first() throws Exception {
        List list = new ArrayList();

        Object result = ListHelper.first(list);
        Assert.assertEquals(null,result);

        list.add("test1");
        list.add("test2");
        list.add("test3");
        result = ListHelper.first(list);
        Assert.assertEquals("test1",result);
    }

}