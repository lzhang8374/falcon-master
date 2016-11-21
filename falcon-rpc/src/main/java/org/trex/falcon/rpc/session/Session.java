package org.trex.falcon.rpc.session;


import io.netty.channel.Channel;
import org.trex.falcon.common.URL;

import java.net.InetSocketAddress;

public class Session {

    //实际会话对象
    private Channel channel;

    public Session(Channel channel) {
        this.channel = channel;
    }

    public void write(Object message) {
        this.channel.writeAndFlush(message);
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    public URL localUrl() {
        InetSocketAddress address = (InetSocketAddress) this.channel.localAddress();
        return new URL(address.getHostName(), address.getPort());
    }

    public URL remoteUrl() {
        InetSocketAddress address = (InetSocketAddress) this.channel.remoteAddress();
        return new URL(address.getHostName(), address.getPort());
    }


    public void close() {
        this.channel.close();
    }
}
