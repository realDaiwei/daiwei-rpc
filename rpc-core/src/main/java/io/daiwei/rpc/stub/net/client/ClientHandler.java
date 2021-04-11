package io.daiwei.rpc.stub.net.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // TODO: 2021/4/11   provider 返回的
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("daiwei-rpc client catch a exception, ctx is closing");
        ctx.close();
    }

    // TODO: 2021/4/11 添加心跳包
}
