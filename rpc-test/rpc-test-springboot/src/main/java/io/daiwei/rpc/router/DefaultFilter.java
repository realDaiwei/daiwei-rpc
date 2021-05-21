package io.daiwei.rpc.router;

import io.daiwei.rpc.router.common.Filter;

import java.lang.reflect.Method;

/**
 * Created by Daiwei on 2021/5/3
 */
public class DefaultFilter implements Filter {

    @Override
    public boolean filter(String urls, Method method, Object[] args) {
        System.out.println("default filter!!!");
        return true;
    }
}
