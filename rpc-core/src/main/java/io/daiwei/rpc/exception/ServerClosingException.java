package io.daiwei.rpc.exception;

/**
 * Created by Daiwei on 2021/4/26
 */
public class ServerClosingException extends DaiweiRpcException {

    public ServerClosingException() {
        super("channel is closing");
    }
}
