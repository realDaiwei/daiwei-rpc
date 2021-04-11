package io.daiwei.rpc.stub.invoker;

import io.daiwei.rpc.stub.invoker.component.InvokerUnit;
import io.daiwei.rpc.stub.invoker.component.RegisterUnit;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daiwei on 2021/3/28
 */
public class RpcInvokerFactory {

    private InvokerUnit invokerUnit;

    private RegisterUnit registerUnit;

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
        // 启动时候调用
    }

    public void stop() {
        try {
            invokerUnit.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init(InvokerUnit invokerUnit, RegisterUnit registerUnit) {
        this.invokerUnit = invokerUnit;
        this.registerUnit = registerUnit;
    }
}
