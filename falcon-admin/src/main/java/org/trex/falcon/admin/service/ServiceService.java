package org.trex.falcon.admin.service;

import org.springframework.stereotype.Component;
import org.trex.falcon.admin.model.Service;
import org.trex.falcon.admin.utils.HttpUtil;
import org.trex.falcon.admin.utils.JsonBinder;
import org.trex.falcon.common.utils.DateUtil;
import org.trex.falcon.common.utils.StringUtil;
import org.trex.falcon.zookeeper.ZookeeperClient;
import org.trex.falcon.zookeeper.curator.CuratorZookeeperClient;

import java.util.*;

@Component
public class ServiceService {

    ZookeeperClient zk = CuratorZookeeperClient.getInstance("127.0.0.1:2181");

    public List<Service> getServices() {
        List<Service> result = new ArrayList<Service>();
        List<String> list = zk.getChildren("/falcon");
        for (String s : list) {
            Service service = new Service();
            service.setName(s);
            result.add(service);
        }
        return result;
    }

    public List<Service> getProviders(String service) {
        List<Service> result = new ArrayList<Service>();
        List<String> list = zk.getChildren("/falcon/" + service + "/providers");
        for (String c : list) {
            Service consumer = new Service();
            consumer.setName(c);
            result.add(consumer);
        }
        return result;
    }

    public List<Service> getConsumers(String service) {
        List<Service> result = new ArrayList<Service>();
        List<String> list = zk.getChildren("/falcon/" + service + "/consumers");
        for (String c : list) {
            Service consumer = new Service();
            consumer.setName(c);
            result.add(consumer);
        }
        return result;
    }

    public Map<String, Object> getChart(String service, String provider) {
        String url = "http://localhost:9118/falcon-monitor/monitor/" + service;
        if (!StringUtil.isEmpty(provider)) {
            url += "/" + provider;
        }

        String response = HttpUtil.doGet(url);
        Map<String, Object> data = JsonBinder.buildNormalBinder().fromJson(response, Map.class);

        // x轴
        List<String> times = new ArrayList<String>();
        for (String t : data.keySet()) {
            Date d = new Date(Long.parseLong(t));
            times.add(DateUtil.format(d, "HH:mm") + "-" + t);
        }
        Collections.sort(times);

        // series1
        List<Integer> datas1 = new ArrayList<Integer>();
        for (String t : times) {
            Map<String, Object> statistics = (Map<String, Object>) data.get(t.split("-")[1]);
            datas1.add(Integer.parseInt(statistics.get("total").toString()));
        }
        Map<String, Object> serie1 = new HashMap<String, Object>();
        serie1.put("name", "总请求数");
        serie1.put("type", "line");
        serie1.put("data", datas1);

        // series2
        List<Integer> datas2 = new ArrayList<Integer>();
        for (String t : times) {
            Map<String, Object> statistics = (Map<String, Object>) data.get(t.split("-")[1]);
            datas2.add(Integer.parseInt(statistics.get("success").toString()));
        }
        Map<String, Object> serie2 = new HashMap<String, Object>();
        serie2.put("name", "成功数");
        serie2.put("type", "line");
        serie2.put("data", datas2);


        List<String> ts = new ArrayList<>();
        for (String t : times) {
            ts.add(t.split("-")[0]);
        }

        // result
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("title", service + "-" + provider);
        result.put("date", ts);
        result.put("series", Arrays.asList(serie1, serie2));
        result.put("legends", Arrays.asList("总请求数", "成功数"));
        result.put("zoom", 100);
        return result;
    }

    public String getProvider(String service, String provider) {
        String priority = this.zk.getData("/falcon/" + service + "/providers/" + provider);
        if (StringUtil.isEmpty(priority)) {
            priority = "10";
        }
        return priority;
    }

    public void setPriority(String service, String provider, String priority) {
        this.zk.setData("/falcon/" + service + "/providers/" + provider, priority);
    }
}
