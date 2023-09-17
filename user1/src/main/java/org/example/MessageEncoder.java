package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.MessageProtocol;

/**
 * 发送包编码
 *
 * @author Robert Hou
 * @date 2020年05月02日 23:12
 **/
public class MessageEncoder extends MessageToByteEncoder<MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) {
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getContent());
    }
}
