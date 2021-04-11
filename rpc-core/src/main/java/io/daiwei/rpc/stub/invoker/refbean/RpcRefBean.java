package io.daiwei.rpc.stub.invoker.refbean;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.invoker.RpcInvokerFactory;
import io.daiwei.rpc.stub.invoker.component.InvokerUnit;
import lombok.Data;
import io.daiwei.rpc.stub.net.Client;

import java.util.List;

/**
 * 本地代理实例info 聚合对象
 * Created by Daiwei on 2021/3/28
 */
@Data
public class RpcRefBean {

    private Client clientInstance;

    private Class<?> targetFace;

    private List<String> availAddr;

    private String accessToken;

    private long timeout = 60;

    private String version;

    /**
     * 初始化 确定调用的目标客户端
     * 初始化 序列化器
     */
    public void init(InvokerUnit invokerUnit) {

    }
}
