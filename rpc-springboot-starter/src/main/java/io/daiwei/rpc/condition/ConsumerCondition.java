package io.daiwei.rpc.condition;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Created by Daiwei on 2021/5/2
 */
public class ConsumerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String registerConn = environment.getProperty("spring.daiwei.rpc.provider.register-conn");
        return StringUtils.hasText(registerConn);
    }
}
