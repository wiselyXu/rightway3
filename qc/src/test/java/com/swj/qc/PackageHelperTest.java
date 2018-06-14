package com.swj.qc;

import com.swj.basic.helper.PackageHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * com.swj.framework.helper
 *
 * @DESCRIPTION
 * @AUTHOR Chenjw
 * @DATE 2018/3/15
 * @EMAIL chenjw@3vjia.com
 **/
public class PackageHelperTest {

    /**
     * 获取某包下（包括该包的所有子包）所有类
     */
    @Test
    public void getClassName() throws IOException {
        String packageName="com.swj.basic.annotation";
        List<String> list = PackageHelper.getClassName(packageName);//默认遍历子包
        Assert.assertTrue(list.contains(packageName+".Column"));
        Assert.assertTrue(list.contains(packageName+".ErrorMessage"));
        Assert.assertTrue(list.contains(packageName+".Length"));
        Assert.assertTrue(list.contains(packageName+".Pattern"));
        Assert.assertTrue(list.contains(packageName+".Require"));
        Assert.assertTrue(list.contains(packageName+".Table"));
        Assert.assertTrue(list.contains(packageName+".Transaction"));

        packageName="com.swj.basic";
        List<String> list2 = PackageHelper.getClassName(packageName,false);
        Assert.assertTrue(list2.contains(packageName+".PageResult"));
        Assert.assertTrue(list2.contains(packageName+".ResultWrap"));
        Assert.assertTrue(list2.contains(packageName+".ReturnList"));
        Assert.assertTrue(list2.contains(packageName+".SwjConfig"));
        Assert.assertTrue(list2.contains(packageName+".SwjZkSerializer"));
        Assert.assertTrue(list2.contains(packageName+".TableColumn"));
        Assert.assertTrue(list2.contains(packageName+".TypeColumn"));
        Assert.assertTrue(list2.contains(packageName+".TypeColumnMapping"));

        Assert.assertTrue(!list2.contains(packageName+".Require"));
        Assert.assertTrue(!list2.contains(packageName+".Table"));
        Assert.assertTrue(!list2.contains(packageName+".Transaction"));

        packageName="com.swj.basic";
        List<String> list3 = PackageHelper.getClassName(packageName,true);
        Assert.assertTrue(list3.contains(packageName+".PageResult"));
        Assert.assertTrue(list3.contains(packageName+".ResultWrap"));
        Assert.assertTrue(list3.contains(packageName+".ReturnList"));
        Assert.assertTrue(list3.contains(packageName+".SwjConfig"));
        Assert.assertTrue(list3.contains(packageName+".SwjZkSerializer"));
        Assert.assertTrue(list3.contains(packageName+".TableColumn"));
        Assert.assertTrue(list3.contains(packageName+".TypeColumn"));
        Assert.assertTrue(list3.contains(packageName+".TypeColumnMapping"));

        Assert.assertTrue(list3.contains(packageName+".annotation.Require"));
        Assert.assertTrue(list3.contains(packageName+".annotation.Table"));
        Assert.assertTrue(list3.contains(packageName+".annotation.Transaction"));
    }

    @Test
    public void getClassNameFile() throws IOException {
        //todo jar包无法加载到，测试也无法覆盖这一块的代码
        /*String packageName="com.swj";
        List<String> list = PackageHelper.getClassName(packageName,false);
        Assert.assertEquals(10,list.size());*/
    }
}