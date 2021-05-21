package io.daiwei.rpc.stub.net.params;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Daiwei on 2021/3/30
 */
@Data
@Builder
public class RpcResponse implements Serializable {

    private String requestId;

    private int code;

    private Object data;

    private String msg;

    private Exception exception;
}
