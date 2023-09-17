package org.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
        @Cleanup("shutdownGracefully") EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        @Cleanup("shutdownGracefully") EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(8010)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(new ServerHandler());
                    }
                });
        ChannelFuture cf = bootstrap.bind(9000).sync();
       System.out.println("聊天室server启动成功");
        cf.channel().closeFuture().sync();
    }
}
