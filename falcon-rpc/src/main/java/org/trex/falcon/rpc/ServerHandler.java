package org.trex.falcon.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.trex.falcon.rpc.invoker.Invoker;
import org.trex.falcon.rpc.invoker.InvokerHolder;
import org.trex.falcon.rpc.model.Request;
import org.trex.falcon.rpc.model.Response;

public class ServerHandler extends SimpleChannelInboundHandler<Request> {


    /**
     * 通道建立
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("通道被建立");
    }

    /**
     * 接收消息
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        System.out.println("提供者收到请求:" + request.toString());
        //获取命令执行器
        Invoker invoker = InvokerHolder.getInstance().getInvoker(request.getService());
        Response response = null;
        if (invoker != null) {
            try {
                Object obj = invoker.invoke(request.getMethod(), request.getArgs());
                response = new Response(200, obj);
            } catch (Exception e) {
                response = new Response(500, null);
            }
        } else {//未找到执行者
            response = new Response(404, null);
        }
        ctx.channel().writeAndFlush(response);
    }

    /**
     * 断线移除会话
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道断开...");
    }

}
