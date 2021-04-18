package io.daiwei.rpc.stub.provider.component;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.register.zk.ZkRpcRegister;
import io.daiwei.rpc.stub.provider.invoke.RpcProviderProxyPool;
import io.daiwei.rpc.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;

/**
 * Created by Daiwei on 2021/4/14
 */
@Slf4j
public class ProviderRegisterUnit extends ZkRpcRegister {

    public ProviderRegisterUnit(String zkConnStr) {
        this.zkConnStr = zkConnStr;
        this.init();
        this.start();
    }

    /**
     * 注册被调用的 proxy 到 proxyPool 中
     * @param clazz
     */
    @Override
    public void registerInvokeProxy(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        serverCheck(clazz);
        Class<?> clazzInterface = clazz.getInterfaces()[0];
        String clazzName = clazz.getCanonicalName() + "$$daiweiRpxProxyByByteBuddy";
        Object proxy = new ByteBuddy().subclass(clazz).name(clazzName)
                .method(ElementMatchers.any()).intercept(MethodCall.invokeSuper().withAllArguments())
                .make().load(ProviderRegisterUnit.class.getClassLoader()).getLoaded().newInstance();
        RpcProviderProxyPool.getInstance().addProxy(clazzInterface, proxy);
    }

    @Override
    public void registerService(int port, Class<?> clazz) {
        try {
            serverCheck(clazz);
            Class<?> service = clazz.getInterfaces()[0];
            if (null == client.checkExists().forPath("/" + service.getCanonicalName())) {
                client.create().withMode(CreateMode.PERSISTENT).forPath("/" + service.getCanonicalName(), "service".getBytes(StandardCharsets.UTF_8));
            }
            String addr = NetUtil.getIpAddress().concat(":").concat(String.valueOf(port));
            client.create().withMode(CreateMode.EPHEMERAL).forPath("/" + service.getCanonicalName() + "/" + addr, "provider".getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            log.error("create zk node failed.", exception);
            exception.printStackTrace();
            throw new DaiweiRpcException("create zk node failed.");
        }
    }

    private void serverCheck(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length == 0) {
            throw new DaiweiRpcException("not find interface of  " + clazz.getCanonicalName());
        }
        if (interfaces.length != 1) {
            throw new DaiweiRpcException("find more than one interfaces of " + clazz.getCanonicalName());
        }
    }
}
