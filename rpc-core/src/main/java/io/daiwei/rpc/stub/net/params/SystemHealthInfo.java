package io.daiwei.rpc.stub.net.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Daiwei on 2021/4/22
 */
@Data
public class SystemHealthInfo implements Serializable {

    private long latency;

    private BigDecimal cpuLoadPercent;

    private BigDecimal memLoadPercent;

    private long respSendTime;

    public SystemHealthInfo() {}

    public SystemHealthInfo(long latency, BigDecimal cpuLoadPercent, BigDecimal memLoadPercent) {
        this.latency = latency;
        this.cpuLoadPercent = cpuLoadPercent;
        this.memLoadPercent = memLoadPercent;
        this.respSendTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "SystemHealthInfo{" +
                "latency=" + latency +
                ", cpuLoadPercent=" + cpuLoadPercent +
                ", memLoadPercent=" + memLoadPercent +
                '}';
    }
}