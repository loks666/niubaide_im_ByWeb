package com.niubaide.im.common;


import com.niubaide.im.dao.GroupInfoDao;
import com.niubaide.im.dao.UserInfoDao;
import com.niubaide.im.web.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Scope("singleton")
@Slf4j
public class AppContext {
    private final WebSocketServer webSocketServer;
    private final UserInfoDao userInfoDao;
    private final GroupInfoDao groupDao;

    private Thread nettyThread;

    public AppContext(WebSocketServer webSocketServer, UserInfoDao userInfoDao, GroupInfoDao groupDao) {
        this.webSocketServer = webSocketServer;
        this.userInfoDao = userInfoDao;
        this.groupDao = groupDao;
    }

    /**
     * 描述：Tomcat加载完ApplicationContext-main和netty文件后：
     * 1. 启动Netty WebSocket服务器；
     * 2. 加载用户数据；
     * 3. 加载用户交流群数据。
     */
    @PostConstruct
    public void init() {
        nettyThread = new Thread(webSocketServer);
        log.info("开启独立线程，启动Netty WebSocket服务器...");
        nettyThread.start();
        log.info("加载用户数据...");
        userInfoDao.loadUserInfo();
        log.info("加载用户交流群数据...");
        groupDao.loadGroupInfo();
    }

    /**
     * 描述：Tomcat服务器关闭前需要手动关闭Netty Websocket相关资源，否则会造成内存泄漏。
     * 1. 释放Netty Websocket相关连接；
     * 2. 关闭Netty Websocket服务器线程。（强行关闭，是否有必要？）
     */
    @SuppressWarnings("deprecation")
    @PreDestroy
    public void close() {
        log.info("正在释放Netty Websocket相关连接...");
        webSocketServer.close();
        log.info("正在关闭Netty Websocket服务器线程...");
        nettyThread.stop();
        log.info("系统成功关闭！");
    }
}
