package io.daiwei.rpc.stub.invoker.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Created by Daiwei on 2021/3/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcRef {

    long timeout() default 60 * 1000;

    int retryTimes() default 3;

    Class<?>[] retryExceptions() default {};

    double version() default 1.0;

}
