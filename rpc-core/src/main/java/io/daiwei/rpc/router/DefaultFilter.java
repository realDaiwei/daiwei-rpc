package io.daiwei.rpc.router;

import io.daiwei.rpc.router.common.Filter;

/**
 * Created by Daiwei on 2021/4/18
 */
public class DefaultFilter implements Filter {

    @Override
    public boolean filter(String urls) {
        return true;
    }
}
