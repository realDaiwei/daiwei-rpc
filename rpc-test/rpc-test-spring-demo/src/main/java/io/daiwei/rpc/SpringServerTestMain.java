package io.daiwei.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Daiwei on 2021/5/2
 */
public class SpringServerTestMain {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("META-INF/server-context.xml");
        Thread.sleep(300000);
        applicationContext.close();
    }
}
