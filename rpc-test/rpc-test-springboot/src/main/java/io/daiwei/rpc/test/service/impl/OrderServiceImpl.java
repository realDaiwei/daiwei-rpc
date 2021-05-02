package io.daiwei.rpc.test.service.impl;

import io.daiwei.rpc.entity.Order;
import io.daiwei.rpc.stub.provider.annotation.RpcService;
import io.daiwei.rpc.service.OrderService;

import java.util.Date;

/**
 * Created by Daiwei on 2021/5/2
 */
@RpcService
public class OrderServiceImpl implements OrderService {

    @Override
    public void insertOrder(Order order) {
        System.out.println("insert order!!! "+ order.getOrderId());
    }

    @Override
    public Order findById(Long id) {
        return Order.builder().orderId(666L).createTime(new Date()).customName("daiwei").build();
    }
}
