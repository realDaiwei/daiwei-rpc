package io.daiwei.rpc.stub.net.params;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Daiwei on 2021/3/30
 */
@Data
@Builder
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Object[] params;

    private Class<?> classType;

    private long createTimeMillis;

    private String accessToken;

}
