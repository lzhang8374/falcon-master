package org.trex.falcon.registry;


import org.trex.falcon.common.URL;
import org.trex.falcon.zookeeper.ChildListener;
import org.trex.falcon.zookeeper.ZookeeperClient;
import org.trex.falcon.zookeeper.curator.CuratorZookeeperClient;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperRegistry implements Registry {

    private static final String ZK_ROOT = "/falcon";
    private static final String ZK_TYPE_CONSUMERS = "consumers";
    private static final String ZK_TYPE_PROVIDERS = "providers";

    private static ZookeeperClient zkClient = null;

    public ZookeeperRegistry(URL url) {
        if (zkClient == null) {
            zkClient = CuratorZookeeperClient.getInstance(url.toString());
        }
    }

    @Override
    public boolean isAvailable() {
        return this.zkClient.isConnected();
    }

    @Override
    public void destroy() {
        this.zkClient.close();
    }

    @Override
    public void publish(Class<?> service, URL url, int type) {
        String path = ZK_ROOT + "/" + service.getName() + "/";
        if (type == Registry.PROVIDER) {
            path += ZK_TYPE_PROVIDERS;
        } else {
            path += ZK_TYPE_CONSUMERS;
        }

        if (!this.zkClient.exists(path)) {
            this.zkClient.create(path, false); // 创建持久节点
        }

        path += "/" + url.toString();
        if (!this.zkClient.exists(path)) {
            this.zkClient.create(path, true); // 创建临时节点
        } else {
            throw new RegistryException("provider already exists");
        }
    }

    @Override
    public void unpublish(Class<?> service, URL url, int type) {
        String path = ZK_ROOT + "/" + service.getName() + "/";
        if (type == Registry.PROVIDER) {
            path += ZK_TYPE_PROVIDERS;
        } else {
            path += ZK_TYPE_CONSUMERS;
        }
        path += "/" + url.toString();
        this.zkClient.delete(path);
    }

    @Override
    public void subscribe(Class<?> service, ChildListener childListener) {
        this.zkClient.addChildListener(ZK_ROOT + "/" + service.getName() + "/" + ZK_TYPE_PROVIDERS, childListener);
    }

    @Override
    public void unsubscribe(Class<?> service, ChildListener childListener) {
        this.zkClient.removeChildListener(ZK_ROOT + "/" + service.getName() + "/" + ZK_TYPE_PROVIDERS, childListener);
    }

    @Override
    public List<String> getServices() {
        return this.zkClient.getChildren(ZK_ROOT);
    }

    @Override
    public List<URL> getProviders(Class<?> service) {
        String path = ZK_ROOT + "/" + service.getName() + "/" + ZK_TYPE_PROVIDERS;
        List<String> strs = this.zkClient.getChildren(path);
        List<URL> urls = new ArrayList<URL>();
        for (String str : strs) {
            URL url = new URL(str);
            urls.add(url);
        }
        return urls;
    }

    @Override
    public List<URL> getConsumers(Class<?> service) {
        String path = ZK_ROOT + "/" + service.getName() + "/" + ZK_TYPE_CONSUMERS;
        List<String> strs = this.zkClient.getChildren(path);
        List<URL> urls = new ArrayList<URL>();
        for (String str : strs) {
            URL url = new URL(str);
            urls.add(url);
        }
        return urls;
    }
}
