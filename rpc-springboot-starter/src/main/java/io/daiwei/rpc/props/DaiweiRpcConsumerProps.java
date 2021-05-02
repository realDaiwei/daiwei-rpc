package io.daiwei.rpc.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Daiwei on 2021/5/2
 */
@Configuration
@ConfigurationProperties(prefix = "spring.daiwei.rpc.consumer")
public class DaiweiRpcConsumerProps {

    private String RegisterConn;

    public String getRegisterConn() {
        return RegisterConn;
    }

    public void setRegisterConn(String registerConn) {
        RegisterConn = registerConn;
    }
}
