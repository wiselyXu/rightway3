package com.swj.freeway.zookeeper;

/**
 * com.swj.freeway.zookeeper
 * @author 余焕【yuh@3vjia.com】
 * @since 2018/5/13 0:42
 **/
public class ZnodeUtils {

    public static String getAppNode(Class clazz) {
        String[] packageSplit = clazz.getPackage().getName().split("\\.");
        return packageSplit[packageSplit.length - 1];
    }

}
