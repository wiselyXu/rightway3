package com.swj.basic.validation;


public class ValidateResult {

    private boolean isValid;

    private String errorMessage;

    public ValidateResult()
    {
        this.isValid=true;
        this.errorMessage=null;
    }

    public ValidateResult(String errorMessage)
    {
        this.errorMessage=errorMessage;
        this.isValid=false;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
