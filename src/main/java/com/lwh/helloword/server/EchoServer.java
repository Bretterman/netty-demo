package com.lwh.helloword.server;

import com.lwh.helloword.decode.ByteToCharDecode;
import com.lwh.helloword.encode.*;
import com.lwh.helloword.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class EchoServer {
    private EventExecutorGroup eventExecutors = new DefaultEventExecutor(Executors.newSingleThreadExecutor());

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup(); // 接受处理线程组
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class) // 制定信道类型
                    .group(group) // 制定管理组
                    .localAddress(new InetSocketAddress(this.port)) // 制定端口

                    .childHandler(new ChannelInitializer<SocketChannel>() { // 初始化每一个新连接的的子 channel
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new CharToByteEncode());
                            channel.pipeline().addLast(new CharToByteLastEncode());
                            channel.pipeline().addLast(new ByteArrayToBinaryEncoder());
                            channel.pipeline().addLast(new StringToByteEncode());
                            channel.pipeline().addLast(serverHandler);
                            channel.pipeline().addLast(new ByteToCharDecode());
                            channel.pipeline().addLast(eventExecutors, new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    // super.channelRead(ctx, msg);
                                    System.out.println("msg = " + msg.toString());
//                                    Thread.sleep(1000000L); 会将后续全部卡死
                                    if (!(msg instanceof ByteBuf)){
                                        return;
                                    }
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    // byteBuf.retain();
                                    byte[] arrays;
                                    if (byteBuf.hasArray()) {
                                        arrays = byteBuf.array();
                                    } else {
                                        int index = byteBuf.readableBytes();
                                        byte[] bytes = new byte[index];
                                        byteBuf.getBytes(byteBuf.readerIndex(), bytes);
                                        arrays = bytes;
                                    }
                                    System.out.println("msg = " + byteBuf.toString(Charset.defaultCharset()));
                                    // System.out.println("msg = " + new String(arrays));
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("BBBBBBBBBBBBBBBBBBBBB-11111111111111111111111!" + Thread.currentThread().getName(), Charset.defaultCharset())); // 发送一条消息
                                    ctx.fireChannelRead(msg);
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    // super.channelReadComplete(ctx);
                                    ctx.writeAndFlush(new byte[10]);
                                    ctx.writeAndFlush('C');
                                    ctx.writeAndFlush("sssssssssssssssssssssssssaaaAq");
                                    Channel channel = ctx.channel();
                                    ChannelPipeline pipeline = ctx.pipeline();
                                    ctx.channel().writeAndFlush('A');
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("AAAAAAAAAAAAAAAAAAAAA-00000000000000000000!" + Thread.currentThread().getName(), Charset.defaultCharset())); // 发送一条消息
                                }
                            });
                            channel.pipeline().addLast(new CharToByteFinalLastEncode());
//                            ch.pipeline().addLast(new StringToByteEncode());


                        }
                    });
            ChannelFuture sync = bootstrap.bind().sync();
            System.out.println("success start echoServer !");
            sync.channel().closeFuture().sync(); // 服务器信道关闭那就关闭
        } finally {
            group.shutdownGracefully().sync(); // 关闭所有线程
        }


    }
}
