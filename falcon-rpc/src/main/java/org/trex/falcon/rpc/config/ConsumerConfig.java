package org.trex.falcon.rpc.config;

import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;
import org.trex.falcon.rpc.Client;
import org.trex.falcon.rpc.proxy.RpcProxyFactory;
import org.trex.falcon.zookeeper.ChildListener;

import java.util.List;

public class ConsumerConfig {

    private Registry registry;
    private Class<?> service;
    private Object proxy;

    public ConsumerConfig(Class<?> service, Registry registry) {
        this.service = service;
        this.registry = registry;
    }

    /**
     * 获取代理对象
     */
    public Object get() {
        this.proxy = RpcProxyFactory.create(this.service, this.registry);
        return proxy;
    }

    /**
     * 销毁
     */
    public void destroy() {
        this.proxy = null;
    }

}
