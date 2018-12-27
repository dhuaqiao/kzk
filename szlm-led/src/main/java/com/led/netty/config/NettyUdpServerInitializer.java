package com.led.netty.config;

import com.led.netty.codec.HardWareUdpDecoder;
import com.led.netty.codec.HardWareUdpEncoder;
import com.led.netty.handler.HardWareUdpHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;

@SpringBootApplication(scanBasePackages = { "com.led.netty.config", "com.led.netty" })
public class NettyUdpServerInitializer extends ChannelInitializer<DatagramChannel> {

	private static final Logger logger = LoggerFactory.getLogger(NettyServerConfig.class);

	@Autowired
	private NettyUdpServerConfig nettyUdpServerConfig;

	/**
	 * 启动
	 * 
	 * @throws InterruptedException
	 * @throws SSLException
	 */
	@PostConstruct
	public void start() throws InterruptedException, SSLException {
		nettyUdpServerConfig.start();
	}

	@PreDestroy
	public void stop() {
		nettyUdpServerConfig.stop();
	}

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		logger.info(" udpServer initChannel ");
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HardWareUdpDecoder()).addLast(new HardWareUdpEncoder()).addLast(new HardWareUdpHandler());
	}
}
