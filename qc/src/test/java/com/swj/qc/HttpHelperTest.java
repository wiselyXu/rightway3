package com.swj.qc;

import com.alibaba.fastjson.JSON;
import com.swj.basic.helper.ObjectHelper;
import com.swj.basic.helper.HttpHelper;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;


/**
 * com.swj.framework.helper
 *
 * @Deacription
 * @Author :xug
 * @Date :2018/3/16
 */

public class HttpHelperTest {

    //String getUrl = "http://172.18.3.122/api/User/List";
    String getUrl = "http://www.baidu.com/";
    String postUrl = "http://172.18.3.122/api/Party/SignOut?name=%E9%99%88%E5%98%89%E6%96%87";
    String postUrl1 = "http://172.18.3.122/api/Party/Message";

    @Test
    public void post() {

        try {
            String result = HttpHelper.post(postUrl,"utf-8","");
            ResultModel obj= JSON.parseObject(result,ResultModel.class);
            Assert.assertEquals(200,obj.getCode());
            Assert.assertTrue(obj.isSuccess());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void post1() {

        try {
            Map map= ObjectHelper.getParameter("name","陈嘉文");
            String result = HttpHelper.post(postUrl1,"utf-8",map);
            ResultModel obj= JSON.parseObject(result,ResultModel.class);
            Assert.assertEquals(200,obj.getCode());
            Assert.assertTrue(obj.isSuccess());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get() throws IOException {
        String result = HttpHelper.get(getUrl,"utf-8","");
        Assert.assertTrue(result.indexOf(getUrl)>-1);
    }
}