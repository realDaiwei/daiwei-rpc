package io.daiwei.rpc.serializer;

import java.io.IOException;

/**
 * Created by Daiwei on 2021/3/28
 */
public interface RpcSerializer {

    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
