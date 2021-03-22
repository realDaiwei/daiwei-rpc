package io.daiwei.rpc.exception;

import io.daiwei.rpc.constant.RpcConstant;
import lombok.extern.log4j.Log4j;

/**
 * Created by Daiwei on 2021/3/22
 */
@Log4j
public abstract class RpcException extends RuntimeException {

    protected Throwable e;

    public RpcException(String msg) {
        super(msg);
    }

    public void throwWrapped() {
        throw this;
    }

    public void throwReal() throws Throwable {
        throw this.e;
    }

    public void printError() {
        log.error("[" + this.getClass().getName() + "::"  + this.getMessage() + "] ## real Exception [" + e.getClass().getName() + "::" + e.getMessage() +"]");
    }

    public void printStack() {
        this.e.printStackTrace();
    }

    public Throwable getReal() {
        return this.e;
    }

    protected void set(Throwable e) {
        this.e = e;
    }
}
