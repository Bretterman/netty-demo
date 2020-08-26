package com.lwh.helloword.udp.monitor.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

public class LogStringDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        InetSocketAddress sender = msg.sender();

        ByteBuf content = msg.content();

        String s = content.toString(Charset.defaultCharset());

        out.add(s);
    }
}
