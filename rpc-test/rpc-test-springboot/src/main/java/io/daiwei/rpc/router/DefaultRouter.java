package io.daiwei.rpc.router;

import io.daiwei.rpc.router.common.Router;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Daiwei on 2021/5/3
 */
public class DefaultRouter implements Router {

    @Override
    public List<String> route(List<String> urls, Method method, Object[] args) {
        System.out.println(Arrays.toString(urls.toArray()) + "#" + method.getName() + "#" + Arrays.toString(args));
        return urls;
    }
}
