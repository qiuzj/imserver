package cn.javaee.im;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.Date;

public class IMApplication {

	/** WebSocket端口号 */
	private static final int WEBSOCKET_PORT = 8082;

	public static void main(String[] args) {
		IMApplication application = new IMApplication();
		application.startNetty();
	}

	/**
	 * 启动Netty监听
	 */
	private void startNetty() {
		startTCP();
		startWebSocket();
	}

	/**
	 * 启动TCP监听
	 */
	private void startTCP() {

	}

	/**
	 * 启动WebSocket监听
	 */
	private void startWebSocket() {
		NioEventLoopGroup boosGroup = new NioEventLoopGroup(); // 监听连接事件线程组
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // 监听IO事件线程组

		final ServerBootstrap serverBootstrap = new ServerBootstrap(); // 服务端启动器
		serverBootstrap
				.group(boosGroup, workerGroup) // 设置线程组
				.channel(NioServerSocketChannel.class) // 设置Channel类型为Nio
				.option(ChannelOption.SO_BACKLOG, 1024) // TCP连接队列长度
				.childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接
				.childOption(ChannelOption.TCP_NODELAY, true) // 关闭Nagle算法，小包即时发送
				.childHandler(new ChannelInitializer<NioSocketChannel>() { // 添加客户端连接的Pipeline处理链
					protected void initChannel(NioSocketChannel ch) {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new HttpServerCodec());
						pipeline.addLast(new HttpObjectAggregator(65536)); // 64*1024=64KB
//						pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
						pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true));
						pipeline.addLast(new WebSocketHandler());
					}
				});

		bindWebSocket(serverBootstrap, WEBSOCKET_PORT);
	}

	private static void bindWebSocket(final ServerBootstrap serverBootstrap, final int port) {
		serverBootstrap.bind(port).addListener(future -> {
			if (future.isSuccess()) {
				System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
			} else {
				System.err.println("端口[" + port + "]绑定失败!");
			}
		});
	}

}
