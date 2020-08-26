package com.lwh.helloword;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import io.netty.util.ReferenceCountUtil;
import org.junit.jupiter.api.*;

import java.util.List;

public class EmbededChannelTest {


    @Test
    public void testInbound() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new LimiDecode());
        assertTrue(embeddedChannel.writeInbound(input.readBytes(3)));
        try {
            embeddedChannel.writeInbound(input.readBytes(4));
            fail();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        assertTrue(embeddedChannel.writeInbound(input.readBytes(3)));
        assertTrue(embeddedChannel.finish());

        ByteBuf read = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.readSlice(3), read);

        read = (ByteBuf) embeddedChannel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3), read);

        read.release();
        assertNull(embeddedChannel.readInbound());

        buffer.release();

        buffer = Unpooled.buffer();

        buffer.retain(10);

        buffer.release();
        buffer.release();
        ReferenceCountUtil.release(buffer);

        System.out.println("buffer = " + buffer.refCnt());
    }

    private static class LimiDecode extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            int count = in.readableBytes(); // 获取可读取字节数量
            if (count > 3) {
                in.clear();// discard the bytes
                throw new RuntimeException("字节太长！");
            }
            ByteBuf byteBuf = in.readBytes(count);
            out.add(byteBuf);
        }
    }
}
