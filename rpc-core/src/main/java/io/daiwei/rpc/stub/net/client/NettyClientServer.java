package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.codec.NettyDecoder;
import io.daiwei.rpc.stub.net.codec.NettyEncoder;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.HeartBeat;
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Daiwei on 2021/4/10
 */
@Slf4j
public class NettyClientServer extends ConnectServer {

    private static volatile NioEventLoopGroup nioEventLoopGroup;

    private final RpcSerializer serializer;

    public NettyClientServer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void init(String address, ClientHandler clientHandler) {
        initEventLoop();
        String[] hostAndPort = NetUtil.getHostAndPort(address);
        this.host = hostAndPort[0];
        this.port = Integer.parseInt(hostAndPort[1]);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, HeartBeat.BEAT_INTERVAL, TimeUnit.SECONDS))
                                    .addLast(new NettyEncoder(RpcRequest.class, serializer))
                                    .addLast(new NettyDecoder(RpcResponse.class, serializer))
                                    .addLast(clientHandler);
                        }
                    });
            this.channel = bootstrap.connect(this.host, this.port).sync().channel();
            if (!isValid()) {
                close();
                log.debug("client for addr[{}] is closed", address);
            }
            log.debug("daiwei-rpc client connect {}:{} successfully" , this.host, this.port);
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
            try {
                channel.writeAndFlush(request);
            } catch (Exception e) {
                throw e;
            }
        });

    }

    @Override
    public String toString() {
        return "netty client for " + host + ":" + port;
    }

    @Override
    public void cleanStaticResource() {
        if (nioEventLoopGroup != null && !nioEventLoopGroup.isTerminated()) {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    private static void initEventLoop() {
        if (nioEventLoopGroup == null) {
            synchronized (NettyClientServer.class) {
                if (nioEventLoopGroup == null) {
                    nioEventLoopGroup = new NioEventLoopGroup();
                }
            }
        }
    }

}
