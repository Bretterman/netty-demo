package com.lwh.helloword.server;

import com.lwh.helloword.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

public class EchoClient {
    private final String host;
    private final int port;



    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
//        Unpooled.unreleasableBuffer(Unpooled.copiedBuffer())
        try {
            Bootstrap bootstrap = new Bootstrap();
            AttributeKey<Integer> id = AttributeKey.valueOf("ID");
            bootstrap.remoteAddress(new InetSocketAddress(host, port))
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // super.channelActive(ctx);

                        }
                    });
            bootstrap.attr(id, 123456789);

            ChannelFuture sync = bootstrap.connect().sync();
            sync.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
