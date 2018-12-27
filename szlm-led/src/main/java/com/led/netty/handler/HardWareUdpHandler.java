package com.led.netty.handler;

import com.led.netty.pojo.CommonCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.utils.PackDataUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class HardWareUdpHandler extends SimpleChannelInboundHandler<Object>{

	private static final Logger logger = LoggerFactory.getLogger(HardWareUdpHandler.class);

	private volatile boolean isInit = true;
	//存取CMD集合 -- 控制卡ID-CMD
	private  Map<String,Queue<CommonCommand>> mapItems = new ConcurrentHashMap<>();


	@Override
	protected void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception { // (1)
		if(!(msg instanceof  CommonCommand)) return; //不是指定的数据包
		CommonCommand cmd = (CommonCommand)msg;
		if(msg instanceof HeartBeatCommand) {
			byte[] cardId = cmd.getDataCardId();
			//数据
			String key = new String(cardId);
			if(!mapItems.containsKey(key)){
				Queue<CommonCommand> cmds = new ArrayBlockingQueue(100);
				//开机
				byte[] binary = PackDataUtils.packOpenCmdByCardDeviceId(cardId);
				//重啓
				//binary = PackDataUtils.packRestartHardWareCmdByCardDeviceId(cardId);
				//关机
				binary = PackDataUtils.packCloseCmdByCardDeviceId(cardId);
				CommonCommand item = new CommonCommand(binary,cardId,cmd.getDatagramPacket());
				//指令
				ctx.writeAndFlush(item);

				//close
				item = new CommonCommand(PackDataUtils.packCloseCmdByCardDeviceId(cardId),cardId,cmd.getDatagramPacket());
				cmds.add(item);
				//open
				item = new CommonCommand(PackDataUtils.packOpenCmdByCardDeviceId(cardId),cardId,cmd.getDatagramPacket());
				cmds.add(item);
				//clear
				//item = new CommonCommand(PackDataUtils.packClearCmdByCardDeviceId(cardId),cardId,cmd.getDatagramPacket());
				//cmds.add(item);

				//设置模版
				byte[] setTemplate = PackDataUtils.setTemplate(256,32,cardId);
				//設置模板
				item = new CommonCommand(setTemplate,cardId,cmd.getDatagramPacket());
				cmds.add(item);
				//进入模版方式
				item = new CommonCommand(PackDataUtils.stepIntoTemplate(cardId),cardId,cmd.getDatagramPacket());
				cmds.add(item);

				//packData0x85  packDataByMany pack0X85
				String title = "今日头条是北京字节跳动科技有限公司开发的一款基于数据挖掘的推荐引擎产品，为用户推荐信息，提供连接人与信息的服务的产品。由张一鸣于2012年3月创建，2012年8月发布第一个版本。 [1]" +
						"2016年9月20日，今日头条宣布投资10亿元用以补贴短视频创作。后独立孵化 UGC 短视频平台火山小视频 [2]  。2017年1月，今日头条中国新第一批认证的8组独立音乐人入驻今日头条。2017年2月2日，全资收购美国短视频应用Flipagram。 [3-4]" +
						"2018年9月29日，针对网络转载版权专项整治中发现的突出版权问题，国家版权局在京约谈了今日头条，要求其进一步提高版权保护意识，切实加强版权制度建设，全面履行企业主体责任，规范网络转载版权秩序。";

				title = "今日头条是北京字节跳动科技有限公司开发的一款基于数据挖掘的推荐引擎产品，为用户推荐信息，提供连接人与信息的服务的产品。";
				List<byte[]> items = PackDataUtils.packSubcontract(256,32,1,4,1,0,6,title,null,null);

				title = "北斗三号基本系统完成建设，于今日开始提供全球服务。这标志着北斗系统服务范围由区域扩展为全球，北斗系统正式迈入全球时代。";
				List<byte[]> items2 = PackDataUtils.packSubcontract(256,32,2,4,1,0,6,title);

				items.addAll(items2);

				//byte[] dataT = {(byte)0xA5, 0x68, 0x32, 0x01, 0x7B, 0x01, 0x10, 0x00, 0x00, 0x00, (byte)0x85, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x68, 0x65, 0x6C, 0x6C, 0x6F, 0x5F, 0x31, 0x00, 0x53, 0x04, (byte)0xAE};
//				title = "今日头条是北京字节跳动科技有限公司开发的一款基于数据挖掘的推荐引擎产品，为用户推荐信息，提供连接人与信息的服务的产品。";
//				byte[] dataT = PackDataUtils.pack0X85(title);
//				byte[] data0x85 = PackDataUtils.packDataAddCardDeviceId(cardId,dataT);
//
//				System.out.println(PackDataUtils.binaryToHexString(data0x85));
//
//				item = new CommonCommand(data0x85,cardId,cmd.getDatagramPacket());
//				cmds.add(item);




				//内容
				items.forEach(data->{
					//加入设备id
					byte[] binaryData = PackDataUtils.packDataAddCardDeviceId(cardId,data);
					CommonCommand c = new CommonCommand(binaryData,cardId,cmd.getDatagramPacket());
					cmds.add(c);
				});



				mapItems.put(key,cmds);
			}else{
				ChannelFuture channelFuture = ctx.writeAndFlush(msg); //发送心跳包
				//System.out.println("channelFuture "+channelFuture.isDone()+ " > "+channelFuture.isSuccess());
			}

		}else{
			CommonCommand dataReviceCmd = (CommonCommand)msg;
			byte[] cardId = dataReviceCmd.getDataCardId();
			String key = new String(cardId);
			Queue<CommonCommand> cmds = mapItems.get(key);
			if(null!=cmds) {
				CommonCommand cmdItem = cmds.poll();
				if (cmdItem != null) {
					ChannelFuture channelFuture = ctx.writeAndFlush(cmdItem);
					//System.out.println("channelFuture "+channelFuture.isDone()+ " > "+channelFuture.isSuccess());
				}
			}
		}


	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		logger.error("Handler ex:{}",cause);
	}
	


}
