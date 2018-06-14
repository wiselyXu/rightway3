package com.swj.basic;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

/**
 * com.swj.framework
 *
 * @author Chenjw
 * @since 2018/3/9
 **/
public class ResultWrap implements Serializable {

    @JSONField(ordinal = 0 ,name = "Status")
    private int status;

    @JSONField(ordinal = 1,name ="JSON")
    public String json;

    @JSONField(ordinal = 2,name ="ErrorMessage",serialzeFeatures = SerializerFeature.WriteMapNullValue)
    private String errorMessage;

    @JSONField(ordinal = 3,name ="InfoMessage",serialzeFeatures = SerializerFeature.WriteMapNullValue)
    private String infoMessage;


    public ResultWrap()
    {
        status=200;
        json="";
        errorMessage=null;
        infoMessage=null;


    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        if (infoMessage != null) {
            this.infoMessage = infoMessage;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            this.errorMessage = errorMessage;
        }
        this.setErrorCode();
    }

    public void setErrorMessage(String errorMessage, int status) {
        if (errorMessage != null) {
            this.errorMessage = errorMessage;
        }
        this.status = status;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        if (json != null) {
            this.json = json;
        }
    }

    public void setErrorCode() {
        status = 304;
    }
}
