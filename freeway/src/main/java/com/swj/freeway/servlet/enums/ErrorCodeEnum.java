package com.swj.freeway.servlet.enums;

import lombok.Getter;

/**
 * 错误码枚举
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/28 10:51
 **/
@Getter
public enum ErrorCodeEnum {

    REQUEST_PARAM_INVALID(1, "请求参数校验不通过"),
    COMMAND_INVALID(2, "指令无效"),
    SYSTEM_ERROR(998, "系统错误"),
    UNKNOWN_ERROR(999, "未知错误");

    private int    code;
    private String errorMsg;

    ErrorCodeEnum(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

}
