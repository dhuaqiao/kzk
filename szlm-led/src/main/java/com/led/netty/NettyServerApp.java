package com.led.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;

import com.led.netty.config.NettyServerInitializer;

public class NettyServerApp {

	// 一共分为五个级别：DEBUG、INFO、WARN、ERROR和FATAL。这五个级别是有顺序的，DEBUG < INFO < WARN < ERROR < FATAL，
	//明白这一点很重要，这里Log4j有一个规则：假设设置了级别为P，如果发生了一个级别Q比P高，则可以启动，否则屏蔽掉。
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(NettyServerInitializer.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.setRegisterShutdownHook(true);
		app.run(args);
	}
}
