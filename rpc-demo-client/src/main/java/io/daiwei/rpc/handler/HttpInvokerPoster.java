package io.daiwei.rpc.handler;

import com.alibaba.fastjson.JSON;
import io.daiwei.rpc.pojo.RpcFxReq;
import io.daiwei.rpc.pojo.RpcFxResp;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;

/**
 * Created by Daiwei on 2021/3/22
 */
public class HttpInvokerPoster {

    private static final OkHttpClient client = new OkHttpClient();

    private static final MediaType mediaType = MediaType.get("application/json; charset=utf-8");

    public static RpcFxResp post(RpcFxReq rpcFxReq) throws IOException {
        String url = "http://127.0.0.1:8080";
        String reqJson = JSON.toJSONString(rpcFxReq);
        final Request request = new Request.Builder()
                .url(url).post(RequestBody.create(reqJson, mediaType))
                .build();
        String bodyStr = client.newCall(request).execute().body().string();
        return JSON.parseObject(bodyStr, RpcFxResp.class);
    }
}
