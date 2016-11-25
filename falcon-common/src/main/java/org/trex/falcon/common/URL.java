package org.trex.falcon.common;

/**
 * URL
 */
public class URL {

    private String host;
    private int port;
    private int priority = 0;

    public URL(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public URL(String url) {
        String[] strs = url.split(":");
        this.host = strs[0];
        this.port = Integer.parseInt(strs[1]);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return this.host + ":" + this.getPort();
    }

}
