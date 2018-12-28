package com.led.netty;

import com.led.netty.config.NettyUdpServerConfig;
import com.led.netty.utils.PackDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class NettyServerApp {

	// 一共分为五个级别：DEBUG、INFO、WARN、ERROR和FATAL。这五个级别是有顺序的，DEBUG < INFO < WARN < ERROR < FATAL，
	//明白这一点很重要，这里Log4j有一个规则：假设设置了级别为P，如果发生了一个级别Q比P高，则可以启动，否则屏蔽掉。
	public static void main(String[] args) {
		/***/
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
