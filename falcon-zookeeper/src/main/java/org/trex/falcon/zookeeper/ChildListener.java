package org.trex.falcon.zookeeper;

import java.util.Map;

public interface ChildListener {

    void childChanged(String path, Map<String, String> children);

}
