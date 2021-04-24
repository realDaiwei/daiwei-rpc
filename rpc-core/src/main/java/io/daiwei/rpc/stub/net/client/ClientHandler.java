package io.daiwei.rpc.stub.net.client;

import com.sun.xml.internal.bind.v2.model.core.ID;
import io.daiwei.rpc.exception.DaiweiRpcException;
import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.HeartBeat;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.stub.net.params.SystemHealthInfo;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.data.Id;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private final Map<String, RpcFutureResp> respPool;

    private final List<String> subHealthUrls;

    private final Map<String, ConnectServer> clientServers;

    public ClientHandler(Map<String, RpcFutureResp> respPool, List<String> subHealthUrls, Map<String, ConnectServer> clientServers) {
        this.respPool = respPool;
        this.subHealthUrls = subHealthUrls;
        this.clientServers = clientServers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        ThreadPoolUtil.defaultRpcClientExecutor().execute(() -> {
            String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
            if (msg.getRequestId().startsWith(NetConstant.HEART_BEAT_RESP_ID)) {
                judgeHealth((SystemHealthInfo) msg.getData(), serverAddr);
                return;
            }
            // 极端情况下 如果 channel 关闭的时间 小于调用超时时间 * 重试次数，
            // 这将导致方法还没正常返回，channel 已经关闭了
//            if (msg.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID)) {
//                if (msg.getCode() == 0) {
//                    log.debug("channel[{}] close!", serverAddr);
//                    ctx.close();
//                }
//                return;
//            }
            RpcFutureResp resp = respPool.get(msg.getRequestId());
            if (resp != null) {
                resp.RespBackBellRing(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("daiwei-rpc client catch a exception, ctx is closing", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.debug("send heart beat request");
            ctx.channel().writeAndFlush(HeartBeat.healthReq());
        }
        super.userEventTriggered(ctx, evt);
    }

    private void judgeHealth(SystemHealthInfo healthInfo, String serverAddr) {
        boolean health = healthInfo.getLatency() < 50 && healthInfo.getCpuLoadPercent().compareTo(new BigDecimal("0.70")) < 0
                && healthInfo.getMemLoadPercent().compareTo(new BigDecimal("0.75")) < 0;
        if (health) {
            subHealthUrls.remove(serverAddr);
        } else {
            if (!subHealthUrls.contains(serverAddr)) {
                subHealthUrls.add(serverAddr);
            }
        }
    }


}
