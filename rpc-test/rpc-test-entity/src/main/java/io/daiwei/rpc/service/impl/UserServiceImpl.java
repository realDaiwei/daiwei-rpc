package io.daiwei.rpc.service.impl;


import io.daiwei.rpc.entity.ClassInfo;
import io.daiwei.rpc.entity.User;
import io.daiwei.rpc.service.UserService;
import io.daiwei.rpc.stub.provider.annotation.RpcService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Daiwei on 2021/4/16
 */
@RpcService()
public class UserServiceImpl implements UserService {

    @Override
    public void sayHello(String name) {
        System.out.println("hello" + name);
    }

    @Override
    public User findByUser(Long id) {
//        Thread.sleep(10 * 1000);
        ClassInfo info = ClassInfo.builder().grade(2).schoolName("hello school").stuMap(Collections.singletonMap("daiwei", "daiwei"))
                .nums(Arrays.asList(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN)).build();
        return User.builder().id(id).username("daiwei").age(26).info(info).build();
    }
}
