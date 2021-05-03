package io.daiwei.rpc.stub.provider.annotation;

import io.daiwei.rpc.stub.provider.common.ServerInitRunnable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Daiwei on 2021/4/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

    Class<? extends ServerInitRunnable>[] init() default {};

    String version() default "";

}
