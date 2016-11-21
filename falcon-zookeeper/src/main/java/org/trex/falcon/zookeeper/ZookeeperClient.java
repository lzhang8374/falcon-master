package org.trex.falcon.zookeeper;

import java.util.List;

public interface ZookeeperClient {

    void create(String path, boolean ephemeral);

    void delete(String path);

    boolean exists(String path);

    List<String> getChildren(String path);

    void addChildListener(String path, ChildListener listener);

    void removeChildListener(String path, ChildListener childListener);

    void close();

    String getUrl();

    boolean isConnected();
}
