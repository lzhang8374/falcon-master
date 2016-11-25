package org.trex.falcon.rpc.monitor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.trex.falcon.common.URL;

import java.net.InetSocketAddress;

public class MonitorClient {

    //服务类
    private Bootstrap bootstrap = new Bootstrap();
    //线程池
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static MonitorClient instance;

    private MonitorClient() {
    }

    public static MonitorClient getInstance() {
        if (instance == null) {
            instance = new MonitorClient();
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
                ch.pipeline().addLast("decoder", new StringDecoder());
                ch.pipeline().addLast("encoder", new StringEncoder());
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
