
package com.swj.qc;

import com.swj.basic.validation.EntityValidation;
import org.junit.Assert;
import org.junit.Test;

public class EntityValidationTest {
    @Test
    public void validate(){
        //空实体验证
        UserTest userTest=new UserTest();
        try{
            EntityValidation.validate(userTest);
        }catch (Exception e){
            Assert.assertTrue(e.getMessage().indexOf("字段 os 是必须的")>-1);
        }

        userTest.setOs("requireOs");
        try{
            EntityValidation.validate(userTest);
        }catch (Exception e){
            Assert.assertTrue(!(e.getMessage().indexOf("字段 os 是必须的")>-1));
        }


        // EntityValidation.validate(new HashMap<>());//todo 传map也没报错，有待商榷

    }
    @Test
    public void validate1(){
        ObjectModelTest obj = new ObjectModelTest();

        //未设值
        try {
            EntityValidation.validate(obj);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().indexOf("字段 testRange 的范围必须在 1 至 10 之间")>-1);
        }
        //小于最小值　
        try {
            obj.setTestRange(0);
            EntityValidation.validate(obj);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().indexOf("字段 testRange 的范围必须在 1 至 10 之间")>-1);
        }
        //大于最大值
        try {
            obj.setTestRange(11);
            EntityValidation.validate(obj);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().indexOf("字段 testRange 的范围必须在 1 至 10 之间")>-1);
        }
        //范围中设值　
        try {
            obj.setTestRange(8);
            EntityValidation.validate(obj);
        } catch (Exception e) {
            Assert.assertTrue(!(e.getMessage().indexOf("字段 testRange 的范围必须在 1 至 10 之间")>-1));
        }

    }

    @Test
    public void validate2(){
        ObjectModelTest obj = new ObjectModelTest();
        obj.setTestRange(8);
        obj.setTestLength("123456");
        try {
            EntityValidation.validate(obj);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().indexOf("testLength字段输入长度不能超过【2】")>-1);
        }
    }

    @Test
    public void validate3(){
        ObjectModelTest obj = new ObjectModelTest();
        obj.setTestRange(8);
        obj.setTestLength("1");
        obj.setTestPattern("123a");
        try {
            EntityValidation.validate(obj);//值不为空
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().indexOf("testPattern值不符合规则")>-1);
        }

        ObjectModelTest obj1 = new ObjectModelTest();
        try {
            EntityValidation.validate(obj1);//值为空
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().indexOf("testPattern值不符合规则")>-1);
        }
    }

    /**
     * 测试是否会校验父类实体
     * @author Zenglj
     * @date 2018/05/31
     */
    @Test
    public void Parentvalidate(){
        ObjectModelTest obj = new ObjectModelTest();
        obj.setTestRange(8);
        obj.setTestLength("1");
        obj.setTestPattern("123a");

        obj.setParentLength("1234");
        obj.setParentRange(12);
        obj.setParentPattern("123b");
        try {
            EntityValidation.validate(obj);
        } catch (Exception e) {
            String message="parentPattern值不符合规则; 字段 parentRange 的范围必须在 1 至 10 之间; parentLength字段输入长度不能超过【2】; testPattern值不符合规则";
            Assert.assertTrue(e.getMessage().indexOf(message)>-1);
        }

    }

}
