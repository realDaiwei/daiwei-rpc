package io.daiwei.rpc.stub;

import com.alibaba.fastjson.parser.ParserConfig;
import io.daiwei.rpc.api.RpcCgLibInvokeHandler;

/**
 * Created by Daiwei on 2021/3/20
 */
public class RpcInvokerFactory {

    static {
        ParserConfig.getGlobalInstance().addAccept("io.daiwei");
    }

    private RpcCgLibInvokeHandler rpcInvokeHandler;

    public RpcInvokerFactory(RpcCgLibInvokeHandler handler) {
        this.rpcInvokeHandler = handler;
    }

    public <T> T createStub(Class<T> clazz) {
        return this.rpcInvokeHandler.create(clazz);
    }
}
