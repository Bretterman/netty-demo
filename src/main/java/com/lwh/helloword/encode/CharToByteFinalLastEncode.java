package com.lwh.helloword.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToByteEncoder;

public class CharToByteFinalLastEncode extends MessageToByteEncoder<Character> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Character msg, ByteBuf out) throws Exception {
        System.out.println("CharToByteFinalLastEncode write char:" + msg);
        out.writeChar(msg);
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();
    }
}
