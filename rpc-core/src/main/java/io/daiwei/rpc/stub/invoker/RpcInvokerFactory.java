package io.daiwei.rpc.stub.invoker;

import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daiwei on 2021/3/28
 */
public class RpcInvokerFactory {


    private final ConcurrentHashMap<Class<?>, Object> stubs = new ConcurrentHashMap();




    /**
     * 创建调用的 stub
     * @param rpcRefBean
     * @return
     */
    public Object createStub(RpcRefBean rpcRefBean) {
        rpcRefBean.init();
        if (stubs.contains(rpcRefBean.getTargetFace())) {
            return stubs.get(rpcRefBean.getTargetFace());
        }
        // byte-buddy 创建代理调用桩
        Object stub = null;
        try {
            stub = new ByteBuddy().subclass(rpcRefBean.getTargetFace()).method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new DelegateInvokerMethod(rpcRefBean.getSerializerInstance(), rpcRefBean.getClientInstance())))
                    .make().load(this.getClass().getClassLoader()).getLoaded().newInstance();
            stubs.put(rpcRefBean.getTargetFace(), stub);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return stub;
    }

    public void start() {
        // 启动时候调用
    }

    public void stop() {
        // 停机时候时调用
    }

    private void initBean() {

    }
}
