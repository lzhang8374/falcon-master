package org.trex.falcon.rpc.test.services;

public class Service1Impl implements Service1 {

    @Override
    public String sayHello(String to) {
        return "hello " + to;
    }
}
