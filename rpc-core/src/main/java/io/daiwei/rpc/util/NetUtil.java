package io.daiwei.rpc.util;

import io.daiwei.rpc.exception.DaiweiRpcException;

/**
 * Created by Daiwei on 2021/4/11
 */
public class NetUtil {

    private NetUtil() {}

    public static String[] getHostAndPort(String addr) {
        String[] res = new String[2];
        if (addr.contains(":")) {
            String[] split = addr.split(":");
            res[0] = split[0].trim();
            res[1] = split[1].trim();
            return res;
        } else {
            throw new DaiweiRpcException("addr[ "+ addr +" ] is illegal！");
        }
    }
}
