package org.trex.falcon.admin.service;

import org.springframework.stereotype.Component;
import org.trex.falcon.admin.model.Service;
import org.trex.falcon.zookeeper.ZookeeperClient;
import org.trex.falcon.zookeeper.curator.CuratorZookeeperClient;


import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceService {

    ZookeeperClient zk = CuratorZookeeperClient.getInstance("127.0.0.1:2181");

    public List<Service> getService() {
        List<Service> result = new ArrayList<Service>();
        List<String> list = zk.getChildren("/falcon");
        for(String s : list) {
            Service service = new Service();
            service.setName(s);
            result.add(service);
        }
        return result;
    }

}
