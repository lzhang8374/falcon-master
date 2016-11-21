package org.trex.falcon.rpc.invoker;

import java.util.HashMap;
import java.util.Map;

public class InvokerHolder {

    private static InvokerHolder instance;

    //命令调用器
    private Map<String, Invoker> invokers = null;

    private InvokerHolder() {
        this.invokers = new HashMap<>();
    }

    public static InvokerHolder getInstance() {
        if (instance == null) {
            instance = new InvokerHolder();
        }
        return instance;
    }

    /**
     * 添加命令调用
     */
    public void addInvoker(String service, Invoker invoker) {
        Invoker i = this.invokers.get(service);
        if (i == null) {
            this.invokers.put(service, invoker);
        }
    }


    /**
     * 获取命令调用
     */
    public Invoker getInvoker(String service) {
        return this.invokers.get(service);
    }

}
