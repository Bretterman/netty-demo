package com.lwh.helloword.udp.broadcast;

import com.lwh.helloword.udp.broadcast.encode.LogStringEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

public class LogStringBroadcaster {
    private Bootstrap bootstrap;
    private EventLoopGroup eventExecutors;

    public LogStringBroadcaster() {
        bootstrap = new Bootstrap();
        eventExecutors = new NioEventLoopGroup();
        bootstrap.group(eventExecutors)
                .channel(NioDatagramChannel.class)
                .handler(new LogStringEncoder(new InetSocketAddress("255.255.255.255", 8888)))
                .option(ChannelOption.SO_BROADCAST, true); //设置套接字选项
    }

    public void run() throws InterruptedException {
        Channel channel = bootstrap.bind(0).sync().channel();
        System.out.println("开始广播！");
        for (int i = 0; i < 50; i++) {
            ChannelFuture channelFuture = channel.writeAndFlush("-----------" + i + "");
//            channelFuture.addListener(x->{
//                if (!x.isSuccess()){
//                    x.cause().printStackTrace();
//                }
//            });
        }
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER);
        System.out.println("广播完毕！");
    }

    public void stop() {
        eventExecutors.shutdownGracefully();
    }

    public static void main(String[] args) {
        LogStringBroadcaster broadcaster = new LogStringBroadcaster();
        try {
            broadcaster.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            broadcaster.stop();
        }
    }
}
