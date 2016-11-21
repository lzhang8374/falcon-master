package org.trex.falcon.rpc.proxy;

import org.trex.falcon.common.URL;
import org.trex.falcon.registry.Registry;

import java.lang.reflect.Proxy;

public class RpcProxyFactory {

    public static Object create(Class<?> service, Registry registry) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        RpcProxy handler = new RpcProxy(service, registry);
        Object obj = Proxy.newProxyInstance(loader, new Class[]{service}, handler);
        return obj;
    }
}
