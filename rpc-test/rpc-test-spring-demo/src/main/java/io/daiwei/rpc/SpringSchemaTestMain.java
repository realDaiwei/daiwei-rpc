package io.daiwei.rpc;

import io.daiwei.rpc.service.UserActionService;
import io.daiwei.rpc.spring.stub.RpcSpringServerBoot;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Daiwei on 2021/5/2
 */
public class SpringSchemaTestMain {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("META-INF/daiwei-rpc.xml");
//        RpcSpringServerBoot bean = applicationContext.getBean(RpcSpringServerBoot.class);
//        System.out.println(bean.getClass());
        UserActionService service = applicationContext.getBean(UserActionService.class);

        for (int i = 0; i < 100; i++) {
//            Thread.sleep(1000);
            System.out.println(service.getUser());
        }

//        System.out.println(service.getUser());
        Thread.sleep(60 * 1000);
        for (int i = 0; i < 100; i++) {
//            Thread.sleep(1000);
            System.out.println(service.getUser());
        }
        applicationContext.close();
    }
}
