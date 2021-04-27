package io.daiwei.rpc.stub.net.server;

import io.daiwei.rpc.exception.ServerClosingException;
import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.HeartBeat;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.util.OSHealthUtil;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Daiwei on 2021/4/13
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static volatile boolean SERVER_STATUS;

    private final ProviderInvokerCore invokerCore;

    private final ChannelGroup channels;

    private volatile boolean channelClosing;

    private volatile long msgTimeout;

    private final Set<String> reqIds;

    private final AtomicInteger globalReqNums;

    public ServerHandler(ProviderInvokerCore invokerCore, ChannelGroup channels, AtomicInteger globalReqNums) {
        this.invokerCore = invokerCore;
        this.channels = channels;
        this.reqIds = new HashSet<>();
        this.msgTimeout = System.currentTimeMillis();
        this.globalReqNums = globalReqNums;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        ThreadPoolUtil.defaultRpcProviderExecutor().execute(() -> {
            try {
                if (invokerCore.valid(msg)) {
                    Channel channel = ctx.channel();
                    if (messagePreHandleFilter(channel, msg)) {
                        collectMessageData(channel, msg);
                        RpcResponse response = invokerCore.requestComingBellRing(msg);
                        channel.writeAndFlush(response);
                        reqIds.remove(msg.getRequestId());
                        globalReqNums.decrementAndGet();
                    }
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
    private RpcResponse replyHealthCheck(RpcRequest msg) {
        long latency = System.currentTimeMillis() - msg.getCreateTimeMillis();
        BigDecimal cpuLoad = OSHealthUtil.getCpuLoad();
        BigDecimal memLoad = OSHealthUtil.getMemLoad();
        return HeartBeat.healthResp(cpuLoad, memLoad, latency);
    }

    /**
     * 收集message 的一些信息用于简单的服务治理
     * @param channel
     * @param msg
     */
    private void collectMessageData(Channel channel, RpcRequest msg) {
        channelClosing = false;
        channels.add(channel);
        globalReqNums.getAndIncrement();
        long reqTimeout = msg.getTimeout() + msg.getCreateTimeMillis();
        if (msgTimeout < reqTimeout) {
            msgTimeout = reqTimeout;
        }
        reqIds.add(msg.getRequestId());
    }

    /**
     * message 在被正式处理前，经历处理过滤链
     *  过滤处理的请求包括 心跳，关闭channel，已经channel关闭过程中的请求
     * @param channel 本次请求的 channel
     * @param msg 请求过来的 message
     * @return 本次请求是否通过
     */
    private boolean messagePreHandleFilter(Channel channel, RpcRequest msg) {
        if (msg.getRequestId().startsWith(NetConstant.HEART_BEAT_REQ_ID)) {
            log.debug("[daiwei-rpc] heart beat request from {}", channel.remoteAddress());
            channel.writeAndFlush(replyHealthCheck(msg));
            return false;
        } else if (msg.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_REQ_ID)) {
            channelClosing = true;
            boolean approval = msgTimeout < System.currentTimeMillis() || reqIds.isEmpty();
            log.debug("[daiwei-rpc] idle-channel close asking form {} and server says [{}]", channel.remoteAddress(), approval ? "ok" : "nope");
            if (approval) {
                channel.writeAndFlush(HeartBeat.channelCloseRespSuccess());
                channels.find(channel.id()).close();
            } else {
                channel.writeAndFlush(HeartBeat.channelCloseRespFailed());
            }
            return false;
        } else if (channelClosing || !ServerHandler.SERVER_STATUS) {
            channel.writeAndFlush(RpcResponse.builder().code(-1).exception(new ServerClosingException()).build());
            return false;
        }
        return true;
    }

    public static void serverHandlerClose() {
        SERVER_STATUS = false;
    }

    public static void serverHandlerOpen() {
        SERVER_STATUS = true;
    }

}
