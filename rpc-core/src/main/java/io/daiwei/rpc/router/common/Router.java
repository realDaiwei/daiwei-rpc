package io.daiwei.rpc.router.common;

import java.lang.reflect.Method;
import java.util.List;

/**
 * rpc 路由策略
 * Created by Daiwei on 2021/4/18
 */
public interface Router {

    List<String> route(List<String> urls, Method method, Object[] args);

}
