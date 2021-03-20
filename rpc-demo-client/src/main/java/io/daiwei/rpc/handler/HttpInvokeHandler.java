package io.daiwei.rpc.handler;

import com.alibaba.fastjson.JSON;
import io.daiwei.rpc.api.RpcCgLibInvokeHandler;
import io.daiwei.rpc.pojo.RpcFxReq;
import io.daiwei.rpc.pojo.RpcFxResp;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Daiwei on 2021/3/20
 */
public class HttpInvokeHandler implements RpcCgLibInvokeHandler {

    private Enhancer enhancer;

    private final OkHttpClient client;

    private Class<?> proxyClass;

    private final MediaType mediaType = MediaType.get("application/json; charset=utf-8");

    public HttpInvokeHandler() {
        client = new OkHttpClient();
        enhancer = new Enhancer();
    }

    @Override
    public <T> T create(Class<T> clazz) {
        enhancer.setCallback(this);
        enhancer.setSuperclass(clazz);
        this.proxyClass = clazz;
        return clazz.cast(enhancer.create());
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RpcFxReq rpcFxReq = new RpcFxReq(this.proxyClass.getName(), method.getName(), objects);
        RpcFxResp response = post(rpcFxReq);
        return JSON.parseObject(response.getData().toString(), method.getReturnType());
    }

    public RpcFxResp post(RpcFxReq rpcFxReq) throws IOException {
        String url = "http://127.0.0.1:8080";
        String reqJson = JSON.toJSONString(rpcFxReq);
        final Request request = new Request.Builder()
                .url(url).post(RequestBody.create(reqJson, mediaType))
                .build();
        String bodyStr = client.newCall(request).execute().body().string();
        return JSON.parseObject(bodyStr, RpcFxResp.class);
    }
}
