package io.daiwei.rpc.stub.net.codec;

import io.daiwei.rpc.serializer.RpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Daiwei on 2021/4/12
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {

    private final Class<?> clazz;

    private final RpcSerializer serializer;

    public NettyEncoder(Class<?> clazz, RpcSerializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    // TODO: 2021/4/12 这里可以开发一个可拓展的协议
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (clazz.isInstance(msg)) {
            byte[] bytes = this.serializer.serialize(msg);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
