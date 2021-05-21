package io.daiwei.rpc.spring.stub;

import io.daiwei.rpc.stub.provider.annotation.RpcService;
import io.daiwei.rpc.stub.provider.boot.RpcServerBoot;
import io.daiwei.rpc.stub.provider.common.ServerInitRunnable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Daiwei on 2021/5/2
 */
public class RpcSpringServerBoot implements InitializingBean, DisposableBean {

    private String scanPackagePath;

    private String registerConn;

    private RpcServerBoot rpcServerBoot;

    @Override
    public void afterPropertiesSet() {
        RpcServerBoot.ServerBuilder builder = RpcServerBoot.builder().init(this.registerConn);
        for (Class<?> registerClazz : scanRpcAnnotatedClass(this.scanPackagePath)) {
            RpcService rpcService = registerClazz.getAnnotation(RpcService.class);
            for (Class<? extends ServerInitRunnable> clazz : rpcService.init()) {
                runInit(clazz);
            }
            builder.registerService(registerClazz, rpcService.version().isEmpty() ? null : rpcService.version());
        }
        this.rpcServerBoot = builder.build();
        this.rpcServerBoot.runAsync();
    }

    @Override
    public void destroy() throws Exception {
        this.rpcServerBoot.stop();
    }

    private List<Class<?>> scanRpcAnnotatedClass(String scanPackagePath) {
        Reflections reflection = new Reflections(new ConfigurationBuilder().forPackages(scanPackagePath));
        Set<Class<?>> clazz = reflection.getTypesAnnotatedWith(RpcService.class);
        return new ArrayList<>(clazz);
    }

    public void setRegisterConn(String registerConn) {
        this.registerConn = registerConn;
    }

    public void setScanPackagePath(String scanPackagePath) {
        this.scanPackagePath = scanPackagePath;
    }


    private void runInit(Class<? extends ServerInitRunnable> clazz) {
        try {
            clazz.newInstance().init();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
