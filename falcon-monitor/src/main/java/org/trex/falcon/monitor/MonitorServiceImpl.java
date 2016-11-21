package org.trex.falcon.monitor;

public class MonitorServiceImpl implements MonitorService {

    @Override
    public void collect(String aaa) {
        System.out.println("收到日志" + aaa);
    }

}
