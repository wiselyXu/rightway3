package com.swj.basic.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 返回结果 封装
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/15 14:44
 **/
@Getter
@Setter
@ToString
public class ResultWrap<T> implements Serializable {

    private boolean success = false;

    private T result;

    private Integer errorCode;

    private String errorMessage;

}
