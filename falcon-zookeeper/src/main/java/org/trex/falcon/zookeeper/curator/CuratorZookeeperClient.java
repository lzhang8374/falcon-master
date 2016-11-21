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

    private Map<String, Set<ChildListener>> listeners = null;
    private Map<String, PathChildrenCache> caches = null;

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
        this.listeners = new HashMap<String, Set<ChildListener>>();
        this.caches = new HashMap<String, PathChildrenCache>();
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
            System.out.println("-------------------------------------" + path + "------------------------");
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
            final PathChildrenCache cache = new PathChildrenCache(this.client, path, true);
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            cache.getListenable().addListener(new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)
                            || event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)
                            || event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {

                        List<String> children = new ArrayList<String>();
                        List<ChildData> datas = cache.getCurrentData();
                        for (ChildData data : datas) {
                            String[] paths = data.getPath().split("/");
                            children.add(paths[paths.length - 1]);
                        }
                        listener.childChanged(path, children);
                    }
                }
            });

//            this.listeners.put(path, listener);
//            this.caches.put(path, cache);
        } catch (Exception e) {
//            this.listeners.remove(path);
//            this.caches.remove(path);
            e.printStackTrace();
            throw new ZookeeperException(e.getMessage());
        }








//        PathChildrenCache cache = this.caches.get(path);
//        Set<ChildListener> listenerSet = this.listeners.get(path);
//
//        if(listenerSet == null) {
//            listenerSet = new HashSet<ChildListener>();
//        }
//        listenerSet.add(listener);
//
//        if (cache == null) { //尚未注册过
//            try {
//                cache = new PathChildrenCache(this.client, path, true);
//                cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
//                cache.getListenable().addListener(new PathChildrenCacheListener() {
//                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
//                        if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)
//                                || event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)
//                                || event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
//
//                            List<String> children = new ArrayList<String>();
//                            List<ChildData> datas = cache.getCurrentData();
//                            for (ChildData data : datas) {
//                                String[] paths = data.getPath().split("/");
//                                children.add(paths[paths.length - 1]);
//                            }
//                            listener.childChanged(path, children);
//                        }
//                    }
//                });
//
//                this.listeners.put(path, listener);
//                this.caches.put(path, cache);
//            } catch (Exception e) {
//                this.listeners.remove(path);
//                this.caches.remove(path);
//                e.printStackTrace();
//                throw new ZookeeperException(e.getMessage());
//            }
//        }
    }

    @Override
    public void removeChildListener(String path, ChildListener listener) {
//        if (this.listeners.get(path) != null && this.caches.get(path) != null) {//已经注册过
//            try {
//                PathChildrenCache cache = this.caches.get(path);
//                cache.close();
//                this.listeners.remove(path);
//                this.caches.remove(path);
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new ZookeeperException(e.getMessage());
//            }
//        } else {//尚未注册过
//            this.listeners.remove(path);
//            this.caches.remove(path);
//            throw new ZookeeperException(path + " listener not regist");
//        }
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
