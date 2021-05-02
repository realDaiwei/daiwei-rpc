package io.daiwei.rpc.spring.stub;

import io.daiwei.rpc.stub.invoker.annotation.RpcRef;
import io.daiwei.rpc.stub.invoker.factory.RpcInvokerFactory;
import io.daiwei.rpc.stub.invoker.refbean.RpcRefBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Created by Daiwei on 2021/5/1
 */
@Slf4j
public class RpcInvokerSpringBean implements BeanPostProcessor, InitializingBean, DisposableBean {

    private RpcInvokerFactory rpcInvokerFactory;

    private String registerConn;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(RpcRef.class)) {
                RpcRef rpcRef = field.getAnnotation(RpcRef.class);
                RpcRefBean refBean = RpcRefBean.builder().retryTimes(rpcRef.retryTimes()).timeout(rpcRef.timeout())
                        .retryExceptions(Arrays.asList(rpcRef.retryExceptions()))
                        .targetFace(field.getType()).build();
                field.setAccessible(true);
                field.set(bean, rpcInvokerFactory.createStubByRefBean(refBean));
            }
        });
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void afterPropertiesSet() {
        if (!StringUtils.hasText(registerConn)) {
            log.error("register conn is empty!");
        }
        rpcInvokerFactory = new RpcInvokerFactory(this.registerConn);
    }

    public void setRegisterConn(String registerConn) {
        this.registerConn = registerConn;
    }

    @Override
    public void destroy() throws Exception {
        rpcInvokerFactory.stop();
    }
}
