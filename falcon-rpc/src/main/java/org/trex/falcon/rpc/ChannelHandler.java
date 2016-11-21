package org.trex.falcon.rpc;

import io.netty.channel.Channel;
import org.trex.falcon.rpc.model.Response;

public interface ChannelHandler {
    public void received(Channel channel, Response response);
    public void exceptionCaught(Channel channel);
    public void channelInactive(Channel channel);
}
