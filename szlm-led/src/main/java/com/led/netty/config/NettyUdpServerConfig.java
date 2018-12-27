package com.led.netty.config;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;

@Component
public class NettyUdpServerConfig {

	private static final Logger logger = LoggerFactory.getLogger(NettyUdpServerConfig.class);

	private Channel channel;
	
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
	private NettyUdpServerInitializer nettyUdpServerInitializer;

	/**
	 * 启动
	 * 
	 * @throws InterruptedException
	 * @throws SSLException
	 */
	public void start() throws InterruptedException, SSLException {
		logger.info("begin to start netty udp server");
		workerGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup).channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(nettyUdpServerInitializer);

		channel = bootstrap.bind(host, port).sync().channel();
		logger.info("info Netty websocket server listening on port " + port + " and ready for connections...");
		logger.warn("warn Netty websocket server listening on port " + port + " and ready for connections...");
		logger.debug("debug Netty websocket server listening on port " + port + " and ready for connections...");
		logger.error("error Netty websocket server listening on port " + port + " and ready for connections...");
		logger.trace("trace Netty websocket server listening on port " + port + " and ready for connections...");
	}



	//tcp

	public void stop() {
		logger.info("destroy websocket server resources");
		if (null == channel) {
			logger.error("server channel is null");
		}
		workerGroup.shutdownGracefully();
		channel.closeFuture().syncUninterruptibly();
		workerGroup = null;
		channel = null;
	}
}


