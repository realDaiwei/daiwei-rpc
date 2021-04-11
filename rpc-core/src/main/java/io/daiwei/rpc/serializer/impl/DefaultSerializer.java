package io.daiwei.rpc.serializer.impl;

import io.daiwei.rpc.serializer.RpcSerializer;

/**
 * Created by Daiwei on 2021/4/11
 */

public class DefaultSerializer implements RpcSerializer {

    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
