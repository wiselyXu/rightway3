package com.swj.freeway.rpc.exception;

import lombok.Getter;

/**
 * com.swj.freeway.freeway.exception
 *
 * @Description 远程调用异常
 * @Author 余焕【yuh@3vjia.com】
 * @Datetime 2018/5/9 20:08
 **/
@Getter
public class RpcException extends RuntimeException {

    /**
     * 错误描述
     */
    private String message;

    public RpcException(String message) {
        super(message);
        this.message = message;
    }

}
