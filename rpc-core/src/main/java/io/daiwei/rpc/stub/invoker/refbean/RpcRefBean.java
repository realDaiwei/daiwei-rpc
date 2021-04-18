package io.daiwei.rpc.stub.invoker.refbean;

import io.daiwei.rpc.stub.invoker.component.InvokerRegisterUnit;
import io.daiwei.rpc.stub.invoker.component.InvokerUnit;
import lombok.Builder;
import lombok.Data;
import io.daiwei.rpc.stub.net.Client;

import java.util.List;

/**
 * 本地代理实例info 聚合对象
 * Created by Daiwei on 2021/3/28
 */
@Data
@Builder
public class RpcRefBean {

    private Class<?> targetFace;

    private List<String> availUrls;

    private String accessToken;

    private long timeout = 60;

    private String version;

}