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
import io.netty.channel.Channel;
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

    private static final Map<String, Integer> IDLE_HEART_BEAT_TIMES = new HashMap<>();

    private volatile boolean beatReturn;

    private final Lock lock = new ReentrantLock();

    private final Condition beatReturnCond = lock.newCondition();

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
            if (!messagePreHandleFilter(ctx.channel(), msg, serverAddr)) {
                return;
            }
            IDLE_HEART_BEAT_TIMES.put(serverAddr, 0);
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
            String serverAddr = ctx.channel().remoteAddress().toString().substring(1);
            log.debug("send heart beat request");
            if (!IDLE_HEART_BEAT_TIMES.containsKey(serverAddr) || IDLE_HEART_BEAT_TIMES.get(serverAddr) < 3) {
                IDLE_HEART_BEAT_TIMES.put(serverAddr, IDLE_HEART_BEAT_TIMES.get(serverAddr) + 1);
                ctx.channel().writeAndFlush(HeartBeat.healthReq());
            } else {
                clientServers.remove(serverAddr);
                this.subHealthUrls.remove(serverAddr);
                ctx.channel().writeAndFlush(HeartBeat.channelCloseReq());
            }
            ThreadPoolUtil.defaultRpcClientExecutor().execute(() -> heartBeatTimeoutCheck(serverAddr));
        }
        super.userEventTriggered(ctx, evt);
    }

    private void judgeHealth(SystemHealthInfo healthInfo, String serverAddr) {
        if (healthInfo.getRespSendTime() + 2000 > System.currentTimeMillis()) {
            // 超时心跳不处理
            return;
        }
        boolean health = healthInfo.getLatency() < 100 && healthInfo.getCpuLoadPercent().compareTo(new BigDecimal("0.70")) < 0
                && healthInfo.getMemLoadPercent().compareTo(new BigDecimal("0.75")) < 0;
        if (health) {
            subHealthUrls.remove(serverAddr);
        } else {
            if (!subHealthUrls.contains(serverAddr)) {
                subHealthUrls.add(serverAddr);
            }
        }
        log.debug("remote server[{}], health status[{}]", serverAddr, health);
    }

    private boolean messagePreHandleFilter(Channel channel, RpcResponse msg, String serverAddr) {
        if (msg.getRequestId().startsWith(NetConstant.HEART_BEAT_RESP_ID) && !beatReturn) {
            wakeBeatTimeoutChecker();
            judgeHealth((SystemHealthInfo) msg.getData(), serverAddr);
            return false;
        }
        if (msg.getRequestId().startsWith(NetConstant.IDLE_CHANNEL_CLOSE_RESP_ID)) {
            wakeBeatTimeoutChecker();
            if (msg.getCode() == 0) {
                channel.close();
                log.debug("idle-channel[{}] close!", serverAddr);
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
                subHealthUrls.add(serverAddress);
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
