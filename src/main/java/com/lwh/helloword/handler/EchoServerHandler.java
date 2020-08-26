package com.lwh.helloword.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    AttributeKey<Integer> id = AttributeKey.newInstance("ID");

    /**
     * 对于每个传入的消息都要调用
     *
     * @param ctx 上下文对象
     * @param msg 此次的消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // super.channelRead(ctx, msg);
        Attribute<Integer> attr = ctx.channel().attr(id);
        System.out.println("attr = " + attr);
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.retain();
        System.out.println("Server received： " + byteBuf.toString(Charset.defaultCharset()));
        ctx.write(byteBuf); // 将接受到的消息, 写给发送者，而不冲刷出站消息
        ctx.fireChannelRead(msg);
    }

    /**
     * 通知 ChannelInboundHandler 最后一次对channelRead() 的调用是当前批量读取中的最后一条消息
     *
     * @param ctx 上下文对象
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //  super.channelReadComplete(ctx);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER); // 写出一个空缓存
        // .addListener(ChannelFutureListener.CLOSE); // 将未决消息冲刷到远程节点，并且关闭该Channel，（未决消息为目前暂存于ChannelOutBoundBuffer中的消息）
        ctx.fireChannelReadComplete(); // 调用下一个 ReadComplete
//        if (atomicInteger.incrementAndGet() < 10){
        // 重新从pipeline() 开始调用 ReadComplete()
//            ctx.channel().pipeline().fireChannelReadComplete();
//        }else{
//            atomicInteger.set(0);
//        }
//         ctx.channel().pipeline().fireChannelReadComplete();

    }

    /**
     * 在读取操作期间，有异常抛出时会调用
     *
     * @param ctx   上下文对象
     * @param cause 错误异常
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close(); // 关闭该Channel
    }
}
