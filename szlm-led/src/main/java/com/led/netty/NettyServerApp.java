package com.led.netty;

import com.led.netty.config.NettyServerInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;

public class NettyServerApp {

	// 一共分为五个级别：DEBUG、INFO、WARN、ERROR和FATAL。这五个级别是有顺序的，DEBUG < INFO < WARN < ERROR < FATAL，
	//明白这一点很重要，这里Log4j有一个规则：假设设置了级别为P，如果发生了一个级别Q比P高，则可以启动，否则屏蔽掉。
	public static void main(String[] args) {
//		byte v = Integer.valueOf("A5", 16).byteValue();
//		System.out.println(v);
//		System.out.println((byte)0xA5);
//		System.out.println((byte)0xAE);
		//byte[] binary = {51,57,52,54,49,50};
		//System.out.println(new String(binary));

		/**
		33 51
		39 57
		34 52
		36 54
		31 49
		32 50
		*/
		// A5 33 39 34 36 31 32 01 AE 心跳数据
		// A5 33 39 34 36 31 32 91 AE 回复心跳
		byte[] data = {0x32, 0x00, 0x39, 0x32, 0x00, 0x39};
		System.out.println(new String(data));
		SpringApplication app = new SpringApplication(NettyServerInitializer.class);
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
}
