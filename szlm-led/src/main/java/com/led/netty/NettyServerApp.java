package com.led.netty;

import com.led.netty.config.NettyUdpServerInitializer;
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
//		byte[] data = {0x32, 0x00, 0x39, 0x32, 0x00, 0x39};
//		System.out.println(new String(data));
//
//		byte[] dataT = {(byte)0xA5, 0x31, 0x38, 0x31, 0x32, 0x32, 0x36, 0x68, 0x32, 0x01, 0x7B, 0x01, 0x10, 0x00, 0x00, 0x00, (byte)0x85, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x5F, 0x31, 0x00, 0x53, 0x04, (byte)0xAE};
//
//
//		System.out.println(PackDataUtils.binaryToHexString(dataT));
//
//		dataT = PackDataUtils.pack0X85();
//
//		System.out.println(PackDataUtils.binaryToHexString(dataT));
//
//		String title = "今日头条是北京字节跳动科技有限公司开发的一款基于数据挖掘的推荐引擎产品，为用户推荐信息，提供连接人与信息的服务的产品。由张一鸣于2012年3月创建，2012年8月发布第一个版本。 [1]" +
//				"2016年9月20日，今日头条宣布投资10亿元用以补贴短视频创作。后独立孵化 UGC 短视频平台火山小视频 [2]  。2017年1月，今日头条中国新第一批认证的8组独立音乐人入驻今日头条。2017年2月2日，全资收购美国短视频应用Flipagram。 [3-4]" +
//				"2018年9月29日，针对网络转载版权专项整治中发现的突出版权问题，国家版权局在京约谈了今日头条，要求其进一步提高版权保护意识，切实加强版权制度建设，全面履行企业主体责任，规范网络转载版权秩序。";
//		List<byte[]> items = PackDataUtils.packDataByMany(256,32,1,4,1,0,6,title,null,null);
//		System.out.println(items.size());


		/***/
		SpringApplication app = new SpringApplication(NettyUdpServerInitializer.class);
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
