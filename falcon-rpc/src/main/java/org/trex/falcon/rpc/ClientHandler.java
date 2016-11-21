package org.trex.falcon.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.trex.falcon.rpc.invoker.Invoker;
import org.trex.falcon.rpc.invoker.InvokerHolder;
import org.trex.falcon.rpc.model.Response;

public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    private ChannelHandler channelHandler;

    public ClientHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    /**
     * 接收消息
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        System.out.println("客服端收到响应：" + response.toString());
        this.channelHandler.received(ctx.channel(), response);
    }

    /**
     * 断开链接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channelHandler.channelInactive(ctx.channel());
    }

    /**
     * 发生错误
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.channelHandler.exceptionCaught(ctx.channel());
    }

}
