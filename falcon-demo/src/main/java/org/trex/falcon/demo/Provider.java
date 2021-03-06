package org.trex.falcon.demo;

import org.trex.falcon.common.URL;
import org.trex.falcon.demo.services.Service1;
import org.trex.falcon.demo.services.Service1Impl;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.registry.ZookeeperRegistry;
import org.trex.falcon.rpc.config.ProviderConfig;

public class Provider {

    public static void main(String[] args) throws Exception {

        int port = 9999;

        // 初始化注册中心
        Registry registry = new ZookeeperRegistry(new URL("127.0.0.1", 2181));

        // 注册服务
        ProviderConfig providerConfig = new ProviderConfig(Service1.class, registry, new Service1Impl(), new URL("localhost", port));
        providerConfig.export();


        System.in.read(); // 按任意键退出

        // 注销服务
        providerConfig.unexport();
        System.out.println("提供者进程" + port + "结束！！！");
        System.exit(0);
    }

}
