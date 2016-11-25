package org.trex.falcon.rpc.loadbalance;

import org.trex.falcon.common.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 带权重轮询负载均衡算法
 */
public class RoundRobinLoadBalance implements LoadBalance {

    @Override
    public URL doSelect(List<URL> providerUrls) {
        //优先级总和
        double prioritySum = 0;
        for (URL url : providerUrls) {
            prioritySum += url.getPriority();
        }

        double stepPrioritySum = 0;
        for (int i = 0; i < providerUrls.size(); i++) {
            stepPrioritySum += providerUrls.get(i).getPriority();
            if (Math.random() <= stepPrioritySum / prioritySum) {
                return providerUrls.get(i);
            }
        }

        throw new RuntimeException("负载均衡出错");
    }


    public static void main(String[] args) {
        List<URL> providerUrls = new ArrayList<>();
        Map<String, Integer> result = new HashMap<>();

        URL url1 = new URL("11111:11");
        url1.setPriority(10);
        result.put(url1.toString(), 0);
        providerUrls.add(url1);

        URL url2 = new URL("22222:22");
        url2.setPriority(10);
        result.put(url2.toString(), 0);
        providerUrls.add(url2);

        URL url3 = new URL("33333:33");
        url3.setPriority(5);
        result.put(url3.toString(), 0);
        providerUrls.add(url3);

        URL url4 = new URL("44444:44");
        url4.setPriority(10);
        result.put(url4.toString(), 0);
        providerUrls.add(url4);

        RoundRobinLoadBalance t = new RoundRobinLoadBalance();

        for(int i = 0; i < 10000; i++) {
            URL url = t.doSelect(providerUrls);
            int c = result.get(url.toString());
            c+= 1;
            result.put(url.toString(), c);
        }

        for(String key : result.keySet()) {
            double b = (result.get(key) / 10000d) * 100d;
            System.out.println(key + "--------------" + result.get(key) + "----------------" + b);
        }
    }

}
