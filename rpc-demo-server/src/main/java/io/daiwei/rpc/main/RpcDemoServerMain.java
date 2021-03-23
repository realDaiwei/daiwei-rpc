package io.daiwei.rpc.main;

import io.daiwei.rpc.demo.service.impl.UserServiceImpl;
import io.daiwei.rpc.pojo.RpcFxReq;
import io.daiwei.rpc.pojo.RpcFxResp;
import io.daiwei.rpc.resolver.AnnotationFindResolver;
import io.daiwei.rpc.resolver.MapServiceResolver;
import io.daiwei.rpc.skeleton.RpcSkeletonServerStub;
import org.checkerframework.checker.units.qual.A;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Daiwei on 2021/3/21
 */
@RestController
@SpringBootApplication
public class RpcDemoServerMain {

    public static void main(String[] args) {
        SpringApplication.run(RpcDemoServerMain.class, args);
    }

    @Bean
    public RpcSkeletonServerStub stub() {
//        RpcSkeletonServerStub rpcServerStub = new RpcSkeletonServerStub(new MapServiceResolver());
//        rpcServerStub.register(new UserServiceImpl());
//        return rpcServerStub;
        AnnotationFindResolver findResolver = new AnnotationFindResolver("io.daiwei.rpc");
        RpcSkeletonServerStub serverStub = new RpcSkeletonServerStub(findResolver);
        findResolver.register();
        return serverStub;
    }

    @Resource
    private RpcSkeletonServerStub stub;

    @PostMapping("/")
    public RpcFxResp invoke(@RequestBody RpcFxReq request) {
        return stub.invoke(request);
    }
}
