package io.daiwei.rpc.server;

import io.daiwei.rpc.anntataion.RpcService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daiwei on 2021/3/24
 */
public class RpcNettyInitializeHandler extends ChannelInitializer<SocketChannel> {

    private final static Map<String, Object> serviceMap = new HashMap<>();

    public RpcNettyInitializeHandler() {
        if (serviceMap.size() == 0) {
            registerService();
        }
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(1024 * 1024))
                .addLast(new RpcProviderHandler(serviceMap));
    }

    private void registerService() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> clazz : classSet) {
            try {
                Object o = clazz.newInstance();
                serviceMap.put(clazz.getInterfaces()[0].getName(), o);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
