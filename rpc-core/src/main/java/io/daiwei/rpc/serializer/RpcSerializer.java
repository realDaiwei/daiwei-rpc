package io.daiwei.rpc.serializer;

/**
 * Created by Daiwei on 2021/3/28
 */
public interface RpcSerializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
