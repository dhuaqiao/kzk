package com.led.netty.config;


import com.led.netty.handler.HardWareUdpHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyUdpServerConfig {

	private static final Logger logger = LoggerFactory.getLogger(NettyUdpServerConfig.class);

	private Channel channelLocal;
	
	private EventLoopGroup workerGroup;
	
	@Value("${udpServer.host:0.0.0.0}")
	private String host;

	@Value("${udpServer.ioThreadNum:5}")
	private int ioThreadNum;
	// 内核为此套接口排队的最大连接个数，对于给定的监听套接口，内核要维护两个队列，未链接队列和已连接队列大小总和最大值

	@Value("${udpServer.backlog:1024}")
	private int backlog;

	@Value("${udpServer.port:8686}")
	private int port;
	
	@Autowired
	private NettyUdpServerInitializer nettyUdpServerInitializer;

	@Autowired
	private HardWareUdpHandler hardWareUdpHandler;

	/**
	 * 启动
	 * 
     * @throws Exception
	 */
	public void start() throws Exception {
		logger.info("begin to start netty udp server");
		workerGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup).channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(nettyUdpServerInitializer);
		channelLocal = bootstrap.bind(host, port).sync().channel();
		hardWareUdpHandler.setChannelLocal(channelLocal);
		logger.info("info Netty udp server listening on port " + port + " and ready for connections...");
	}



	//tcp


	public void stop() {
		logger.info("destroy udp server resources");
		HardWareUdpHandler.isRunCheckUdpClient = false;//stop check...
		if (null == channelLocal) {
			logger.error("server channel is null");
		}
		workerGroup.shutdownGracefully();
		channelLocal.closeFuture().syncUninterruptibly();
		workerGroup = null;
		channelLocal = null;
	}
}


