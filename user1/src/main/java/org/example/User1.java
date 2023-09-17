package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.MessageProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class User1 {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(User1.class, args);
        @Cleanup("shutdownGracefully") EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(new ClientHandler());
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
        Channel channel = channelFuture.channel();
        System.out.println("欢迎进入聊天室，请输入文字消息进行聊天");
        //客户端输入信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            byte[] bytes = msg.getBytes(CharsetUtil.UTF_8);
            MessageProtocol messageProtocol = MessageProtocol.builder().length(bytes.length).content(bytes).build();
            //通过channel发送到服务器端
            channel.writeAndFlush(messageProtocol);
        }
        channelFuture.channel().closeFuture().sync();
    }
}
