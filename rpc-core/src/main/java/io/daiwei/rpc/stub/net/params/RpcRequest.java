package io.daiwei.rpc.stub.net.params;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Daiwei on 2021/3/30
 */
@Data
@Builder
public class RpcRequest implements Serializable {

    private String requestId;

    private String className;

    private String methodName;

    private Object[] params;

    private Class<?> classType;

    private long createTimeMillis;

    private String accessToken;

    private long timeout;

}
