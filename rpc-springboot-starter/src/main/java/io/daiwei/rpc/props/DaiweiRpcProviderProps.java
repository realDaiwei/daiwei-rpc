package io.daiwei.rpc.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Daiwei on 2021/5/2
 */
@Configuration
@ConfigurationProperties(prefix = "spring.daiwei.rpc.provider")
public class DaiweiRpcProviderProps {

    private String registerConn;

    private String scanPackagePath;

    public String getRegisterConn() {
        return registerConn;
    }

    public void setRegisterConn(String registerConn) {
        this.registerConn = registerConn;
    }

    public String getScanPackagePath() {
        return scanPackagePath;
    }

    public void setScanPackagePath(String scanPackagePath) {
        this.scanPackagePath = scanPackagePath;
    }
}
