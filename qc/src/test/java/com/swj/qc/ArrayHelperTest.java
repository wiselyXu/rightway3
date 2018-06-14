package com.swj.qc;


import com.swj.basic.helper.ArrayHelper;

import org.junit.Assert;
import org.junit.Test;

public class ArrayHelperTest {
    Object [] arr={};
    Object [] arr1={1,2,3};
    @Test
    public void isEmpty() throws Exception {
        Boolean result = ArrayHelper.isEmpty(arr);
        Boolean result1 = ArrayHelper.isEmpty(arr1);
        Assert.assertEquals(true,result);
        Assert.assertEquals(false,result1);
    }

    @Test
    public void isNotEmpty() throws Exception {
        Boolean result = ArrayHelper.isNotEmpty(arr);
        Boolean result1 = ArrayHelper.isNotEmpty(arr1);
        Assert.assertEquals(false,result);
        Assert.assertEquals(true,result1);
    }

}