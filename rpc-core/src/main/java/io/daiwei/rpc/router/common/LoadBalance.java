package io.daiwei.rpc.router.common;

import java.util.List;

/**
 * 负载策略
 * Created by Daiwei on 2021/4/18
 */
public interface LoadBalance {

    String select(List<String> healthUrls, List<String> allAvailableUrls);

}
