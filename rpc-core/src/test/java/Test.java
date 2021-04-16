import io.daiwei.rpc.serializer.impl.HessianSerializer;
import io.daiwei.rpc.stub.net.params.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Created by Daiwei on 2021/4/11
 */
@Slf4j
public class Test {


    public static void main(String[] args) {
        HessianSerializer serializer = new HessianSerializer();
        RpcRequest build = RpcRequest.builder().requestId(UUID.randomUUID().toString()).className("hello")
                .methodName("methodName").params(new Object[]{}).params(new Object[]{}).build();
        byte[] serialize = serializer.serialize(build);
        RpcRequest deserialize = serializer.deserialize(serialize, RpcRequest.class);
        System.out.println(deserialize.getRequestId());
    }
}
