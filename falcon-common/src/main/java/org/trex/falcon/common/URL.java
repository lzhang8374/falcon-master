package org.trex.falcon.common;

/**
 *
 */
public class URL {

    private String host;
    private int port;

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

    @Override
    public String toString() {
        return this.host + ":" + this.getPort();
    }

}
