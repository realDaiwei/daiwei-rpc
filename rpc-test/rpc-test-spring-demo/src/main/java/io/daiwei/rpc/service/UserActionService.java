package io.daiwei.rpc.service;

import io.daiwei.rpc.entity.User;
import io.daiwei.rpc.stub.invoker.annotation.RpcRef;

/**
 * Created by Daiwei on 2021/5/1
 */

public class UserActionService {

    @RpcRef
    private UserService userService;

    public User getUser() {
        return userService.findByUser(10L);
    }

}
