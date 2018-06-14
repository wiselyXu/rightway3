package com.swj.qc;

import com.alibaba.fastjson.JSONException;
import com.swj.basic.helper.JsonHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.junit.Assert.fail;


/**
 * com.swj.framework.helper
 *
 * @Deacription
 * @Author :xug
 * @Date :2018/3/16
 */

public class JsonHelperTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    //所有的catch 中异常 没有覆盖到。
    @Test
    public void parseBean() {

        String jsonString="";
        FlowDaoModel flowDaoModel=new FlowDaoModel();
        //1，jsonString 为null,则返回null
        flowDaoModel= JsonHelper.parseBean(null,FlowDaoModel.class);
        Assert.assertNull(flowDaoModel);

        //2，jsonString 为空, 则返回一个null
        flowDaoModel=JsonHelper.parseBean(jsonString,FlowDaoModel.class);
        Assert.assertNull(flowDaoModel);

        //3，jsonString 非空， 但没有 bean 对应的field,返回bean， 但bean中的field为null
        jsonString="{name:'xug10001',address:'gz'}";
        flowDaoModel=JsonHelper.parseBean(jsonString,FlowDaoModel.class);
        Assert.assertNotNull(flowDaoModel);
        Assert.assertNull(flowDaoModel.getFlowid());

        //4，jsonString 非空， 且仅有bean中的field 作为key
        jsonString="{flowtypeid:'xugtp0001',flowid:'id0001',flowdetailid:'xugde10001'}";
        flowDaoModel=JsonHelper.parseBean(jsonString,FlowDaoModel.class);
        Assert.assertNotNull(flowDaoModel);
        Assert.assertNull(flowDaoModel.getAmount());
        Assert.assertEquals("id0001",flowDaoModel.getFlowid());

    }

    @Test
    public void parseBean1(){
        String jsonString="";
        jsonString="{flowtypeid:'xugtp0001',flowid:'id0001',flowdetailid:'xugde10001'}";
        //JsonHelper.parseBean(jsonString,);
    }

    @Test
    public void parseBeanException(){
        try
        {
            String jsonString="{name:xug10001','address':'gz'}";
            FlowDaoModel flowDaoModel=JsonHelper.parseBean(jsonString,FlowDaoModel.class);
       }
        catch (Exception ex)
        {
            Assert.assertEquals(JSONException.class,ex.getClass());
        }
    }


    @Test
    public void parseBeanArray() {

        String jsonStrin="[{flowtypeid:'xugtp0001',flowid:'id0001',flowdetailid:'xugde10001'}," +
                "{flowtypeid:'xugtp0001',flowid:'id0001',flowdetailid:'xugde1000',amount:8500}," +
                "{flowtypeid:'xugtp0001',flowid:'id0001',flowdetailid:'xugde10003'}]";
        List<FlowDaoModel> flowDaoModels=JsonHelper.parseBeanArray(jsonStrin,FlowDaoModel.class);

        Assert.assertEquals(3,flowDaoModels.size());
        Assert.assertEquals(8500,flowDaoModels.get(1).getAmount(),0);




    }

    @Test
    public void parseListMap() {
        String jsonStrin="[{flowtypeid:'xugtp0001',flowid:'id0001',flowdetailid:'xugde10001'}," +
                "{name:'xugtp0001',address:'id0001',flowdetailid:'xugde1000',amount:8500}," +
                "{flowtypeid:'xugtp0001',flowid:'id0001',flowdetailid:'xugde10003'}]";
        List<Map<String,Object>> mapList=JsonHelper.parseListMap(jsonStrin);
        Assert.assertEquals(3,mapList.size());
        Assert.assertEquals("xugtp0001",mapList.get(1).get("name"));
    }

    @Test
    public void toJsonString() {
        FlowDaoModel flowDaoModel=new FlowDaoModel();
        flowDaoModel.setFlowtypeid("xug12335");
        String jsonString = JsonHelper.toJsonString(flowDaoModel);
        //System.out.println(jsonString);
        Assert.assertEquals("{\"flowtypeid\":\"xug12335\"}",jsonString);
    }
}