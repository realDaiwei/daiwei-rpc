package io.daiwei.rpc.stub.net.server;

import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.HeartBeat;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.util.OSHealthUtil;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Daiwei on 2021/4/13
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final ProviderInvokerCore invokerCore;

    private final ChannelGroup channels;

    private final Map<String, ChannelId> reqIdChannelIdMap;

    public ServerHandler(ProviderInvokerCore invokerCore, ChannelGroup channels, Map<String, ChannelId> map) {
        this.invokerCore = invokerCore;
        this.channels = channels;
        this.reqIdChannelIdMap = map;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        ThreadPoolUtil.defaultRpcProviderExecutor().execute(() -> {
            try {
                if (invokerCore.valid(msg)) {
                    if (msg.getRequestId().startsWith(NetConstant.HEART_BEAT_REQ_ID)) {
                        log.debug("heart beat request from {}", ctx.channel().remoteAddress());
                        RpcResponse response = replyHealthCheck(msg);
                        ctx.channel().writeAndFlush(response);
                        return;
                    }
                    Channel channel = ctx.channel();
                    channels.add(channel);
                    reqIdChannelIdMap.put(msg.getRequestId(), channel.id());
                    invokerCore.requestComingBellRing(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("daiwei-rpc server catch a exception, ctx is closing", cause);
        ctx.close();
    }

    /**
     * 回应健康检查请求
     * @return server 系统关键指标
     */
    public RpcResponse replyHealthCheck(RpcRequest msg) {
        long latency = System.currentTimeMillis() - msg.getCreateTimeMillis();
        BigDecimal cpuLoad = OSHealthUtil.getCpuLoad();
        BigDecimal memLoad = OSHealthUtil.getMemLoad();
        return HeartBeat.healthResp(cpuLoad, memLoad, latency);
    }

}
