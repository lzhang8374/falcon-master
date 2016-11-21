package org.trex.falcon.zookeeper;


import org.trex.falcon.zookeeper.curator.CuratorZookeeperClient;

public class ZookeeperClientTest {

    public static void main(String[] args) throws Exception {

        ZookeeperClient zkClient = CuratorZookeeperClient.getInstance("localhost:2181");

        zkClient.create("/test/test01", false);
        zkClient.create("/test/test02", true);


        ChildListenerImpl c = new ChildListenerImpl();
        zkClient.addChildListener("/test/test01", c);
        zkClient.addChildListener("/test", c);

        try {
            zkClient.addChildListener("/test/test01", c);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            zkClient.addChildListener("/test", c);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Thread.sleep(120 * 1000);

        System.out.println("----------------------------------------------");


        zkClient.removeChildListener("/test/test01", null);
        zkClient.removeChildListener("/test", null);

        Thread.sleep(60 * 1000);

        zkClient.close();
        System.out.println("over...");
    }
}
