package org.trex.falcon.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.trex.falcon.rpc.codc.MessageDecoder;
import org.trex.falcon.rpc.codc.MessageEncoder;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private Map<String, ChannelFuture> channels;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public static Server instance;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private Server() {
        this.serverBootstrap = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.channels = new HashMap<String, ChannelFuture>();
        try {
            this.serverBootstrap.group(this.bossGroup, this.workerGroup);
            this.serverBootstrap.channel(NioServerSocketChannel.class);

            this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decoder", new MessageDecoder());
                    ch.pipeline().addLast("encoder", new MessageEncoder());
                    ch.pipeline().addLast("handler", new ServerHandler());
                }
            });

            this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 2048);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(int port) {
        if (!this.channels.containsKey(port + "")) {
            try {
                ChannelFuture channel = this.serverBootstrap.bind(port).sync();
                this.channels.put(port + "", channel);
                System.out.println("port:" + port + " start..............");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException("端口已经占用...");
        }

    }

    public void stop(int port) {
        if (this.channels.containsKey(port + "")) {
            ChannelFuture channel = this.channels.get(port + "");
            channel.channel().closeFuture();
            this.channels.remove(port + "");
            System.out.println(port + "    stop");
        } else {
            throw new RuntimeException("端口没有启动...");
        }
    }
}
