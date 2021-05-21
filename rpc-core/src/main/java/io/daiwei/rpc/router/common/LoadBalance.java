package io.daiwei.rpc.router.common;

import java.util.List;
import java.util.Map;

/**
 * 负载策略
 * Created by Daiwei on 2021/4/18
 */
public abstract class LoadBalance {

    protected Map<String, String> dataMap;

    public void setDataMap(Map<String, String> map) {
        this.dataMap = map;
    }

    public abstract String select(List<String> healthUrls, List<String> allAvailableUrls);

}
