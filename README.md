# 互联网实时聊天系统 (Spring + Netty + Websocket + docker)


- 本项目重构于[Kanarienvogels/Chatroom](https://github.com/Kanarienvogels/Chatroom "Kanarienvogels/Chatroom")即时通讯系统
<!-- PROJECT SHIELDS -->

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<!-- PROJECT LOGO -->  
<br />

<p align="center">
  <a href="https://github.com/loks666/niubaide_im_ByWeb/">
    <img src="http://tva1.sinaimg.cn/large/ed264f1bgy1h46fyw6hgjj20dw0azaas.jpg" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">niubaide_im_ByWeb</h3>
  <p align="center">
    一个"牛掰的"即时通讯项目！
    <br />
    <a href="https://github.com/loks666/niubaide_im_ByWeb"><strong>探索本项目的文档 »</strong></a>
    <br />
    <br />
    <a href="https://github.com/loks666/niubaide_im_ByWeb">查看Demo</a>
    ·
    <a href="https://github.com/loks666/niubaide_im_ByWeb/issues">报告Bug</a>
    ·
    <a href="https://github.com/loks666/niubaide_im_ByWeb/issues">提出新特性</a>
  </p>

</p>


## 0. 前言
在之前在学习netty的时候，找到了Kanarienvogels下的即时通讯，最终受到他的启发，构建了一个即时通讯系统[公司内的需求] 
* 之前的项目  
[loks666/niubaide_im](https://github.com/loks666/niubaide_im)

但是由于随着技术发展，Kanarienvogels采用的SSM框架显得有些过时，再加上[niubaide_im](https://github.com/loks666/niubaide_im)使用uniapp构建的前端，对于学习或者希望尽快看到效果的朋友不是很友好，所以有了重构的想法，最终经过一段时间的不懈努力。。。  
本项目就横空出世啦！！！  
- 它改进了什么？
> 采用SpringBoot + thymeleaf 开发，docker容器化部署，极速开发，极其部署
- 演示地址
> [牛掰的聊天室 (http://im.superxiang.com)](http://im.superxiang.com)
- 关于学习netty的书籍
> [Essential Netty in Action 《Netty 实战(精髓)》](https://legacy.gitbook.com/book/waylau/essential-netty-in-action/details)  
- 关联项目区别
> [使用uniapp + springBoot构建的即使通讯系统](https://github.com/loks666/niubaide_im)  
> 主要区别为本项目目前未使用数据库，在niubaide_im已经实现数据库，同时含有用户添加功能(本项目目前为后台内置好用户信息)

## 1. 技术准备（自己熟悉即可）
* IDE：IDEA 2022
* JDK版本：1.8
* 浏览器：谷歌浏览器
* 涉及技术：
  * Netty 4
  * WebSocket + HTTP
  * SpringBoot + thymeleaf
  * JQuery
  * Bootstrap 3 + Bootstrap-fileinput
  * Maven 3.5
  * Docker 20.10.17

## 2. 整体说明
### 2.1 设计思想
基础沿用 [Kanarienvogels/Chatroom](https://github.com/Kanarienvogels/Chatroom) 项目设计，功能未做明显改动

### 2.2 系统结构
  HTML > 构建websocket对象 > 与服务器请求连接 > 开发发送消息
### 2.3 项目结构
项目后端结构：
![项目结构（后端）](http://tva1.sinaimg.cn/large/ed264f1bgy1h46idstqt1j20fb120dmr.jpg)

项目前端结构：
![项目结构（前端）](http://tva1.sinaimg.cn/large/ed264f1bgy1h46idsupr0j20ax0k4tbh.jpg)

### 2.4 系统功能模块
系统只包括两个模块：登录模块和聊天管理模块。

* 登录模块：既然作为一个系统，那么登录的角色认证是必不可少的，这里使用简单、传统的Session方式维持登录状态，当然也有对应的注销功能，但这里的注销除了清空Session对象，还要释放WebSocket连接，否则造成内存泄露。
* 聊天管理模块：系统的核心模块，这部分主要使用Netty框架实现，功能包括信息、文件的单条和多条发送，也支持表情发送。
* 其他模块：如好友管理模块、聊天记录管理、注册模块等，暂时未实现，后续有时间会考虑设计数据库，与传统的开发方式类似。


由于本系统涉及多个用户状态，有必要进行说明，下面给出本系统的用户状态转换图。
![状态转换图](https://kanarien-1254133416.cos.ap-guangzhou.myqcloud.com/Image%20Bed/%E8%81%8A%E5%A4%A9%E5%AE%A4%20-%20%E7%94%A8%E6%88%B7%E7%8A%B6%E6%80%81%E8%BD%AC%E6%8D%A2%E5%9B%BE.png)

### 2.6 系统界面
系统聊天界面如下：
![聊天界面](http://tva1.sinaimg.cn/large/ed264f1bgy1h46in33g0pj20th0kwn2j.jpg)

## 3. 核心编码
这里只说明需要注意的地方，详细的请看源码

### 3.1 Netty服务器启动与关闭
**当关闭Tomcat服务器时，也要释放Netty相关资源，否则会造成内存泄漏**，关闭方法如下面的``close()``，如果只是使用``shutdownGracefully()``方法的话，关闭时会报内存泄露Memory Leak异常（但IDE可能来不及输出到控制台）
```Java
/**
 * 描述: Netty WebSocket服务器
 *      使用独立的线程启动
 * @author Kanarien
 * @version 1.0
 * @date 2018年5月18日 上午11:22:51
 */
public class WebSocketServer implements Runnable{

        /**
	 * 描述：启动Netty Websocket服务器
	 */
	public void build() {
	    // 略，详细请看源码
	}
     
      /**
	 * 描述：关闭Netty Websocket服务器，主要是释放连接
	 *     连接包括：服务器连接serverChannel，
	 *     客户端TCP处理连接bossGroup，
	 *     客户端I/O操作连接workerGroup
	 *
	 *     若只使用
	 *         bossGroupFuture = bossGroup.shutdownGracefully();
	 *         workerGroupFuture = workerGroup.shutdownGracefully();
	 *     会造成内存泄漏。
	 */
	public void close(){
	    serverChannelFuture.channel().close();
		Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();

        try {
            bossGroupFuture.await();
            workerGroupFuture.await();
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
        }
	}

}
```
改进点：sync方法会引起netty死锁问题，解决方案为升级netty版本，使用licenser监听类判断是否发生死锁，从而进行进一步业务处理

## 4. 效果及操作演示
### 4.1 登录操作
登录入口为：http://localhost:5001 或 http://localhost:5001/toLogin  
如果需要部署到服务器，同时需要开放3001端口【websocket端口】
当前系统用户固定为10个，群组1个，包括10人用户。
* 用户1  用户名：001  密码：001
* 用户2  用户名：002  密码：002
* ······
* 用户9  用户名：010  密码：010

![登录入口](http://tva1.sinaimg.cn/large/ed264f1bgy1h46jexiv81j20iu0gv795.jpg)

### 4.2 聊天演示
![聊天演示](http://tva1.sinaimg.cn/large/ed264f1bgy1h46jtvn8qng20rd0jw4lj.gif)

### 4.3 在线体验地址
[牛掰的聊天室](http://im.superxiang.com/)  

---
### 注意事项  ##
- docker构建镜像时，首先需要打开服务器docker服务的tcp端口，同时需要改动pom文件<docker.host>标签,将ip与端口改为自己的docker服务tcp端口，再执行mvn package命令，打好docker镜像包后部署到服务器发布就可以了
- docker默认2375端口，容易被黑客攻击，建议自定义其他的端口
- springBoot jar包启动时，项目打包为niuibaide_im_web.jar,执行如下命令即可  
  `java -jar niuibaide_im_web.jar `
---
### 关联项目
- 构建来自于：https://github.com/Kanarienvogels/Chatroom
- uni app的即时通讯：https://github.com/loks666/niubaide_im
- 个人博客：https://www.superxiang.com/
- halo博客系统[一款现代化的开源博客/CMS系统]：https://halo.run/
<!-- links -->

[your-project-path]:loks666/niubaide_im_ByWeb

[contributors-shield]: https://img.shields.io/github/contributors/loks666/niubaide_im_ByWeb.svg?style=flat-square

[contributors-url]: https://github.com/loks666/niubaide_im_ByWeb/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/loks666/niubaide_im_ByWeb.svg?style=flat-square

[forks-url]: https://github.com/loks666/niubaide_im_ByWeb/network/members

[stars-shield]: https://img.shields.io/github/stars/loks666/niubaide_im_ByWeb.svg?style=flat-square

[stars-url]: https://github.com/loks666/niubaide_im_ByWeb/stargazers

[issues-shield]: https://img.shields.io/github/issues/loks666/niubaide_im_ByWeb.svg?style=flat-square

[issues-url]: https://img.shields.io/github/issues/loks666/niubaide_im_ByWeb.svg

[license-shield]: https://img.shields.io/github/license/loks666/niubaide_im_ByWeb.svg?style=flat-square

[license-url]: https://github.com/loks666/niubaide_im_ByWeb/blob/master/LICENSE.txt


> Copyright © 2018, www.github.com/loks666, All Rights Reserved
