package io.daiwei.rpc.pojo;

import io.daiwei.rpc.constant.RpcConstant;
import lombok.Data;

/**
 * Created by Daiwei on 2021/3/19
 */
@Data
public class RpcFxResp {

    private int code;

    private Object data;

    private Exception exception;

    public static RpcFxResp ok(Object data) {
        RpcFxResp resp = new RpcFxResp();
        resp.setData(data);
        resp.setCode(RpcConstant.SUCCESS);
        return resp;
    }

    public static RpcFxResp fail(Exception e) {
        RpcFxResp resp = new RpcFxResp();
        resp.setException(e);
        resp.setCode(RpcConstant.FAILED);
        return resp;
    }
}
