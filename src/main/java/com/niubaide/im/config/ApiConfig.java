package com.niubaide.im.config;

import com.niubaide.im.web.websocket.WebSocketChildChannelHandler;
import com.niubaide.im.web.websocket.WebSocketServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApiConfig {

    @Value("${websocket.port}")
    private int port;

    /**
     * 用于处理客户端连接请求
     */
    @Bean
    public EventLoopGroup bossGroup(){
        return new NioEventLoopGroup();
    }

    /**
     * 用于处理客户端I/O操作
     */
    @Bean
    public EventLoopGroup workerGroup(){
        return new NioEventLoopGroup();
    }

    /**
     * 服务器启动引导类
     */
    @Bean
    public ServerBootstrap serverBootstrap(){
        return new ServerBootstrap();
    }

    /**
     * 自定义的Netty Websocket服务器
     */
    @Bean
    public WebSocketServer webSocketServer(){
        WebSocketServer server = new WebSocketServer();
        server.setPort(port);
        server.setChildChannelHandler(new WebSocketChildChannelHandler());
        return server;
    }
}

