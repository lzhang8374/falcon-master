package org.trex.falcon.monitor.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.trex.falcon.common.MonitorService;
import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.registry.ZookeeperRegistry;

@Component
public class Startup implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Server server;

    @Value("${port}")
    private int port;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null) {//root application context 没有parent，他就是老大.
            // 初始化注册中心
            Registry registry = new ZookeeperRegistry(new URL("127.0.0.1", 2181));
            //开始监听
            this.server.start();
            //注册
            registry.publish(MonitorService.class, new URL("127.0.0.1", this.port), Registry.PROVIDER);
        }

    }

}
