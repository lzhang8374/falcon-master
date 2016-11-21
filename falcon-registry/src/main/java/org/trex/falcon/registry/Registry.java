package org.trex.falcon.registry;

import org.trex.falcon.common.URL;
import org.trex.falcon.zookeeper.ChildListener;

import java.util.List;

/**
 * 注册中心
 */
public interface Registry {

    public static int PROVIDER = 1;
    public static int CONSUMER = 0;

    /**
     * 注销注册中心
     */
    void destroy();

    /**
     * 注册中心是否可用
     */
    boolean isAvailable();

    /**
     * 发布服务提供者/消费者
     */
    void publish(Class<?> service, URL url, int type);

    /**
     * 服务提供者/消费者下架
     */
    void unpublish(Class<?> service, URL url, int type);

    /**
     * 订阅服务
     */
    void subscribe(Class<?> service, ChildListener childListener);

    /**
     * 取消订阅
     */
    void unsubscribe(Class<?> service, ChildListener childListener);

    /**
     * 获取所有服务
     */
    List<String> getServices();

    /**
     * 获取某个服务的所有提供者
     */
    List<URL> getProviders(Class<?> service);

    /**
     * 获取某个服务的所有消费者
     */
    List<URL> getConsumers(Class<?> service);

}
