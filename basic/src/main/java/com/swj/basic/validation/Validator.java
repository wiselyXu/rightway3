package com.swj.basic.validation;

public interface Validator {

    ValidateResult validate(String fieldName,Object entity,Object annotation);

}
