package io.daiwei.rpc.stub.net;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.File;

/**
 * 网络层通信 通用常量
 * Created by Daiwei on 2021/4/22
 */
public class NetConstant {

    private NetConstant() {}

    public static final String HEART_BEAT_REQ_ID = "HEART_BEAT_PING";

    public static final String HEART_BEAT_RESP_ID = "HEART_BEAT_PONG";

    public static final String IDLE_CHANNEL_CLOSE_REQ_ID = "idle_close_ask";

    public static final String IDLE_CHANNEL_CLOSE_RESP_ID = "idle_close_check";

    public static final String FILE_SEPARATOR = "/";
}
