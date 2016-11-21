package org.trex.falcon.rpc.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trex.falcon.rpc.proxy.RpcProxy;

import java.util.HashMap;
import java.util.Map;

public class LogHelper {

    private Map<String, Logger> loggers;

    private static LogHelper instance;

    private LogHelper() {
        this.loggers = new HashMap<>();
    }

    public static LogHelper getInstance() {
        if (instance == null) {
            instance = new LogHelper();
        }
        return instance;
    }

    public Logger getLogger(Class cls) {
        return this.getLogger(cls.getName());
    }

    public Logger getLogger(String app) {
        Logger logger = this.loggers.get(app);
        if (logger == null) {
            logger = LoggerFactory.getLogger(RpcProxy.class);
            this.loggers.put(app, logger);
        }
        return logger;
    }

    public static void main(String[] args) {

        LogHelper logHelper = LogHelper.getInstance();

        Logger logger = logHelper.getLogger(LogHelper.class);

        for(int i = 0; i < 10; i++) {
            logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + i);
        }
    }
}
