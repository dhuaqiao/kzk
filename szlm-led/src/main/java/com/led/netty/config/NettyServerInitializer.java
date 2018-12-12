package com.led.netty.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;

import com.led.netty.codec.HardWareDecoder;
import com.led.netty.codec.HardWareEncoder;
import com.led.netty.handler.HardWareHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

@SpringBootApplication(scanBasePackages = { "com.led.netty.config", "com.led.netty" })
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final Logger logger = LoggerFactory.getLogger(NettyServerConfig.class);

	@Autowired
	private NettyServerConfig nettyServerConfig;

	private SslContext sslCtx;

	/**
	 * 启动
	 * 
	 * @throws InterruptedException
	 * @throws SSLException
	 */
	@PostConstruct
	public void start() throws InterruptedException, SSLException {
		nettyServerConfig.start();
	}

	@PreDestroy
	public void stop() {
		nettyServerConfig.stop();
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		logger.info(" initChannel ");
		System.out.println(" initChannel ");
		ChannelPipeline pipeline = ch.pipeline();
		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		pipeline.addLast(new HardWareDecoder()).addLast(new HardWareEncoder()).addLast(new HardWareHandler());
	}
}
