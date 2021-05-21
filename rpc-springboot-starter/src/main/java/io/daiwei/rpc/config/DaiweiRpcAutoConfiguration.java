package io.daiwei.rpc.config;

import io.daiwei.rpc.condition.ConsumerCondition;
import io.daiwei.rpc.condition.ProviderCondition;
import io.daiwei.rpc.props.DaiweiRpcConsumerProps;
import io.daiwei.rpc.props.DaiweiRpcProviderProps;
import io.daiwei.rpc.spring.stub.RpcInvokerSpringBean;
import io.daiwei.rpc.spring.stub.RpcSpringServerBoot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Created by Daiwei on 2021/5/2
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.daiwei.rpc", name = "enable", havingValue = "true", matchIfMissing = true)
public class DaiweiRpcAutoConfiguration {

    @Resource
    private DaiweiRpcConsumerProps consumerProps;

    @Resource
    private DaiweiRpcProviderProps providerProps;

    @Bean
    @Conditional(ConsumerCondition.class)
    public RpcInvokerSpringBean invokerFactory() {
        RpcInvokerSpringBean bean = new RpcInvokerSpringBean();
        bean.setRegisterConn(consumerProps.getRegisterConn());
        return bean;
    }

    @Bean
    @Conditional(ProviderCondition.class)
    public RpcSpringServerBoot rpcServerBoot() {
        RpcSpringServerBoot bean = new RpcSpringServerBoot();
        bean.setRegisterConn(providerProps.getRegisterConn());
        bean.setScanPackagePath(providerProps.getScanPackagePath());
        return bean;
    }

}
