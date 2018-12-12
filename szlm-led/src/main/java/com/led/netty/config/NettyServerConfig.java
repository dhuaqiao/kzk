package com.led.netty.config;


import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Component
public class NettyServerConfig{

	private static final Logger logger = LoggerFactory.getLogger(NettyServerConfig.class);

	private Channel channel;
	
	private EventLoopGroup bossGroup;
	
	private EventLoopGroup workerGroup;
	
	@Value("${rpcServer.host:0.0.0.0}")
	private String host;

	@Value("${rpcServer.ioThreadNum:5}")
	private int ioThreadNum;
	// 内核为此套接口排队的最大连接个数，对于给定的监听套接口，内核要维护两个队列，未链接队列和已连接队列大小总和最大值

	@Value("${rpcServer.backlog:1024}")
	private int backlog;

	@Value("${rpcServer.port:8686}")
	private int port;
	
	@Autowired
	private NettyServerInitializer nettyServerInitializer;

	/**
	 * 启动
	 * 
	 * @throws InterruptedException
	 * @throws SSLException
	 */
	public void start() throws InterruptedException, SSLException {
		logger.info("begin to start netty server");
		// File keyCertChainFile = new File(keyCertChainFilePath);
		// File keyFile = new File(keyFilePath);
		// SslContext sslCtx =
		// SslContextBuilder.forServer(keyCertChainFile,keyFile).build();
		bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
		workerGroup = new NioEventLoopGroup();
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
				.childHandler(nettyServerInitializer) // (4)
				.option(ChannelOption.SO_BACKLOG, 128) // (5)
				.childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
				.childOption(ChannelOption.SO_REUSEADDR, true);// 允许重用端口
		channel = serverBootstrap.bind(host, port).sync().channel();
		logger.info("info Netty websocket server listening on port " + port + " and ready for connections...");
		logger.warn("warn Netty websocket server listening on port " + port + " and ready for connections...");
		logger.debug("debug Netty websocket server listening on port " + port + " and ready for connections...");
		logger.error("error Netty websocket server listening on port " + port + " and ready for connections...");
		logger.trace("trace Netty websocket server listening on port " + port + " and ready for connections...");

	}

	public void stop() {
		logger.info("destroy websocket server resources");
		if (null == channel) {
			logger.error("server channel is null");
		}
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		channel.closeFuture().syncUninterruptibly();
		bossGroup = null;
		workerGroup = null;
		channel = null;
	}

	
}
