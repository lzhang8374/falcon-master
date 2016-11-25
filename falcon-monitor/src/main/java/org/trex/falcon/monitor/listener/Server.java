package org.trex.falcon.monitor.listener;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class Server {

    @Value("${port}")
    private String port;

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channel;

    @PostConstruct
    public void init() {
        System.out.println("--------------------------server init------------------------------------");
        this.serverBootstrap = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        try {
            this.serverBootstrap.group(this.bossGroup, this.workerGroup);
            this.serverBootstrap.channel(NioServerSocketChannel.class);

            this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decoder", new StringDecoder());
                    ch.pipeline().addLast("encoder", new StringEncoder());
                    ch.pipeline().addLast("handler", new ServerHandler());
                }
            });

            this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 2048);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            this.channel = this.serverBootstrap.bind(Integer.parseInt(this.port)).sync();
            System.out.println("port:" + port + " start..............");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void stop() {
        if (this.channel != null) {
            this.channel.channel().close();
            this.channel = null;
        }
    }
}
