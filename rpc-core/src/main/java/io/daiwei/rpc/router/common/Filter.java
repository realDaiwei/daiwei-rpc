package io.daiwei.rpc.router.common;

import java.lang.reflect.Method;

/**
 * Created by Daiwei on 2021/4/18
 */
public interface Filter {

    boolean filter(String urls, Method method, Object[] args);

}
