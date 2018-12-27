package com.led.netty.config;

import com.led.netty.codec.HardWareUdpDecoder;
import com.led.netty.codec.HardWareUdpEncoder;
import com.led.netty.handler.HardWareUdpHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//@SpringBootApplication(scanBasePackages = { "com.led.netty.config", "com.led.netty" })
@Component
@Scope("prototype")
public class NettyUdpServerInitializer extends ChannelInitializer<DatagramChannel> {

	private static final Logger logger = LoggerFactory.getLogger(NettyUdpServerInitializer.class);

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		logger.info(" udpServer initChannel ");
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HardWareUdpDecoder()).addLast(new HardWareUdpEncoder()).addLast(new HardWareUdpHandler());
	}
}
