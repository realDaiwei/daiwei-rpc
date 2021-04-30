import io.daiwei.rpc.serializer.impl.HessianSerializer;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import io.daiwei.rpc.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.net.SocketServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class Test {



    public static void main(String[] args) throws IOException, InterruptedException {
//        HessianSerializer serializer = new HessianSerializer();
//        RpcRequest build = RpcRequest.builder().requestId(UUID.randomUUID().toString()).className("hello")
//                .methodName("methodName").params(new Object[]{}).params(new Object[]{}).build();
//        byte[] serialize = serializer.serialize(build);
//        RpcRequest deserialize = serializer.deserialize(serialize, RpcRequest.class);
//        System.out.println(deserialize.getRequestId());
//        Random rnd = new Random();
//
//        int[] res = new int[2];
//        for (int i = 0; i < 1000000000; i++) {
//            int nextInt = rnd.nextInt(2);
//            res[nextInt]++;
//        }
//        System.out.println(Arrays.toString(res));
//        System.out.println(res[0] - res[1]);
        long start = System.currentTimeMillis();
        Thread.sleep(10 * 1000);
        System.out.println(System.currentTimeMillis() - start);
    }
}
