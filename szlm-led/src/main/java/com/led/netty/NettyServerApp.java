package com.led.netty;

import com.led.netty.config.NettyUdpServerConfig;
import com.led.netty.utils.PackDataUtils;
import io.netty.buffer.ByteBufUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class NettyServerApp {

	public static void main(String[] args) {
		/***/

		System.out.println(new String(ByteBufUtil.decodeHexDump("303030303031")));


		SpringApplication app = new SpringApplication(Initializer.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.setRegisterShutdownHook(true);
		app.run(args);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				System.out.println(" java ...");
			}
		});
	}

	@SpringBootApplication(scanBasePackages = { "com.led.netty.config", "com.led.netty" })
	static class Initializer{
		@Autowired
		private NettyUdpServerConfig nettyUdpServerConfig;

		/**
		 * 启动
		 *
		 * @throws InterruptedException
		 */
		@PostConstruct
		public void start() throws InterruptedException {
			nettyUdpServerConfig.start();
		}

		@PreDestroy
		public void stop() {
			nettyUdpServerConfig.stop();
		}
	}
}
