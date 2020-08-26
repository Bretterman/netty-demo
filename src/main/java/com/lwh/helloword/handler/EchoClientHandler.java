package com.lwh.helloword.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    AttributeKey<Integer> id = AttributeKey.valueOf("ID");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // super.channelActive(ctx);
        ScheduledFuture<?> future = ctx.channel().eventLoop().scheduleWithFixedDelay(() -> {
            ctx.writeAndFlush(Unpooled.copiedBuffer("心跳打印！" + Thread.currentThread().getName(), Charset.defaultCharset())); // 发送一条消息
        }, 5, 3, TimeUnit.SECONDS);

        Attribute<Integer> attr = ctx.channel().attr(id);

        System.out.println("attr = " + attr);

        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks! " + Thread.currentThread().getName(), Charset.defaultCharset())); // 发送一条消息
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("Client received:  " + msg.toString(Charset.defaultCharset()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    public static void main(String[] args) {
        // 1.创建一个非池化的ByteBuf，大小为10个字节
        ByteBuf buf = Unpooled.buffer(10);
        System.out.println("buf.refCnt() = " + buf.refCnt());
        ByteBuf buf2 = Unpooled.buffer(10);
        System.out.println("原始ByteBuf为====================>" + buf.toString());
        System.out.println("1.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");
        // buf.release();
        // 2.写入一段内容
        byte[] bytes = {1, 2, 3, 4, 5};
        byte[] bytes2s = {6, 7, 8, 9, 10};
        buf.writeBytes(bytes);
        buf2.writeBytes(bytes2s);
        buf2.markReaderIndex();
        buf2.readBytes(buf);
        buf2.resetReaderIndex();
        int i = buf.readInt();
        System.out.println("i = " + i);
        buf.writeInt(100000);
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer(64);
        System.out.println("byteBuf.hasArray() = " + byteBuf.hasArray());
        byteBuf.writeInt(1000);
        System.out.println("byteBuf = " + byteBuf);
        System.out.println("ByteBuf中的内容为===============>" + byteBuf.readInt() + "\n");
        ByteBuf byteBuf1 = ByteBufAllocator.DEFAULT.directBuffer(10);
        System.out.println("byteBuf1.hasArray() = " + byteBuf1.hasArray());
//        int i = buf.readInt();
//        System.out.println("i = " + i);
        System.out.println("写入的bytes为====================>" + Arrays.toString(bytes));
        System.out.println("写入一段内容后ByteBuf为===========>" + buf.toString());
        System.out.println("2.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

//        System.out.println("byteBuf1 = " + buf2.refCnt());
//        buf2.readSlice(1);
//        System.out.println("byteBuf1 = " + buf2.refCnt());
        System.out.println("写入的bytes为====================>" + Arrays.toString(bytes2s));
        System.out.println("写入一段内容后ByteBuf为===========>" + buf2.toString());
        System.out.println("2.ByteBuf中的内容为===============>" + Arrays.toString(buf2.array()) + "\n");

        // 3.读取一段内容
        // buf.markReaderIndex();
        byte b1 = buf.readByte();
        byte b2 = buf.readByte();
        // buf.resetReaderIndex();
        System.out.println("读取的bytes为====================>" + Arrays.toString(new byte[]{b1, b2}));
        System.out.println("读取一段内容后ByteBuf为===========>" + buf.toString());
        System.out.println("3.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        // 4.将读取的内容丢弃
        buf.discardReadBytes();
        System.out.println("将读取的内容丢弃后ByteBuf为========>" + buf.toString());
        System.out.println("4.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        // 5.清空读写指针
        buf.clear();
        System.out.println("将读写指针清空后ByteBuf为==========>" + buf.toString());
        System.out.println("5.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        // 6.再次写入一段内容，比第一段内容少
        byte[] bytes2 = {1, 2, 3};
        buf.writeBytes(bytes2);
        System.out.println("写入的bytes为====================>" + Arrays.toString(bytes2));
        System.out.println("写入一段内容后ByteBuf为===========>" + buf.toString());
        System.out.println("6.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        // 7.将ByteBuf清零
        buf.setZero(0, buf.capacity());
        System.out.println("将内容清零后ByteBuf为==============>" + buf.toString());
        System.out.println("7.ByteBuf中的内容为================>" + Arrays.toString(buf.array()) + "\n");

        // 8.再次写入一段超过容量的内容
        byte[] bytes3 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        buf.writeBytes(bytes3);
        System.out.println("写入的bytes为====================>" + Arrays.toString(bytes3));
        System.out.println("写入一段内容后ByteBuf为===========>" + buf.toString());
        System.out.println("8.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");
    }
}
