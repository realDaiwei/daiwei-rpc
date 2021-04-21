package io.daiwei.rpc.stub.net.params;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Daiwei on 2021/4/22
 */
@Data
@AllArgsConstructor
public class SystemHealthInfo {

    private BigDecimal cpuLoadPercent;

    private BigDecimal memLoadPercent;
}
