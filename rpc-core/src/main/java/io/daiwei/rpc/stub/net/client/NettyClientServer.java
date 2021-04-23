package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.codec.NettyDecoder;
import io.daiwei.rpc.stub.net.codec.NettyEncoder;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.HeartBeat;
import io.daiwei.rpc.stub.net.params.RpcFutureResp;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.util.NetUtil;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Daiwei on 2021/4/10
 */
@Slf4j
public class NettyClientServer extends ConnectServer {

    private volatile static NioEventLoopGroup NIO_EVENT_LOOP_GROUP;

    private final Lock lock = new ReentrantLock();

    private final RpcSerializer serializer;

    public NettyClientServer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void init(String address, Map<String, RpcFutureResp> respPool, List<String> healthUrls, List<String> subHealthUrls) {
        initEventLoop();
        String[] hostAndPort = NetUtil.getHostAndPort(address);
        this.host = hostAndPort[0];
        this.port = Integer.parseInt(hostAndPort[1]);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(NIO_EVENT_LOOP_GROUP).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, HeartBeat.BEAT_INTERVAL, TimeUnit.SECONDS))
                                    .addLast(new NettyEncoder(RpcRequest.class, serializer))
                                    .addLast(new NettyDecoder(RpcResponse.class, serializer))
                                    .addLast(new ClientHandler(respPool, healthUrls, subHealthUrls));
                        }
                    });
            this.channel = bootstrap.connect(this.host, this.port).sync().channel();
            if (!isValid()) {
                close();
                log.debug("client for addr[{}] is closed", address);
            }
            log.debug("daiwei-rpc clint connect {}:{} successfully" , this.host, this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (this.channel != null) {
            this.channel.close();
        }
    }

    @Override
    public boolean isValid() {
        return channel != null && channel.isActive();
    }

    @Override
    public void send(RpcRequest request) {
        channel.writeAndFlush(request);
    }

    @Override
    public void sendAsync(Object request) {
        ThreadPoolUtil.defaultRpcClientExecutor().execute(() -> {
            channel.writeAndFlush(request);
        });
    }

    @Override
    public String toString() {
        return "netty client for " + host + ":" + port;
    }

    @Override
    public void cleanStaticResource() {
        if (NIO_EVENT_LOOP_GROUP != null && !NIO_EVENT_LOOP_GROUP.isTerminated()) {
            NIO_EVENT_LOOP_GROUP.shutdownGracefully();
        }
    }

    private void initEventLoop() {
        if (NIO_EVENT_LOOP_GROUP == null) {
            try {
                lock.lock();
                if (NIO_EVENT_LOOP_GROUP == null) {
                    NIO_EVENT_LOOP_GROUP = new NioEventLoopGroup();
                }
            } finally {
                lock.unlock();
            }
        }
    }

}
