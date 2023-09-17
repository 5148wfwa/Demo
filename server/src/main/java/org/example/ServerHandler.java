package org.example;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) {
        String content = new String(msg.getContent(), CharsetUtil.UTF_8);
        Channel channel = ctx.channel();

        String myContent = "[ 我 ] 发送了消息：" + content;
        byte[] myContentBytes = myContent.getBytes(CharsetUtil.UTF_8);
        MessageProtocol myMessageProtocol = MessageProtocol.builder().length(myContentBytes.length).content(myContentBytes).build();

        String otherContent = channel.remoteAddress() + " 发送了消息：" + content;
        byte[] otherContentBytes = otherContent.getBytes(CharsetUtil.UTF_8);
        MessageProtocol otherMessageProtocol = MessageProtocol.builder().length(otherContentBytes.length).content(otherContentBytes).build();

        CHANNEL_GROUP.forEach(ch -> {
            MessageProtocol messageProtocol = (ch == channel) ? myMessageProtocol : otherMessageProtocol;
            ch.writeAndFlush(messageProtocol);
        });
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Channel channel = ctx.channel();
        SocketAddress address = channel.remoteAddress();
        String content =  address + " 上线了 " + sdf.format(new Date());
        byte[] bytes = content.getBytes(CharsetUtil.UTF_8);
        MessageProtocol messageProtocol = MessageProtocol.builder().length(bytes.length).content(bytes).build();
        CHANNEL_GROUP.writeAndFlush(messageProtocol);
        CHANNEL_GROUP.add(channel);
        System.out.println("当前客户端数量：" + CHANNEL_GROUP.size());
        System.out.println(address + " 上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        SocketAddress address = channel.remoteAddress();
        String content = "[ 客户端 " + address + " ] 下线了";
        byte[] bytes = content.getBytes(CharsetUtil.UTF_8);
        MessageProtocol messageProtocol = MessageProtocol.builder().length(bytes.length).content(bytes).build();
        CHANNEL_GROUP.writeAndFlush(messageProtocol);
        System.out.println("当前客户端数量：" + CHANNEL_GROUP.size());
        System.out.println(address + " 下线了");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
