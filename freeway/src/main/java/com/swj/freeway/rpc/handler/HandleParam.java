package com.swj.freeway.rpc.handler;

import lombok.Getter;
import lombok.Setter;

/**
 * com.swj.freeway.config
 *
 * @Description
 * @Author 余焕【yuh@3vjia.com】
 * @Datetime 2018/5/10 16:04
 **/
@Getter
@Setter
public class HandleParam {

    private String version;

    private String servicePath;

    private String apiName;

    private String appNode;

}
