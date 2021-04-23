package io.daiwei.rpc.stub.net.server;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.common.RpcSendable;
import io.daiwei.rpc.stub.net.codec.NettyDecoder;
import io.daiwei.rpc.stub.net.codec.NettyEncoder;
import io.daiwei.rpc.stub.net.common.ProviderInvokerCore;
import io.daiwei.rpc.stub.net.params.HeartBeat;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.stub.net.params.RpcResponse;
import io.daiwei.rpc.stub.provider.invoke.RpcProviderProxyPool;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 真正的 netty Server
 * Created by Daiwei on 2021/4/13
 */
@Slf4j
public class NettyServer implements RpcSendable {

    private final int port;

    private final RpcSerializer serializer;

    private final ChannelGroup channels;

    private final Map<String, ChannelId> reqChannelIdMap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ChannelFuture channelFuture;

    public NettyServer(Integer port, RpcSerializer serializer) {
        this.port = port;
        this.serializer = serializer;
        this.reqChannelIdMap = new ConcurrentHashMap<>();
        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
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
                                    .addLast(new ServerHandler(invokerCore, channels, reqChannelIdMap));
                        }
                    });
            channelFuture = serverBootstrap.bind(this.port).sync();
            log.info("daiwei-rpc server start successfully and listen for port "+ this.port);
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
     */
    public void close() {
        if (bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
        this.channels.close();
        ThreadPoolUtil.shutdownExistsPools();
        RpcProviderProxyPool.getInstance().cleanPool();
        log.debug("netty server of rpc is offline");
    }

    public boolean isValid() {
        return this.channelFuture != null && this.channelFuture.channel().isActive();
    }

    /**
     * 发送数据给客户端
     * @param obj
     */
    public void sendAsync(Object obj) {
        try {
            ThreadPoolUtil.defaultRpcProviderExecutor().execute(() ->{
                RpcResponse response = (RpcResponse) obj;
                channels.find(this.reqChannelIdMap.get(response.getRequestId())).writeAndFlush(response);
                reqChannelIdMap.remove(response.getRequestId());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "rpc netty server for port[" + this.port + "]";
    }

}
