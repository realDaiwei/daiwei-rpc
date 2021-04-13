package io.daiwei.rpc.stub.invoker.factory;

import io.daiwei.rpc.serializer.impl.DefaultSerializer;
import io.daiwei.rpc.serializer.impl.HessianSerializer;
import io.daiwei.rpc.stub.invoker.component.InvokerUnit;
import io.daiwei.rpc.stub.invoker.component.RegisterUnit;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import io.daiwei.rpc.stub.net.client.NettyInvokerClient;
import io.daiwei.rpc.util.ThreadPoolUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Created by Daiwei on 2021/3/28
 */
public class RpcInvokerFactory {

    private InvokerUnit invokerUnit;

    private RegisterUnit registerUnit;

    public RpcInvokerFactory() {
        start();
    }

    /**
     * 创建调用的 stub
     * @param rpcRefBean
     * @return
     */
    public Object createStub(RpcRefBean rpcRefBean) {
        rpcRefBean.init(this.invokerUnit);
        // byte-buddy 创建代理调用桩
        Object stub = null;
        try {
            stub = new ByteBuddy().subclass(rpcRefBean.getTargetFace()).method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new DelegateInvokerMethod(rpcRefBean.getClientInstance())))
                    .make().load(this.getClass().getClassLoader()).getLoaded().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return stub;
    }

    public void start() {
        this.invokerUnit = new InvokerUnit(HessianSerializer.class, NettyInvokerClient.class);
        this.registerUnit = null;
        try {
            this.invokerUnit.afterSetProperties();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void stop() {
        try {
            invokerUnit.stop();
            ThreadPoolUtil.shutdownExistsPools();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
