package io.daiwei.rpc.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Daiwei on 2021/3/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcFxReq {

    private String serviceClass;

    private String method;

    private Object[] args;

}
