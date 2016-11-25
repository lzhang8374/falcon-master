package org.trex.falcon.monitor.model;

public class Statistics {

    private int total;
    private int success;
    private int failure;
    private int allTime;

    public Statistics() {
        this.total = 0;
        this.success = 0;
        this.failure = 0;
        this.allTime = 0;
    }

    public void addTotal() {
        this.total += 1;
    }

    public void addSuccess() {
        this.success += 1;
    }

    public void addFailure() {
        this.failure += 1;
    }

    public int getTotal() {
        return total;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }

    public void addAllTime(int executeTime) {
        this.allTime += executeTime;
    }



}
