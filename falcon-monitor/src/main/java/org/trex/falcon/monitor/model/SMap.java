package org.trex.falcon.monitor.model;

import java.util.HashMap;
import java.util.Map;

public class SMap {
    private static Map<String, Statistics> serviceStatistics = new HashMap<>();
    public static void init() {
        serviceStatistics = new HashMap<>();
    }

    public static Map<String, Statistics> getServiceStatistics() {
        return serviceStatistics;
    }
}
