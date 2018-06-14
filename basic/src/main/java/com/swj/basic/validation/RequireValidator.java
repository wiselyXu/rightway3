package com.swj.basic.validation;


import com.swj.basic.helper.StringHelper;

public class RequireValidator  implements Validator {

    @Override
    public ValidateResult validate(String fieldName,Object entity, Object annotation) {
        if(StringHelper.isObjectNullOrEmpty(entity))
        {
            return new ValidateResult(String.format("字段 %s 是必须的",fieldName));
        }
        return new ValidateResult();
    }
}
