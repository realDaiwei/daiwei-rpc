package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    private final Map<String, RpcFutureResp> respPool;

    public ClientHandler(Map<String, RpcFutureResp> respPool) {
        this.respPool = respPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        RpcFutureResp resp = respPool.get(msg.getRequestId());
        if (resp == null) {
            throw new DaiweiRpcException("request is not sent from this client");
        }
        resp.RespBackBellRing(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("daiwei-rpc client catch a exception, ctx is closing");
        ctx.close();
    }

    // TODO: 2021/4/11 添加心跳包
}
