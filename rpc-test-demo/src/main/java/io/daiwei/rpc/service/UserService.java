package io.daiwei.rpc.service;

import io.daiwei.rpc.entity.User;

/**
 * Created by Daiwei on 2021/4/11
 */
public interface UserService {

    void sayHello(String name);

    User findByUser(Long id);
}
