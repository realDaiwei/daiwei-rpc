import io.daiwei.rpc.serializer.impl.HessianSerializer;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.net.SocketServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class Test {


    public static void main(String[] args) throws IOException {
//        HessianSerializer serializer = new HessianSerializer();
//        RpcRequest build = RpcRequest.builder().requestId(UUID.randomUUID().toString()).className("hello")
//                .methodName("methodName").params(new Object[]{}).params(new Object[]{}).build();
//        byte[] serialize = serializer.serialize(build);
//        RpcRequest deserialize = serializer.deserialize(serialize, RpcRequest.class);
//        System.out.println(deserialize.getRequestId());
        ServerSocket server = new ServerSocket(7248);
        server.accept();
        System.out.println(NetUtil.getIpAddress());
    }
}
