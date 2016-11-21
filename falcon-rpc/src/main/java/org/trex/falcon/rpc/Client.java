package org.trex.falcon.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.trex.falcon.common.URL;
import org.trex.falcon.rpc.codc.MessageDecoder;
import org.trex.falcon.rpc.codc.MessageEncoder;

import java.net.InetSocketAddress;

public class Client {

    //服务类
    private Bootstrap bootstrap = new Bootstrap();
    //线程池
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ChannelHandler channelHandler;
    private static Client instance;

    private Client(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    public static Client getInstance(ChannelHandler channelHandler) {
        if (instance == null) {
            instance = new Client(channelHandler);
            instance.init();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        // 设置循环线程组事例
        this.bootstrap.group(workerGroup);
        // 设置channel工厂
        this.bootstrap.channel(NioSocketChannel.class);

        // 设置管道
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder", new MessageDecoder());
                ch.pipeline().addLast("encoder", new MessageEncoder());
                ch.pipeline().addLast("handler", new ClientHandler(channelHandler));
            }
        });
    }

    /**
     * 连接
     */
    public Channel connect(URL providerUrl) throws InterruptedException {
        ChannelFuture connect = this.bootstrap.connect(new InetSocketAddress(providerUrl.getHost(), providerUrl.getPort()));
        connect.sync();
        return connect.channel();
    }

    /**
     * 关闭
     */
    public void shutdown() {
        this.workerGroup.shutdownGracefully();
    }
}
