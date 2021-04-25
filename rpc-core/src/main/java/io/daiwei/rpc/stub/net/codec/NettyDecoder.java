package io.daiwei.rpc.stub.net.codec;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by Daiwei on 2021/4/12
 */
@Slf4j
public class NettyDecoder extends ByteToMessageDecoder {

    private final Class<?> clazz;

    private final RpcSerializer serializer;

    public NettyDecoder(Class<?> clazz, RpcSerializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int len = in.readInt();
        if (len < 0) {
            ctx.close();
        }
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        try {
            Object msg = serializer.deserialize(bytes, clazz);
            out.add(msg);
        } catch (Exception e) {
            log.error("server catch exception!", e);
            e.printStackTrace();
        }
    }
}
