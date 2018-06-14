package com.swj.basic.validation;


import com.swj.basic.annotation.Length;
import com.swj.basic.helper.StringHelper;

/**
 * com.swj.framework.validation
 *
 * @DESCRIPTION
 * @AUTHOR Chenjw
 * @DATE 2018/4/17
 * @EMAIL chenjw@3vjia.com
 **/
public class LengthValidator implements Validator {

    @Override
    public ValidateResult validate(String fieldName, Object entity, Object annotation) {
        if(StringHelper.isObjectNullOrEmpty(entity)){
            return new ValidateResult();
        }
        Length length = (Length) annotation;
        String  errorMessage = String.format("%s字段输入长度不能超过【%s】",fieldName,((Length) annotation).value());
       if (String.valueOf(entity).length() >length.value()){
           return new ValidateResult(errorMessage);
       }
         return new ValidateResult();
    }
}
