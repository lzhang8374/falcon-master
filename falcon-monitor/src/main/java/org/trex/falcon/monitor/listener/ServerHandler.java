package org.trex.falcon.monitor.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trex.falcon.common.utils.DateUtil;
import org.trex.falcon.monitor.model.SMap;
import org.trex.falcon.monitor.model.Statistics;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

//    private long oldtime = 0;

    /**
     * 通道建立
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("通道被建立");
    }

    /**
     * 接收消息
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        logger.info(request);
        String[] strs = request.split("\t");
        if (strs.length == 7) {
            long time = DateUtil.parse(strs[0].substring(0, strs[0].lastIndexOf(":")) + ":00").getTime();

//            if (this.oldtime == 0) {
//                this.oldtime = time;
//            }

            String consumer = strs[1];
            String provider = strs[2];
            String service = strs[3];
            String method = strs[4];
            String code = strs[5];
            int executeTime = Integer.parseInt(strs[6]);


            this.statisticsService(time, service, provider, code, executeTime);

//            if (time > this.oldtime) { //持久化处理
//                this.persistent(time);
//                this.oldtime = time;
//            }
        }
    }


    /**
     * 断线移除会话
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道断开...");
    }

    /**
     * 统计service
     */
    private void statisticsService(long time, String service, String provider, String code, int executeTime) {
        String key = "falcon_monitor_" + time + "_" + service + "_" + provider;
        Statistics statistics = SMap.getServiceStatistics().get(key);
        if (statistics == null) {
            statistics = new Statistics();
        }
        statistics.addTotal();
        if ("200".equals(code)) {
            statistics.addSuccess();
        } else {
            statistics.addFailure();
        }
        statistics.addAllTime(executeTime);
        SMap.getServiceStatistics().put(key, statistics);

        for(String k : SMap.getServiceStatistics().keySet()) {
            System.out.println(k + "-------------------->" + SMap.getServiceStatistics().get(k).getTotal());
        }
    }

    /**
     * 持久化到Redis
     */
    private void persistent(long time) {
        for (String key : SMap.getServiceStatistics().keySet()) {
            String[] strs = key.split("_");
            if(strs.length == 5) {
            }
        }

    }

}
