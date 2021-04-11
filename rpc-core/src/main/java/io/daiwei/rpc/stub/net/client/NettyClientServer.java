package io.daiwei.rpc.stub.net.client;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.daiwei.rpc.stub.invoker.RpcInvokerFactory;
import io.daiwei.rpc.stub.net.common.ConnectServer;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.util.NetUtil;
import io.daiwei.rpc.util.ThreadPoolUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
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
    public void init(String address) {
        initEventLoop();
        String[] hostAndPort = NetUtil.getHostAndPort(address);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(NIO_EVENT_LOOP_GROUP).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // todo 添加可拓展协议 传入 serializer
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            this.channel = bootstrap.bind(hostAndPort[0], Integer.parseInt(hostAndPort[1])).sync().channel();
            if (!isValid()) {
                close();
                log.debug("client for addr[{}] is closed", address);
            }

            log.debug("daiwei-rpc clint connected {}:{} successfully" , hostAndPort[0], hostAndPort[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (this.isValid() && this.channel != null) {
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
    public void sendAsync(RpcRequest request) {
        ThreadPoolUtil.defaultRpcExecutor().execute(() -> {
            channel.writeAndFlush(request);
        });
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
