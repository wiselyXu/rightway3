package com.swj.basic.validation;


import com.swj.basic.annotation.Range;
import com.swj.basic.helper.StringHelper;

public class RangeValidator implements Validator {
    @Override
    public ValidateResult validate(String fieldName, Object entity, Object annotation) {


        if(StringHelper.isObjectNullOrEmpty(entity))
        {
            return new ValidateResult();
        }
        Range range = (Range)annotation;
        String errorMessage = String.format("字段 %s 的范围必须在 %s 至 %s 之间",fieldName,range.min(),range.max());
        int  i = Integer.parseInt(entity.toString());
        if(i<range.min() || i>range.max()) {
            return new ValidateResult(errorMessage);
        }
        return new ValidateResult();
    }
}

