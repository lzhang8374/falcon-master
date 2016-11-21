package org.trex.falcon.rpc.config;

import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.rpc.Server;
import org.trex.falcon.rpc.invoker.Invoker;
import org.trex.falcon.rpc.invoker.InvokerHolder;

public class ProviderConfig {

    // 服务提供者地址
    private URL providerUrl;
    //注册中心地址
    private Registry registry;
    //服务api
    private Class<?> service;
    //服务实现对象
    private Object target;

    public ProviderConfig(Class<?> service, Registry registry, Object target, URL providerUrl) {
        this.registry = registry;
        this.service = service;
        this.target = target;
        this.providerUrl = providerUrl;
    }

    public void export() {

        //加入执行器
        InvokerHolder invokerHolder = InvokerHolder.getInstance();
        Invoker invoker = new Invoker(this.target);
        invokerHolder.addInvoker(this.service.getName(), invoker);

        //开始监听
        Server server = Server.getInstance();
        server.start(this.providerUrl.getPort());

        //注册
        this.registry.publish(this.service, this.providerUrl, Registry.PROVIDER);
    }

    public void unexport() {
        //注销
        this.registry.unpublish(this.service, this.providerUrl, Registry.PROVIDER);

        //停止监听
        Server server = Server.getInstance();
        server.stop(this.providerUrl.getPort());
    }

}
