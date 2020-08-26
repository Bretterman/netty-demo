package com.lwh.helloword.udp.monitor;

import com.lwh.helloword.udp.monitor.decoder.LogStringDecoder;
import com.lwh.helloword.udp.monitor.handler.LogStringInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

public class LogStringMonitor {
    private Bootstrap bootstrap;
    private EventLoopGroup eventExecutors;


    public LogStringMonitor() {
        bootstrap = new Bootstrap();
        eventExecutors = new NioEventLoopGroup();
        bootstrap.group(eventExecutors)
                .channel(NioDatagramChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LogStringDecoder());
                        pipeline.addLast(new LogStringInboundHandler());

                    }
                })
                .option(ChannelOption.SO_BROADCAST, true)
                .localAddress(new InetSocketAddress(9999)); //设置套接字选项
    }

    public void run() throws InterruptedException {
        Channel channel = bootstrap.bind().syncUninterruptibly().channel();
        System.out.println("监听者开始监听！");
        channel.closeFuture().sync();
    }

    public void stop() {
        eventExecutors.shutdownGracefully();
    }

    public static void main(String[] args) {
        LogStringMonitor logStringMonitor = new LogStringMonitor();

        try {
            logStringMonitor.run();
            System.out.println("监听者结束监听！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logStringMonitor.stop();
        }
    }
}
