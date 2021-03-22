package io.daiwei.rpc.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Daiwei on 2021/3/22
 */
public class RpcExceptionBuilder {

    private RpcExceptionBuilder() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Builder() {}

        private Class<? extends RpcException> wrapper;

        private String msg;

        private Throwable real;

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder wrapper(Class<? extends RpcException> wrapper) {
            this.wrapper = wrapper;
            return this;
        }

        public Builder real(Throwable e) {
            this.real = e;
            return this;
        }

        public RpcException build() {
            if (null == this.real || null == this.wrapper || null == this.msg) {
                throw new RuntimeException("build failed because some args is empty!");
            }
            try {
                RpcException rpcException = this.wrapper.getConstructor(String.class).newInstance(this.msg);
                rpcException.set(this.real);
                return rpcException;
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("builder need a matched constructor(String.class) like super's");
            }
        }
    }
}
