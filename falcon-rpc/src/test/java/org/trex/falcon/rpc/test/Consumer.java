package org.trex.falcon.rpc.test;

import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.registry.ZookeeperRegistry;
import org.trex.falcon.rpc.Client;
import org.trex.falcon.rpc.config.ConsumerConfig;
import org.trex.falcon.rpc.test.services.Service1;

public class Consumer {

    public static void main(String[] args) throws Exception {

        Registry registry = new ZookeeperRegistry(new URL("127.0.0.1", 2181));

        ConsumerConfig consumerConfig = new ConsumerConfig(Service1.class, registry);


        Service1 service1 = (Service1) consumerConfig.get();
        for (int i = 0; i < 20000; i++) {
            System.out.println(service1.sayHello("zhanglei" + i));
            Thread.sleep(500);
        }

        System.in.read(); // 按任意键退出

        //断开连接
        consumerConfig.destroy();

        System.out.println("消费者进程结束！！！");
        System.exit(0);
    }
}
