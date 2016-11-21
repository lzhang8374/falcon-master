package org.trex.falcon.zookeeper;

import java.util.List;

public class ChildListenerImpl implements ChildListener {
    public void childChanged(String path, List<String> children) {
        System.out.println("change child:" + path);
        for (String child : children) {
            System.out.println(child);
        }
    }
}
