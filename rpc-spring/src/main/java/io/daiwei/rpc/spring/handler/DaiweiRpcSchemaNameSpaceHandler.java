package io.daiwei.rpc.spring.handler;

import io.daiwei.rpc.spring.parser.RpcConsumerParser;
import io.daiwei.rpc.spring.parser.RpcProviderParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by Daiwei on 2021/5/2
 */
public class DaiweiRpcSchemaNameSpaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("provider", new RpcProviderParser());
        registerBeanDefinitionParser("consumer", new RpcConsumerParser());
    }
}
