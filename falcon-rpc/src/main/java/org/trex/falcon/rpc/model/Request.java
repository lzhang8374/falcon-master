package org.trex.falcon.rpc.model;

import java.util.Arrays;

public class Request extends Message {

    private String service;
    private String method;
    private Object[] args;

    public Request() {
        
    }

    public Request(String service, String method, Object[] args) {
        this.service = service;
        this.method = method;
        this.args = args;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "Request [service=" + service + ", method=" + method + ", args=" + Arrays.toString(args) + "]";
    }
}
