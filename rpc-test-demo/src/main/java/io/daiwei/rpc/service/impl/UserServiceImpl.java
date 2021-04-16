package io.daiwei.rpc.service.impl;

import io.daiwei.rpc.service.UserService;

/**
 * Created by Daiwei on 2021/4/16
 */
public class UserServiceImpl implements UserService {

    @Override
    public void sayHello(String name) {
        System.out.println("hello" + name);
    }
}
