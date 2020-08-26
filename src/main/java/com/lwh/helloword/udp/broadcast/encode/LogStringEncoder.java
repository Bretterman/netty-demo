package com.lwh.helloword.udp.broadcast.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LogStringEncoder extends MessageToMessageEncoder<String> {
    private InetSocketAddress remoteAddress;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public LogStringEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer(msg.length());

        buffer.writeBytes(msg.getBytes());

        if (atomicInteger.getAndIncrement() % 2 == 0) {
            out.add(new DatagramPacket(buffer, new InetSocketAddress("255.255.255.255", 9999))); // 将封包写出
        } else {
            out.add(new DatagramPacket(buffer, remoteAddress)); // 将封包写出
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
