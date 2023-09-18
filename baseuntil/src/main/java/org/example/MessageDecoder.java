package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class MessageDecoder extends ByteToMessageDecoder {

    private int length = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        if (length == 0) {
            length = in.readInt();
        }
        if (in.readableBytes() < length) {
            return;
        }
        byte[] content = new byte[length];
        in.readBytes(content);
        MessageProtocol messageProtocol = MessageProtocol.builder().length(length).content(content).build();
        out.add(messageProtocol);
        length = 0;
    }
}
