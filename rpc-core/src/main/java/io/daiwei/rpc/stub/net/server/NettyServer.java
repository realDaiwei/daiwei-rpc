package io.daiwei.rpc.stub.net.server;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.net.codec.NettyDecoder;
import io.daiwei.rpc.stub.net.codec.NettyEncoder;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.stub.provider.invoke.RpcProviderProxyPool;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 真正的 netty Server
 * Created by Daiwei on 2021/4/13
 */
@Slf4j
public class NettyServer {

    private final int port;

    private final RpcSerializer serializer;

    private final ChannelGroup channels;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ChannelFuture channelFuture;

    private final AtomicInteger globalReqNums;

    public NettyServer(Integer port, RpcSerializer serializer) {
        this.port = port;
        this.serializer = serializer;
        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.globalReqNums = new AtomicInteger();
    }

    /**
     * 启动 netty server
     * @param invokerCore
     */
    public void run(ProviderInvokerCore invokerCore) {
        int core = Runtime.getRuntime().availableProcessors();
        this.bossGroup = new NioEventLoopGroup(core > 1 ? core / 2 : core);
        this.workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyDecoder(RpcRequest.class, serializer))
                                    .addLast(new NettyEncoder(RpcResponse.class, serializer))
                                    .addLast(new ServerHandler(invokerCore, channels, globalReqNums));
                        }
                    });
            channelFuture = serverBootstrap.bind(this.port).sync();
            log.info("daiwei-rpc server start successfully and listen for port "+ this.port);
            ServerHandler.serverHandlerOpen();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("thread of daiwei-rpc server is interrupted, perhaps rpc server stopped working.");
        } finally {
            close();
        }
    }

    /**
     * 关闭 netty Server
     * 如果当前server 中存在正在处理的请求，延迟 10 关闭
     */
    public void close() {
        ServerHandler.serverHandlerClose();
        if (this.globalReqNums.get() > 0) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.channels.close();
        ThreadPoolUtil.shutdownExistsPools();
        RpcProviderProxyPool.getInstance().cleanPool();
        if (bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
        log.info("[daiwei-rpc] netty server of rpc is offline");
    }

    public boolean isValid() {
        return this.channelFuture != null && this.channelFuture.channel().isActive();
    }


    @Override
    public String toString() {
        return "rpc netty server for port[" + this.port + "]";
    }

}
