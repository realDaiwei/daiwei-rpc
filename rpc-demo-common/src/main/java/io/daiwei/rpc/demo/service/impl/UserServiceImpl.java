package io.daiwei.rpc.demo.service.impl;

import io.daiwei.rpc.demo.pojo.User;
import io.daiwei.rpc.demo.service.UserService;

/**
 * Created by Daiwei on 2021/3/20
 */
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer id) {
        return new User(1, "daiwei", 24);
    }

}
