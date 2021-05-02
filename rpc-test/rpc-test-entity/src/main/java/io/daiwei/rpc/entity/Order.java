package io.daiwei.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Daiwei on 2021/5/2
 */
@Data
@Builder
public class Order implements Serializable {

    private Long orderId;

    private String customName;

    private Date createTime;

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customName='" + customName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
