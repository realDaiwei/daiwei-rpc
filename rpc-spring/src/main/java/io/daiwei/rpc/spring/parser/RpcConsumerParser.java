package io.daiwei.rpc.spring.parser;

import io.daiwei.rpc.spring.stub.RpcInvokerSpringBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by Daiwei on 2021/5/2
 */
public class RpcConsumerParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return RpcInvokerSpringBean.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String attrValue = element.getAttribute("register-conn");
        if (StringUtils.hasText(attrValue)) {
            builder.addPropertyValue("registerConn", attrValue);
        }

    }
}
