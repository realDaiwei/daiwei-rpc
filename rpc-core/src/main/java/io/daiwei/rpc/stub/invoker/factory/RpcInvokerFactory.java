package io.daiwei.rpc.stub.invoker.factory;

import io.daiwei.rpc.router.DefaultLoadBalance;
import io.daiwei.rpc.router.common.LoadBalance;
import io.daiwei.rpc.serializer.impl.HessianSerializer;
import io.daiwei.rpc.stub.invoker.component.InvokerUnit;
import io.daiwei.rpc.stub.invoker.component.InvokerRegisterUnit;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import io.daiwei.rpc.stub.net.client.NettyInvokerClient;
import io.daiwei.rpc.util.ThreadPoolUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.List;

/**
 * Created by Daiwei on 2021/3/28
 */
public class RpcInvokerFactory {

    private InvokerUnit invokerUnit;

    private InvokerRegisterUnit registerUnit;

    private LoadBalance loadBalance;

    public RpcInvokerFactory(String zkConnStr) {
        start(zkConnStr);
    }

    /**
     * 创建调用的 stub
     * @param rpcRefBean
     * @return
     */
    public Object createStub(RpcRefBean rpcRefBean) {
        Object stub = null;
        try {
            stub = new ByteBuddy().subclass(rpcRefBean.getTargetFace()).method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(new DelegateInvokerMethod(rpcRefBean, loadBalance, invokerUnit)))
                    .make().load(this.getClass().getClassLoader()).getLoaded().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return stub;
    }

    /**
     * 直接通过Class类型创建调用对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createStubByClass(Class<T> clazz) {
        List<String> strings = registerUnit.pullAvailableUrls(clazz);
        RpcRefBean refBean = RpcRefBean.builder().targetFace(clazz).availUrls(strings).version("1.0")
                .accessToken("").timeout(60).build();
        Object stub = createStub(refBean);
        return clazz.cast(stub);
    }

    public void start(String zkConnStr) {
        this.invokerUnit = new InvokerUnit(HessianSerializer.class, NettyInvokerClient.class);
        this.registerUnit = new InvokerRegisterUnit(zkConnStr);
        this.loadBalance = new DefaultLoadBalance();
        try {
            this.invokerUnit.afterSetProperties();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void stop() {
        try {
            invokerUnit.stop();
            registerUnit.stop();
            ThreadPoolUtil.shutdownExistsPools();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> route(List<String> urls) {
        return urls;
    }
}
