package org.trex.falcon.registry;

import org.trex.falcon.common.URL;
import org.trex.falcon.zookeeper.ChildListener;

import java.util.List;


public class RegistyTest {

    public static void main(String[] args) throws InterruptedException {
//        System.out.println("start...");
//
//        Registry registry = new ZookeeperRegistry(new URL("127.0.0.1", 2181));
//
//        registry.publish(String.class, new URL("127.0.0.0", 100), Registry.PROVIDER);
//
//        Thread.sleep(10 * 1000);
//
//        registry.subscribe(String.class, new URL("127.0.0.0", 100), new ChildListener() {
//            @Override
//            public void childChanged(String path, List<String> children) {
//                System.out.println("--------------------------" + path);
//                for(String c:children) {
//                    System.out.println(c);
//                }
//            }
//        });
//
//        registry.publish(String.class, new URL("195.168.56.10", 200));
//        try {
//            registry.publish(String.class, new URL("195.168.56.10", 200));
//        } catch(Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//
//        Thread.sleep(120 * 1000);
//
//        registry.destroy();
//
//
//        System.out.println("over...");
    }
}
