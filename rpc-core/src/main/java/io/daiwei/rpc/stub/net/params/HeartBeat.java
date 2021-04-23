package io.daiwei.rpc.stub.net.params;

import io.daiwei.rpc.stub.net.NetConstant;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Daiwei on 2021/4/22
 */
public final class HeartBeat {

    public static final int BEAT_INTERVAL = 3;

    public static RpcRequest healthReq() {
        return RpcRequest.builder().requestId(NetConstant.HEART_BEAT_REQ_ID).createTimeMillis(System.currentTimeMillis())
                .build();
    }

    public static RpcResponse healthResp(BigDecimal cpuLoad, BigDecimal memLoad, long latency) {
        return RpcResponse.builder().code(0).requestId(NetConstant.HEART_BEAT_RESP_ID)
                .data(new SystemHealthInfo(latency, cpuLoad, memLoad))
                .build();
    }

    public static RpcRequest channelCloseReq() {
        return RpcRequest.builder().requestId(NetConstant.IDLE_CHANNEL_CLOSE_REQ_ID)
                .createTimeMillis(System.currentTimeMillis()).build();
    }

    public static RpcResponse channelCloseResp() {
        return RpcResponse.builder().code(0)
                .requestId(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID).build();
    }

}
