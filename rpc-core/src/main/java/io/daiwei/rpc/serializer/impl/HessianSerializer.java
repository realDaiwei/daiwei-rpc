package io.daiwei.rpc.serializer.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.daiwei.rpc.serializer.RpcSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化
 * Created by Daiwei on 2021/4/12
 */
public class HessianSerializer implements RpcSerializer {

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(outputStream);
        try {
            output.writeObject(obj);
            output.flushBuffer();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(inputStream);
        try {
            Object obj = hessian2Input.readObject();
            return clazz.cast(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
