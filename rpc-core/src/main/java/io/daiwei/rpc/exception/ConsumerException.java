package io.daiwei.rpc.exception;

/**
 * 消费端异常
 * Created by Daiwei on 2021/3/22
 */
public class ConsumerException extends RpcException{

    public ConsumerException(String msg) {
        super(msg);
    }
}
