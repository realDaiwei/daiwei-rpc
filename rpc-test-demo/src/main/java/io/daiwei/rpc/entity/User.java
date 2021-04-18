package io.daiwei.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Daiwei on 2021/4/16
 */
@Data
@Builder
public class User implements Serializable {

    private Long id;

    private String username;

    private Integer age;

    private ClassInfo info;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
