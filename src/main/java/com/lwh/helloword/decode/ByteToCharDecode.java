package com.lwh.helloword.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ByteToCharDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String s = in.readBytes(in.readableBytes()).toString(Charset.defaultCharset());
        System.out.println("ByteToCharDecode in = " + s + " ByteBuf: " + in.toString());
        char[] chars = s.toCharArray();
        for (char c : chars) {
            out.add(c);
        }
    }
}
