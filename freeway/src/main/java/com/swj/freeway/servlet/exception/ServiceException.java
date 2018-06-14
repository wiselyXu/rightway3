package com.swj.freeway.servlet.exception;

import com.swj.freeway.servlet.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * 业务异常
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/28 14:07
 **/
@Getter
public class ServiceException extends RuntimeException {

    /** 错误码 */
    private int code;

    /** 错误描述 */
    private String message;

    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getErrorMsg());
        this.code = errorCodeEnum.getCode();
        this.message = errorCodeEnum.getErrorMsg();
    }

    public ServiceException(String message, Object... args) {
        super(getMessage(message, args));
        this.message = getMessage(message, args);
    }

    public ServiceException(int code, String message, Object... args) {
        super(getMessage(message, args));
        this.code = code;
        this.message = getMessage(message, args);
    }

    private static String getMessage(String message, Object... args) {
        String result = message;
        for (Object object : args) {
            result = result.replaceFirst("\\{\\}", object.toString());
        }
        return result;
    }

}
