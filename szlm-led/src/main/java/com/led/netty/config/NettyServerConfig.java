package com.led.netty.config;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.Bootstrap;
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

//@Component
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



	//tcp

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

	 public class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
		 @Override
		 protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
			logger.info("接收UDP数据: msg:{},ctx:{}",msg,ctx.channel());
			//PooledUnsafeDirectByteBuf
			 //PooledUnsafeDirectByteBuf a;
			 ByteBuf buffer = msg.content();
			 int size = buffer.readableBytes();
			 byte[] datas = new byte[size];
			 buffer.readBytes(datas);
			 System.out.println(msg.content().getClass());
			 String req = msg.content().toString(CharsetUtil.UTF_8);
			 System.out.println(req);

			 String hexDump = ByteBufUtil.hexDump(datas).toUpperCase();
			 logger.info("收到UDP完整数据包...{}",hexDump);
			 StringBuilder _builder = new StringBuilder();
			 for(int i =0;i<hexDump.length();i++){
				 if(i%2==0) _builder.append(" ");
				 _builder.append(hexDump.charAt(i));
			 }
			 logger.error("Package:{}",_builder.toString());

		 }
	 }
}


