package io.daiwei.rpc.server;

import com.alibaba.fastjson.JSON;
import io.daiwei.rpc.demo.pojo.User;
import io.daiwei.rpc.pojo.RpcFxReq;
import io.daiwei.rpc.pojo.RpcFxResp;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Daiwei on 2021/3/24
 */
public class RpcProviderHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> serviceMap;

    public RpcProviderHandler(Map<String, Object> map) {
        this.serviceMap = map;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        System.out.println(request.content().toString(StandardCharsets.UTF_8));
        RpcFxReq rpcFxReq = JSON.parseObject(request.content().toString(StandardCharsets.UTF_8), RpcFxReq.class);
        Object o = serviceMap.get(rpcFxReq.getServiceClass());
        Method method = Arrays.stream(o.getClass().getMethods()).filter(m -> m.getName().equals(rpcFxReq.getMethod()))
                .findFirst().orElse(null);
        RpcFxResp resp = method != null ? RpcFxResp.ok(method.invoke(o, rpcFxReq.getArgs())) : RpcFxResp.fail(new NullPointerException("not find method["
                + rpcFxReq.getServiceClass() + "#" + rpcFxReq.getMethod()+ "]"));
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(JSON.toJSONString(resp).getBytes(StandardCharsets.UTF_8)));
        response.headers().set("Content-Type", "application/json");
        response.headers().setInt("Content-Length", response.content().readableBytes());
        ctx.writeAndFlush(response);
    }
}
