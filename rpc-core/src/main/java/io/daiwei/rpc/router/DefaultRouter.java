package io.daiwei.rpc.router;

import io.daiwei.rpc.router.common.Router;

import java.util.List;

/**
 * Created by Daiwei on 2021/4/18
 */
public class DefaultRouter implements Router {

    @Override
    public List<String> route(List<String> urls) {
        return urls;
    }
}
