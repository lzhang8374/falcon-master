package org.trex.falcon.monitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.trex.falcon.monitor.model.SMap;
import org.trex.falcon.monitor.model.Statistics;
import org.trex.falcon.monitor.utils.JsonBinder;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MonitorController {

    @RequestMapping(value = "/monitor/{service:[a-zA-Z0-9\\\\.]+}", method = RequestMethod.GET)
    @ResponseBody
    public String monitor(@PathVariable("service") String service) {
        Map<String, Statistics> result = new HashMap<>();
        for (String key : SMap.getServiceStatistics().keySet()) {
            String[] strs = key.split("_");
            if (strs.length == 5) {
                if (strs[3].equals(service)) {
                    Statistics statistics = SMap.getServiceStatistics().get(key);
                    result.put(strs[2], statistics);
                }
            }
        }

        return JsonBinder.buildNormalBinder().toJson(result);
    }

    @RequestMapping(value = "/monitor/{service}/{provider}", method = RequestMethod.GET)
    @ResponseBody
    public String monitor(@PathVariable("service") String service, @PathVariable("provider") String provider) {
        Map<String, Statistics> result = new HashMap<>();
        for (String key : SMap.getServiceStatistics().keySet()) {
            String[] strs = key.split("_");
            if (strs.length == 5) {
                if (strs[3].equals(service) && strs[4].equals(provider)) {
                    Statistics statistics = SMap.getServiceStatistics().get(key);
                    result.put(strs[2], statistics);
                }
            }
        }

        return JsonBinder.buildNormalBinder().toJson(result);
    }
}
