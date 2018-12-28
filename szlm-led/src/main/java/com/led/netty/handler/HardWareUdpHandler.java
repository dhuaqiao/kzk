package com.led.netty.handler;

import com.led.netty.pojo.CommonCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.pojo.UdpClient;
import com.led.netty.utils.PackDataUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class HardWareUdpHandler extends SimpleChannelInboundHandler<Object>{

	private static final Logger logger = LoggerFactory.getLogger(HardWareUdpHandler.class);

	//存取CMD集合 -- 控制卡ID-CMD
	private  Map<String,UdpClient> mapItems = new ConcurrentHashMap<>();

	private volatile boolean isRunCheckUdpClient = true;

	//超时时间 单位s
	private volatile Integer TIME_OUT = 60;


	public HardWareUdpHandler() {
		//启动检测UdpClient 守护线程
		Thread thread = new Thread(){
			@Override
			public void run() {
				while (isRunCheckUdpClient){
					try {
						if(mapItems.size()>0){
							long nowSystem = System.currentTimeMillis();
							mapItems.forEach((c,k)->{
								if(TimeUnit.MILLISECONDS.toSeconds(nowSystem-k.getUnixTimeStamp())>=TIME_OUT){//c
									logger.info("Client超时,Key:{}",k.getCardId());
									mapItems.remove(c,k); //remove
								}
							});
						}
						Thread.sleep(TIME_OUT*1000);
					}catch (ConcurrentModificationException e){
						logger.error("检测在线线程异常,更新Map出错...{}",e);
					}catch (Exception e){
						//isRunCheckUdpClient = false;
						logger.error("检测在线线程异常...{}",e);
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.setName("Thread-Daemon-CheckUdpClient");
		thread.start();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception { // (1)
		if(!(msg instanceof  CommonCommand)) return; //不是指定的数据包
		CommonCommand cmd = (CommonCommand)msg;
		byte[] cardId = cmd.getDataCardId();
		String key = new String(cardId);
		UdpClient udpClient = mapItems.get(key);
		if(null==udpClient){
			udpClient = new UdpClient();
			udpClient.setCardId(key);
			udpClient.setCardIdBinary(cardId);
			mapItems.put(key,udpClient);
		}
		//更新时间戳
		udpClient.setUnixTimeStamp(System.currentTimeMillis());
		final Queue<CommonCommand> cmds = udpClient.getCmds();
		if(msg instanceof HeartBeatCommand) {
			if(cmds.isEmpty() && udpClient.isInit()){
				udpClient.setInit(false);
				//_testSendCmd(ctx, cmd, cardId, cmds);

				sendCommonCmd(cardId,2);
				sendCommonCmd(cardId,1);

				sendTemplateAndStepIntoCmd(cardId,256,32);

				sendContentCmd(cardId,256,32,1,4,1,6,"北斗三号基本系统完成建设，于今日开始提供全球服务。这标志着北斗系统服务范围由区域扩展为全球，北斗系统正式迈入全球时代。");

			}else{
				ctx.writeAndFlush(msg); //发送心跳包
				writeCmdToCard(ctx, cmd, udpClient);
			}
		}else{
			writeCmdToCard(ctx, cmd, udpClient);
		}
	}

	/**
	 * 写数据给设备
	 * @param ctx
	 * @param cmd
	 * @param udpClient
	 */
	private void writeCmdToCard(ChannelHandlerContext ctx, CommonCommand cmd, UdpClient udpClient) {
		Queue<CommonCommand> cmdItems = udpClient.getCmds();
		if (null != cmdItems) {
			CommonCommand cmdItem = cmdItems.poll();
			if (cmdItem != null) {
				cmdItem.setDatagramPacket(cmd.getDatagramPacket());
				ChannelFuture channelFuture = ctx.writeAndFlush(cmdItem);
				//if(channelFuture.isSuccess()){}
			}
		}
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		logger.error("Handler ex:{}",cause);
	}


	/**
	 * @param type 1 开机 2 关机 3 重启APP 4 硬件重启
	 * @param cardDeviceId 控制卡ID数组
	 * @return true 加入队列成功 false 控制卡不在线
	 */
	public boolean sendCommonCmd(byte[] cardDeviceId,int type){
		String key = new String(cardDeviceId);
		UdpClient udpClient = mapItems.get(key);
		if(udpClient!=null){
			byte[] data = null;
			switch (type){
				case 1:
					data = PackDataUtils.packOpenCmdByCardDeviceId(cardDeviceId);
					break;
				case 2:
					data = PackDataUtils.packCloseCmdByCardDeviceId(cardDeviceId);
					break;
				case 3:
					data = PackDataUtils.packRestartAppCmdByCardDeviceId(cardDeviceId);
					break;
				case 4:
					data = PackDataUtils.packRestartHardWareCmdByCardDeviceId(cardDeviceId);
					break;
			}
			if(null!=data){
				CommonCommand cmd = new CommonCommand(data,cardDeviceId);
				udpClient.getCmds().add(cmd);
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 设置模版并进入模版方式
	 * @param cardDeviceId 控制卡id
	 * @param width 屏幕宽度
	 * @param height 屏幕高度
	 * @return
	 */
	public boolean sendTemplateAndStepIntoCmd(byte[] cardDeviceId,int width,int height){
		String key = new String(cardDeviceId);
		UdpClient udpClient = mapItems.get(key);
		if(udpClient!=null){
			byte[] data = PackDataUtils.setTemplate(width,height,cardDeviceId);
			CommonCommand cmd = new CommonCommand(data,cardDeviceId);
			udpClient.getCmds().add(cmd);
			data = PackDataUtils.stepIntoTemplate(cardDeviceId);
			cmd = new CommonCommand(data,cardDeviceId);
			udpClient.getCmds().add(cmd);
			return true;
		}
		return false;
	}

	/**
	 * 发送文本数据
	 * @param cardDeviceId 控制卡id
	 * @param width 宽度
	 * @param height 高度
	 * @param xh 节目号 (1-10)
	 * @param font 字体 (2 =16号字体 3 =24号字体 4 =32号字体 5 =40号字体 6 =48号字体 7 =56号字体)
	 * @param time 时间 参考 默认填写1
	 * @param hy 样式 (0 立即显示 6 左移 7 右移 8	上移 9	下移  10	向上滚动  11	向左滚动 12	向右滚动) 参考 《显示特效编码》
	 * @param content 内容
	 * @return
	 */
	public boolean sendContentCmd(byte[] cardDeviceId,int width,int height,int xh,int font,int time,int hy,String content){
		String key = new String(cardDeviceId);
		UdpClient udpClient = mapItems.get(key);
		if(udpClient!=null){
			List<byte[]> datas = PackDataUtils.packSubcontract(width,height,xh,font,time,0,hy,content);
			//内容
			datas.forEach(data->{
				//加入设备id
				byte[] binaryData = PackDataUtils.packDataAddCardDeviceId(cardDeviceId,data);
				CommonCommand cmd = new CommonCommand(binaryData,cardDeviceId);
				udpClient.getCmds().add(cmd);
			});
			return true;
		}
		return false;
	}



	//测试发送指令
	private void _testSendCmd(ChannelHandlerContext ctx, CommonCommand cmd, byte[] cardId, Queue<CommonCommand> cmds) {
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

		String title = "今日头条是北京字节跳动科技有限公司开发的一款基于数据挖掘的推荐引擎产品，为用户推荐信息，提供连接人与信息的服务的产品。";
		List<byte[]> items = PackDataUtils.packSubcontract(256,32,1,4,1,0,6,title,null,null);

		title = "北斗三号基本系统完成建设，于今日开始提供全球服务。这标志着北斗系统服务范围由区域扩展为全球，北斗系统正式迈入全球时代。";
		List<byte[]> items2 = PackDataUtils.packSubcontract(256,32,2,4,1,0,6,title);

		items.addAll(items2);


		//内容
		items.forEach(data->{
			//加入设备id
			byte[] binaryData = PackDataUtils.packDataAddCardDeviceId(cardId,data);
			CommonCommand c = new CommonCommand(binaryData,cardId,cmd.getDatagramPacket());
			cmds.add(c);
		});
		//删除节目
		byte[] binaryDelete = PackDataUtils.packDeleteItem(cardId,1);
		item = new CommonCommand(binaryDelete,cardId,cmd.getDatagramPacket());
		cmds.add(item);
		//删除节目
		binaryDelete = PackDataUtils.packDeleteItem(cardId,2);
		item = new CommonCommand(binaryDelete,cardId,cmd.getDatagramPacket());
		cmds.add(item);
		binaryDelete = PackDataUtils.packDeleteItem(cardId,null);
		item = new CommonCommand(binaryDelete,cardId,cmd.getDatagramPacket());
		cmds.add(item);


		System.out.println("cmds "+cmds.size());
	}


}
