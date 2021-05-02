package io.daiwei.rpc.service;

import io.daiwei.rpc.entity.Order;

/**
 * Created by Daiwei on 2021/5/2
 */
public interface OrderService {

    void insertOrder(Order order);

    Order findById(Long id);
}
