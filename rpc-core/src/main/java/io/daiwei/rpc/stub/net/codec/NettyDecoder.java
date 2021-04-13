package io.daiwei.rpc.stub.net.codec;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by Daiwei on 2021/4/12
 */
public class NettyDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    private RpcSerializer serializer;

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
        serializer.deserialize(bytes, clazz);
    }
}
