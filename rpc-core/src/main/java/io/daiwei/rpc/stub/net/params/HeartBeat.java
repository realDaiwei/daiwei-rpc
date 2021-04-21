package io.daiwei.rpc.stub.net.params;

import java.math.BigDecimal;

/**
 * Created by Daiwei on 2021/4/22
 */
public final class HeartBeat {

    public static final int BEAT_INTERVAL = 30;

    private static final String HEART_BEAT_ID = "HEART_BEAT_PING";

    public static RpcRequest request;

    static {
        request = RpcRequest.builder().requestId(HEART_BEAT_ID).build();
    }

    public static RpcResponse HealthResp(BigDecimal cpuLoad, BigDecimal memLoad) {
        return RpcResponse.builder().code(0).msg("system health info")
                .data(new SystemHealthInfo(cpuLoad, memLoad)).build();
    }

}
