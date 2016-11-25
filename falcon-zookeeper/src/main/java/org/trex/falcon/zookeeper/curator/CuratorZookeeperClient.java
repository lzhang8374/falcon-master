package org.trex.falcon.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.trex.falcon.zookeeper.ChildListener;
import org.trex.falcon.zookeeper.ZookeeperClient;
import org.trex.falcon.zookeeper.ZookeeperException;

import java.util.*;

public class CuratorZookeeperClient implements ZookeeperClient {

    private String url;
    private CuratorFramework client = null;

    private Map<String, Set<ChildListener>> listenerMap = null;
    private Map<String, PathChildrenCache> cacheMap = null;

    private static CuratorZookeeperClient instance;

    private CuratorZookeeperClient(String url) {
        this.url = url;
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 1);
        this.client = CuratorFrameworkFactory.builder()
                .connectString(url) //多个url可以使用逗号分开
                .sessionTimeoutMs(500) //session失效时间，当程序意外中断，session过指定毫秒失效，失效后临时节点删除
                .retryPolicy(retryPolicy)
                .build();
        this.client.start();
        this.listenerMap = new HashMap<>();
        this.cacheMap = new HashMap<>();
    }

    public static CuratorZookeeperClient getInstance(String url) {
        if (instance == null) {
            instance = new CuratorZookeeperClient(url);
        }
        return instance;
    }

    @Override
    public void create(String path, boolean ephemeral) {
        try {
            if (ephemeral) {
                this.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            } else {
                this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public void delete(String path) {
        try {
            this.client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            Stat stat = this.client.checkExists().forPath(path);
            return stat != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public String getData(String path) {
        try {
            String r = new String(this.client.getData().forPath(path));
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public void setData(String path, String value) {
        try {
            this.client.setData().forPath(path, value.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            List<String> children = this.client.getChildren().forPath(path);
            return children;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public void addChildListener(final String path, final ChildListener listener) {
        try {
            PathChildrenCache cache = this.cacheMap.get(path);
            Set<ChildListener> listeners = this.listenerMap.get(path);

            //处理listener
            if (listeners == null) {
                listeners = new HashSet<>();
            }
            listeners.add(listener);
            this.listenerMap.put(path, listeners);

            //处理cache
            if (cache == null) {
                cache = new PathChildrenCache(this.client, path, true);
                cache.start(PathChildrenCache.StartMode.NORMAL);
                cache.getListenable().addListener(new PathChildrenCacheListener() {
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)
                                || event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)
                                || event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                            Map<String, String> children = new HashMap<>();
                            for (String c : getChildren(path)) {
                                children.put(c, getData(path + "/" + c));
                            }
                            for (ChildListener listener : listenerMap.get(path)) {
                                listener.childChanged(path, children);
                            }
                        }
                    }
                });
                this.cacheMap.put(path, cache);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public void removeChildListener(String path, ChildListener listener) {

    }

    @Override
    public void close() {
        try {
            this.client.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public boolean isConnected() {
        return true;
    }


}
