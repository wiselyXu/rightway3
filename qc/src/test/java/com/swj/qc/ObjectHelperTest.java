package com.swj.qc;


import com.swj.basic.helper.ObjectHelper;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.*;


/**
 * com.swj.framework.helper
 *
 * @Deacription
 * @Author :xug
 * @Date :2018/3/16
 */

public class ObjectHelperTest {

     //未覆盖的正好是6行catch内容  没执行，相办法让程序发生对应的异常
    @Test
    public void toMap() throws IllegalAccessException {


        //1,被转化对象为Entity
        //1.1  Entity为null, 则转化的map也为null
        Map map=new HashMap<>();
        map=ObjectHelper.toMap(new FlowDaoModel());
        for (Object value:map.values()){
            Assert.assertNull(value);
        }

        //1.2 Entity不为null， 但没有值, 则返回 只有key没有value的Map
        // map.clear();
        FlowDaoModel flowDaoModel= new FlowDaoModel();
        flowDaoModel.setAmount(3.0);
        map= ObjectHelper.toMap(flowDaoModel);
        Assert.assertNotNull(map);
        Assert.assertEquals(4,map.size());
        Assert.assertNull(map.get("flowid"));
        Assert.assertEquals(3.0,map.get("amount"));

        //1.3 Entity 不为null， 也有值,返回的Map，有对应的field 的key， 若有值，其值也为原field的值
        map.clear();
        flowDaoModel.setFlowtypeid("xug1001");
        map=ObjectHelper.toMap(flowDaoModel);
        Assert.assertNotNull(map);
        Assert.assertEquals("xug1001",flowDaoModel.getFlowtypeid());
        Assert.assertNull(flowDaoModel.getFlowid());

        //2：若object本身即为 Map， 则原样返回

        //2.1object 本身就是map, 有值,
        Map param=new HashMap();
        param.put(1,126);
        param.put(5,185);
        map=ObjectHelper.toMap(param);
        Assert.assertEquals(2,map.size());
        Assert.assertEquals(126,map.get(1));


        //2.2参数, map 的value为一个对象，仍然原样返回
        param.clear();
        param.put("1",flowDaoModel);
        flowDaoModel.setFlowtypeid("xug1002");
        param.put("2",flowDaoModel);
        map=ObjectHelper.toMap(param);
        Assert.assertTrue(map.get("1") instanceof FlowDaoModel);


    }

    @Test
    public void toMapNull(){

        Map<String,Object> map=ObjectHelper.toMap(null);
        Assert.assertNull(map);

        map=ObjectHelper.toMap(new HashMap<>());
        Assert.assertEquals(HashMap.class,map.getClass());
        Assert.assertEquals(0,map.size());

        map=ObjectHelper.toMap("");//todo 有待解决:传空字符串返回的也应该是空字符串
    }

    @Test
    public void toMapException(){

    }

    @Test
    public void toEntity() throws Exception {

        Map map=new HashMap();
        Map param=null;
        FlowDaoModel result=new FlowDaoModel();

        //1,传入null， 则返回null

        result=ObjectHelper.toEntity(param,FlowDaoModel.class);
        Assert.assertNull(result);

        //2,传入空map， 则返回空对象, 有对应的Entity 为null
        param=new HashMap();
        result=ObjectHelper.toEntity(param,FlowDaoModel.class);
        Assert.assertNull(result);

        //3,传入非空map， 返回实体类， 若map中存在与 Entity field 同名的key, 则对应的field值 为key所对应的值
        //3.1 map 不存在 与Entity field同名的key , 返回的Entity， 所有fields为值 为空
        param.clear();
        param.put("name",123);
        param.put("password","root@localhost");
        result= ObjectHelper.toEntity(param,FlowDaoModel.class);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof FlowDaoModel);
        Assert.assertNull(result.getFlowid());

        //3.2 map 存在与 Entity field同名的key ， 返回的Entity

        param.put("flowid","xug10001");
        param.put("flowtypeid","id001");
        result=ObjectHelper.toEntity(param,FlowDaoModel.class);
        Assert.assertNull(result.getAmount());
        Assert.assertEquals("xug10001",result.getFlowid());

        //4 若Entity中有静态变量,则不处理, 如EnterpriseMode 中有  accessNumber static变量
        EnterpriseModel  enterpriseModel= new EnterpriseModel();
        //enterpriseModel.setAccessNumber(520);
        param.clear();
        param.put("accessNumber",128);
        param.put("name","oupai");

        enterpriseModel=ObjectHelper.toEntity(param,EnterpriseModel.class);
        Assert.assertNotEquals(enterpriseModel.getAccessNumber(),(Integer)128);
        Assert.assertEquals("oupai",enterpriseModel.getName());

    }

    @Test
    public void toEntity1() throws Exception {
        //测试static  final 字段
        Map map=ObjectHelper.getParameter("id","001","name","angel","age","18");
        ObjectModelTest obj = ObjectHelper.toEntity(map,ObjectModelTest.class);
        Assert.assertEquals("initId",obj.getId());
        Assert.assertEquals("initName",obj.getName());
        Assert.assertEquals("18",obj.getAge());

        //测试传参类型和实体类型不匹配
        map=ObjectHelper.getParameter("id","002","name","pary","age",19);
        obj = ObjectHelper.toEntity(map,ObjectModelTest.class);
        Assert.assertEquals("initId",obj.getId());
        Assert.assertEquals("initName",obj.getName());
        Assert.assertEquals("19",obj.getAge());

        //测试传参值有null
        map=ObjectHelper.getParameter("id","002","name","pary","age",null);
        obj = ObjectHelper.toEntity(map,ObjectModelTest.class);
        Assert.assertEquals("initId",obj.getId());
        Assert.assertEquals("initName",obj.getName());
        Assert.assertNull(obj.getAge());
    }

    @Test
    public void getParameter(){
        Map<String, Object> map = ObjectHelper.getParameter("name","zlj","monther","12");
        Assert.assertEquals(2,map.size());
        Assert.assertEquals("zlj",map.get("name"));
        Assert.assertEquals("12",map.get("monther"));

        map = ObjectHelper.getParameter("name","zlj","monther","12","",18);//todo 空字符串缺少判断
        Assert.assertEquals(3,map.size());
        Assert.assertEquals(18,map.get(""));
    }

    @Test
    public void getParameterNull(){
        Map<String, Object> map = ObjectHelper.getParameter("name","zlj","monther");
        Assert.assertNull(map);

        map = ObjectHelper.getParameter(null);
        Assert.assertNull(map);

        map = ObjectHelper.getParameter();
        Assert.assertNull(map);

    }

    @Test
    public void addParameter(){
        Map<String, Object> oldMap=new HashMap<>();
        ObjectHelper.addParameter(oldMap,"key1","value1");
        oldMap.put("name","rose");
        oldMap.put("age","18");

        Map<String, Object> map = ObjectHelper.addParameter(oldMap,"sex",0);
        Assert.assertEquals(3,map.size());
        Assert.assertEquals("rose",map.get("name"));
        Assert.assertEquals("18",map.get("age"));
        Assert.assertEquals(0,map.get("sex"));

    }

    @Test
    public void addParameterNull(){
        Map<String, Object> oldMap=new HashMap<>();
        Map<String, Object> map = ObjectHelper.addParameter(oldMap,"key1","value1");
        Assert.assertEquals(oldMap,map);

        map = ObjectHelper.addParameter(null,"key1","value1");
        Assert.assertNull(map);

        oldMap.put("name","rose");
        map = ObjectHelper.addParameter(oldMap,"key1");
        Assert.assertEquals(oldMap,map);

        map = ObjectHelper.addParameter(oldMap,null);
        Assert.assertEquals(oldMap,map);

        map = ObjectHelper.addParameter(oldMap,"","");//todo 空字符串缺少判断
        Assert.assertEquals(oldMap,map);
    }

    @Test
    public void convertToTypeString()throws ParseException {
        Object result = ObjectHelper.convertToType(String.class,111);
        Assert.assertEquals(String.class,result.getClass());
    }

    @Test
    public void convertToTypeDate() throws ParseException {
        Date result = (Date)ObjectHelper.convertToType(Date.class,"2018-05-18");
        Date date = new Date(118,4,18);
        Assert.assertEquals(date.getTime(),result.getTime());
        Assert.assertEquals(Date.class,result.getClass());

        result = (Date)ObjectHelper.convertToType(Date.class,null);
        Assert.assertNull(result);

        Assert.assertEquals("",ObjectHelper.convertToType(Date.class,""));

        Assert.assertEquals("  ",ObjectHelper.convertToType(Date.class,"  "));
    }

    @Test
    public void convertToTypeByte() throws ParseException {
        Object result="";
        /**
         * 整型转　
         */
        //int 转 byte 成功
        int a=10;
        byte r1=(byte)a;
        result = ObjectHelper.convertToType(Byte.class,a);
        Assert.assertEquals(Byte.class,result.getClass());
        Assert.assertEquals(r1,result);

        //shot 转 byte 成功
        short b=10;
        r1=(byte)b;
        result = ObjectHelper.convertToType(Byte.class,b);
        Assert.assertEquals(Byte.class,result.getClass());
        Assert.assertEquals(r1,result);

        //long 转 byte 成功
        long c=10;
        r1=(byte)c;
        result = ObjectHelper.convertToType(Byte.class,c);
        Assert.assertEquals(Byte.class,result.getClass());
        Assert.assertEquals(r1,result);

        /**
         * 浮点型转　 失败
         */
        //double 转 byte 失败
        double d=10.909;
        r1=(byte)d;
        result = ObjectHelper.convertToType(Byte.class,d);//todo 转double类型的值为0
        Assert.assertEquals(Byte.class,result.getClass());
        Assert.assertEquals(r1,result);

        //float 转 byte 失败
        float e=10.756f;
        r1=(byte)e;
        result = ObjectHelper.convertToType(Byte.class,e);//todo 转double类型的值为0
        Assert.assertEquals(Byte.class,result.getClass());
        Assert.assertEquals(r1,result);

    }

    @Test
    public void convertToTypeShort()throws ParseException {
        Object result="";
        /**
         * 整型转　
         */
        //int 转 short 成功
        int a=10;
        short r1=(short)a;
        result = ObjectHelper.convertToType(Short.class,a);
        Assert.assertEquals(Short.class,result.getClass());
        Assert.assertEquals(r1,result);

        //byte 转 short 成功
        byte b=10;
        r1=(short) b;
        result = ObjectHelper.convertToType(Short.class,b);
        Assert.assertEquals(Short.class,result.getClass());
        Assert.assertEquals(r1,result);

        //long 转 short 成功
        long c=10;
        r1=(short) c;
        result = ObjectHelper.convertToType(Short.class,c);
        Assert.assertEquals(Short.class,result.getClass());
        Assert.assertEquals(r1,result);

        /**
         * 浮点型转　 失败
         */
        //double 转 short 失败
        double d=10.909;
        r1=(short) d;
        result = ObjectHelper.convertToType(Short.class,d);//todo 转double类型的值为0
        Assert.assertEquals(Short.class,result.getClass());
        Assert.assertEquals(r1,result);

        //float 转 short 失败
        float e=10.756f;
        r1=(short) e;
        result = ObjectHelper.convertToType(Short.class,e);//todo 转double类型的值为0
        Assert.assertEquals(Short.class,result.getClass());
        Assert.assertEquals(r1,result);

    }

    @Test
    public void convertToTypeInteger()throws ParseException {
        Object result="";
        /**
         * 整型转　
         */
        //String 转 Integer 成功
        String a="10";
        Integer r1=Integer.parseInt(a);
        result = ObjectHelper.convertToType(Integer.class,a);
        Assert.assertEquals(Integer.class,result.getClass());
        Assert.assertEquals(r1,result);

        //short 转 Integer 成功
        short b=10;
        r1=Integer.valueOf(b);
        result = ObjectHelper.convertToType(Integer.class,b);
        Assert.assertEquals(Integer.class,result.getClass());
        Assert.assertEquals(r1,result);
        //long 转 Integer 成功
        long c=10;
        r1=Integer.valueOf((int)c);
        result = ObjectHelper.convertToType(Integer.class,c);
        Assert.assertEquals(Integer.class,result.getClass());
        Assert.assertEquals(r1,result);

        /**
         * 浮点型转　 失败
         */
        //double 转 Integer 失败
        double d=10.909;
        r1=Integer.valueOf((int)d);
        result = ObjectHelper.convertToType(Integer.class,d);//todo 转double类型的值为0
        Assert.assertEquals(Integer.class,result.getClass());
        Assert.assertEquals(r1,result);

        //float 转 Integer 失败
        float e=10.756f;
        r1=Integer.valueOf((int)e);
        result = ObjectHelper.convertToType(Integer.class,e);//todo 转double类型的值为0
        Assert.assertEquals(Integer.class,result.getClass());
        Assert.assertEquals(r1,result);

    }

    @Test
    public void convertToTypeLong()throws ParseException {
        Object result="";
        /**
         * 整型转　
         */
        //String 转 Long 成功
        String a="10";
        Long r1=Long.parseLong(a);
        result = ObjectHelper.convertToType(Long.class,a);
        Assert.assertEquals(Long.class,result.getClass());
        Assert.assertEquals(r1,result);

        //int 转 Long 成功
        int b=10;
        r1=Long.valueOf(b);
        result = ObjectHelper.convertToType(Long.class,b);
        Assert.assertEquals(Long.class,result.getClass());
        Assert.assertEquals(r1,result);

        //short 转 Long 成功
        short c=10;
        r1=Long.valueOf(c);
        result = ObjectHelper.convertToType(Long.class,c);
        Assert.assertEquals(Long.class,result.getClass());
        Assert.assertEquals(r1,result);

        /**
         * 浮点型转　 失败
         */
        //double 转 Long 失败
        double d=10.909;
        //r1=Long.valueOf(d);
        result = ObjectHelper.convertToType(Long.class,d);//todo  double转Long类型的值为0
        Assert.assertEquals(Long.class,result.getClass());
        Assert.assertEquals(10.909,result);

    }

    @Test
    public void convertToTypeDouble()throws ParseException {
        Object result="";
        /**
         * 整型转　
         */
        //String 转 Double 成功
        String a="10";
        Double r1=Double.parseDouble(a);
        result = ObjectHelper.convertToType(Double.class,a);
        Assert.assertEquals(Double.class,result.getClass());
        Assert.assertEquals(r1,result);

        //int 转 Double 成功
        int b=10;
        r1=Double.valueOf(b);
        result = ObjectHelper.convertToType(Double.class,b);
        Assert.assertEquals(Double.class,result.getClass());
        Assert.assertEquals(r1,result);

        //short 转 Double 成功
        short c=10;
        //r1=Double.valueOf(c);
        result = ObjectHelper.convertToType(Double.class,c);
        Assert.assertEquals(Double.class,result.getClass());
        Assert.assertEquals(r1,result);

        /**
         * 浮点型转　 失败
         */
        //float 转 Double 失败
        float e=10.756f;
        r1=Double.valueOf(e);
        result = ObjectHelper.convertToType(Double.class,e);//todo 转double类型的值为0
        Assert.assertEquals(Double.class,result.getClass());
        Assert.assertEquals(10.756,result);
    }

    @Test
    public void convertToTypeFloat()throws ParseException {
        Object result="";
        /**
         * 整型转　
         */
        //String 转 Float 成功
        String a="10";
        Float r1=Float.parseFloat(a);
        result = ObjectHelper.convertToType(Float.class,a);
        Assert.assertEquals(Float.class,result.getClass());
        Assert.assertEquals(r1,result);

        //int 转 Float 成功
        int b=10;
        r1=Float.valueOf(b);
        result = ObjectHelper.convertToType(Float.class,b);
        Assert.assertEquals(Float.class,result.getClass());
        Assert.assertEquals(r1,result);
        //short 转 Float 成功
        short c=10;
        r1=Float.valueOf(c);
        result = ObjectHelper.convertToType(Float.class,c);
        Assert.assertEquals(Float.class,result.getClass());
        Assert.assertEquals(r1,result);

        /**
         * 浮点型转　 失败
         */
        //double 转 Float 失败
        double e=10.75;
        r1=Float.valueOf((int)e);
        result = ObjectHelper.convertToType(Float.class,e);//todo 转double类型的值为0
        Assert.assertEquals(Float.class,result.getClass());
        Assert.assertEquals(10.75f,result);
    }

    @Test
    public void convertToTypeBoolean()throws ParseException {
        Object result="";
        //String 转 boolean 成功
        String a="true";
        result = ObjectHelper.convertToType(Boolean.class,a);
        Assert.assertEquals(Boolean.class,result.getClass());
        Assert.assertTrue((Boolean) result);

        String b="false";
        result = ObjectHelper.convertToType(Boolean.class,b);
        Assert.assertEquals(Boolean.class,result.getClass());
        Assert.assertTrue(!(Boolean) result);
    }
    @Test
    public void convertToTypeException(){
        Object result="";
        try{
            result = ObjectHelper.convertToType(Date.class,"jjj");
        }catch (Exception e){
            Assert.assertEquals("Unparseable date: \"jjj\"",e.getMessage());
        }
    }
}