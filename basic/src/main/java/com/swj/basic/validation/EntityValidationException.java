package com.swj.basic.validation;


public class EntityValidationException extends IllegalArgumentException {

    public EntityValidationException(String errorMessage)
    {
        super(errorMessage);
    }
}
