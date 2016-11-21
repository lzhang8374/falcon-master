package org.trex.falcon.monitor;


import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.registry.ZookeeperRegistry;
import org.trex.falcon.rpc.config.ProviderConfig;

import java.io.IOException;


public class Startup {

    public static void main(String[] args) throws IOException {

        int port = 9119;

        // 初始化注册中心
        Registry registry = new ZookeeperRegistry(new URL("127.0.0.1", 2181));

        // 注册服务
        ProviderConfig providerConfig = new ProviderConfig(MonitorService.class, registry, new MonitorServiceImpl(), new URL("127.0.0.1", port));
        providerConfig.export();

        System.in.read(); // 按任意键退出

        // 注销服务
        providerConfig.unexport();
        System.out.println("提供者进程" + port + "结束！！！");
        System.exit(0);
    }
}
