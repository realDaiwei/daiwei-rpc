package io.daiwei.rpc.stub;

import com.alibaba.fastjson.parser.ParserConfig;
import io.daiwei.rpc.api.RpcInvokeHandler;

/**
 * Created by Daiwei on 2021/3/20
 */
public class RpcInvokerFactory {

    static {
        ParserConfig.getGlobalInstance().addAccept("io.daiwei");
    }

    private RpcInvokeHandler invokeHandler;

    public RpcInvokerFactory(RpcInvokeHandler handler) {
        this.invokeHandler = handler;
    }

    public <T> T createStub(Class<T> clazz) {
        return this.invokeHandler.create(clazz);
    }
}
