package io.daiwei.rpc.demo.service;

import io.daiwei.rpc.demo.pojo.User;

/**
 * Created by Daiwei on 2021/3/20
 */
public interface UserService {

    User findById(Integer id);
}
