package com.niubaide.im.config;

import com.niubaide.im.web.websocket.WebSocketChildChannelHandler;
import com.niubaide.im.web.websocket.WebSocketServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


@Configuration
public class ApiConfig {

    @Value("${websocket.port}")
    private int port;

    public ApiConfig(WebSocketChildChannelHandler webSocketChildChannelHandler) {
        this.webSocketChildChannelHandler = webSocketChildChannelHandler;
    }

    /**
     * 用于处理客户端连接请求
     */
    @Bean
    public EventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    /**
     * 用于处理客户端I/O操作
     */
    @Bean
    public EventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    /**
     * 服务器启动引导类
     */
    @Bean
    public ServerBootstrap serverBootstrap() {
        return new ServerBootstrap();
    }

    private final WebSocketChildChannelHandler webSocketChildChannelHandler;

    /**
     * 自定义的Netty Websocket服务器
     */
    @Bean
    public WebSocketServer webSocketServer() {
        WebSocketServer server = new WebSocketServer();
        server.setPort(port);
        server.setChildChannelHandler(webSocketChildChannelHandler);
        return server;
    }

    @Bean
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("defaultEncoding");
        commonsMultipartResolver.setMaxUploadSize(10485760);
        commonsMultipartResolver.setMaxInMemorySize(40960);
        return commonsMultipartResolver;
    }

}

