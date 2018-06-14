package com.swj.basic;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 配置监听器
 */
public class SwjConfigListener implements IZkDataListener, IZkChildListener {

    private final static Logger logger = LoggerFactory.getLogger(SwjConfigListener.class);


    public void handleDataChange(String dataPath, Object data) {
        logger.info("data {} change,reload configProperties", dataPath);
        SwjConfig.put(dataPath);
    }

    public void handleDataDeleted(String dataPath) {
        logger.info("data {} delete,do nothing", dataPath);
    }

    public void handleChildChange(String parentPath, List<String> currentChilds) {
        logger.info("data {} add,reload configProperties", parentPath);
        if (parentPath.contains(SwjConfig.SOA_SERVICE_NODE)) {
            if (currentChilds.size() > 0) {
                SwjConfig.initSoa();
            } else {
                SwjConfig.removeSoaService(parentPath);
            }
        } else {
            SwjConfig.add(currentChilds);
        }
    }
}
