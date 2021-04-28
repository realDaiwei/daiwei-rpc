package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.stub.net.NetConstant;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.HeartBeat;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.stub.net.params.SystemHealthInfo;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private volatile boolean beatReturn;

    private final AtomicInteger idleHeartBeatTimes;

    private final Lock lock = new ReentrantLock();

    private final Condition beatReturnCond = lock.newCondition();

    private final Map<String, RpcFutureResp> respPool;

    private final Map<String, ConnectServer> clientServers;

    private final HealthAvailableAnalyzer availableAnalyzer;

    public ClientHandler(Map<String, RpcFutureResp> respPool,  Map<String, ConnectServer> clientServers, HealthAvailableAnalyzer availableAnalyzer) {
        this.respPool = respPool;
        this.clientServers = clientServers;
        this.idleHeartBeatTimes = new AtomicInteger();
        this.availableAnalyzer = availableAnalyzer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        ThreadPoolUtil.defaultRpcClientExecutor().execute(() -> {
            String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
            if (!messagePreHandleFilter(ctx.channel(), msg, serverAddr)) {
                return;
            }
            idleHeartBeatTimes.set(0);
            RpcFutureResp resp = respPool.get(msg.getRequestId());
            if (resp != null) {
                resp.RespBackBellRing(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("daiwei-rpc client catch a exception, ctx is closing", cause);
        String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
        this.clientServers.remove(serverAddr);
        this.availableAnalyzer.removeUrl(serverAddr);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
            if (idleHeartBeatTimes.getAndIncrement() < 10) {
                log.debug("[daiwei-rpc] send heart beat request");
                ctx.channel().writeAndFlush(HeartBeat.healthReq());
            } else {
                log.debug("[daiwei-rpc] send idle channel close request");
                if (idleHeartBeatTimes.get() == 10) {
                    this.clientServers.remove(serverAddr);
                    this.availableAnalyzer.removeUrl(serverAddr);
                }
                ctx.channel().writeAndFlush(HeartBeat.channelCloseReq());
            }
            ThreadPoolUtil.defaultRpcClientExecutor().execute(() -> heartBeatTimeoutCheck(serverAddr));
        }
        super.userEventTriggered(ctx, evt);
    }

    private boolean messagePreHandleFilter(Channel channel, RpcResponse msg, String serverAddr) {
        if (msg.getRequestId().startsWith(NetConstant.HEART_BEAT_RESP_ID) && !beatReturn) {
            wakeBeatTimeoutChecker();
            availableAnalyzer.analyzeHeartBeatRes((SystemHealthInfo) msg.getData(), serverAddr);
            return false;
        }
        if (msg.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID)) {
            wakeBeatTimeoutChecker();
            if (msg.getCode() == 0) {
                channel.close();
                log.debug("[daiwei-rpc] idle-channel[{}] close!", serverAddr);
            }
            return false;
        }
        return true;
    }

    /**
     * 心跳超时检测
     * @param serverAddress 目标服务地址
     * @throws InterruptedException
     */
    private void heartBeatTimeoutCheck(String serverAddress) {
        beatReturn = false;
        try {
            lock.lock();
            while (!beatReturn) {
                boolean await = this.beatReturnCond.await(2, TimeUnit.SECONDS);
                if (!await) {
                    break;
                }
            }
            // 超时处理
            if (!beatReturn) {
                beatReturn = true;
                availableAnalyzer.heartBeatTimeout(serverAddress);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void wakeBeatTimeoutChecker() {
        beatReturn = true;
        try {
            lock.lock();
            this.beatReturnCond.signalAll();
        } finally {
            lock.unlock();
        }
    }


}
