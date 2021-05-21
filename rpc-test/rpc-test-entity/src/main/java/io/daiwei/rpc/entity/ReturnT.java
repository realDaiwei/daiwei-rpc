package io.daiwei.rpc.entity;

import lombok.Data;

/**
 * Created by Daiwei on 2021/4/7
 */
@Data
public class ReturnT<T> {

    private int status;

    private String msg;

    private T data;

    public static <T> ReturnT<T> ok(T data) {
        ReturnT<T> res = new ReturnT<>();
        res.setMsg("ok");
        res.setData(data);
        res.setStatus(0);
        return res;
    }

    public static <T> ReturnT<T> ok() {
        return ok(null);
    }
}
