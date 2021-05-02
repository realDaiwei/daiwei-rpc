package io.daiwei.rpc;

import io.daiwei.rpc.service.UserActionService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Daiwei on 2021/5/1
 */
public class SpringClientTestMain {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("META-INF/bean-context.xml");
        UserActionService service = applicationContext.getBean(UserActionService.class);
        System.out.println(service.getUser());
        applicationContext.close();
    }
}
