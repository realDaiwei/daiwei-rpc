package io.daiwei.rpc.stub.net.params;

import lombok.Data;

/**
 * Created by Daiwei on 2021/3/30
 */
@Data
public class RpcResponse  {

    private String requestId;

    private Object data;

    private String msg;

    private Exception exception;
}
