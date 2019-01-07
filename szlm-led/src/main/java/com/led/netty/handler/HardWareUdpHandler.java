package com.led.netty.handler;

import com.led.netty.pojo.CommonCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.pojo.QueryStateCommand;
import com.led.netty.pojo.UdpClient;
import com.led.netty.utils.PackDataUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@Component
public class HardWareUdpHandler extends SimpleChannelInboundHandler<Object>{

	private static final Logger logger = LoggerFactory.getLogger(HardWareUdpHandler.class);

	//存取CMD集合 -- 控制卡ID-CMD
	private  Map<Long,UdpClient> mapCardUdpClient = new ConcurrentHashMap<>();

	public volatile boolean isRunCheckUdpClient = true;

	//超时时间 单位s
	private volatile Integer TIME_OUT = 60;

	//执行器
	private ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * This method is visible for testing!
	 */
	private final long ticksInMillis() {
		return System.currentTimeMillis();
	}

	@PostConstruct
	public void startCheck() {
		//启动检测UdpClient 守护线程
		Thread startCheckThread = new Thread(){
			@Override
			public void run() {
				while (isRunCheckUdpClient){
					try {
						if(mapCardUdpClient.size()>0){
							long nowSystem = System.currentTimeMillis();
							mapCardUdpClient.forEach((c, k)->{
								if(TimeUnit.MILLISECONDS.toSeconds(nowSystem-k.getUnixTimeStamp())>=TIME_OUT){//c
									k.getCmds().clear();
									boolean isRemove = mapCardUdpClient.remove(c,k); //remove
									logger.info("Client超时,Key:{},isRemove:{}",k.getCardId(),isRemove);
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
		startCheckThread.setDaemon(true);
		startCheckThread.setName("Thread-Daemon-CheckUdpClient");
		startCheckThread.start();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception { // (1)
		if(!(msg instanceof  CommonCommand)) return; //不是指定的数据包
		CommonCommand cmd = (CommonCommand)msg;
		byte[] cardIdBinary = cmd.getDataCardId();
		Long keyCardId = null;
		try{
			keyCardId = Long.parseLong(new String(cardIdBinary));
		}catch (Exception e){
			throw new IllegalArgumentException("设备控制卡ID错误,不是数字...",e);
		}
		UdpClient udpClient = mapCardUdpClient.get(keyCardId);
		if(null==udpClient){
			udpClient = new UdpClient();
			udpClient.setCardId(keyCardId);
			udpClient.setCardIdBinary(cardIdBinary);
			mapCardUdpClient.put(keyCardId,udpClient);
		}
		//更新时间戳
		udpClient.setUnixTimeStamp(ticksInMillis());
		final Queue<CommonCommand> cmds = udpClient.getCmds();
		if(msg instanceof HeartBeatCommand) {
			if(udpClient.isInit()){
				udpClient.setInit(false);
				//此CMD 必须先发送...
				//sendTemplateAndStepIntoCmd(keyCardId,256,32);
				//节目1
				//sendContentCmd(keyCardId,256,32,1,4,1,6,"北斗三号基本系统完成建设，于今日开始提供全球服务。这标志着北斗系统服务范围由区域扩展为全球，北斗系统正式迈入全球时代。");
				//节目2
				//sendContentCmd(keyCardId,256,32,2,4,1,8,"公安部新规：民警依法履职致公民权益受损，个人不担法律责任。");
				//节目3
				//sendContentCmd(keyCardId,256,32,3,4,1,14,"特朗普威胁国会：若得不到建墙拨款，将关闭美墨边境。");

				//sendCommonCmd(keyCardId,1);//开机

				//查询状态 packConfigQueryState PackDataUtils.packConfigQueryState(cardIdBinary)
				//CommonCommand cmdQuery = new CommonCommand(PackDataUtils.packQueryCmdByCardDeviceId(cardIdBinary),cardIdBinary);
				//cmds.offer(cmdQuery);

				//sendCommonCmd(keyCardId,2);//关机

				//cmdQuery = new CommonCommand(PackDataUtils.packQueryCmdByCardDeviceId(cardIdBinary),cardIdBinary);
				//cmds.offer(cmdQuery);

				/** 测试状态查询...
				final long cid = keyCardId;
				new Thread(()->{
					try {
						sendCommonCmd(cid,1,false);//开机
						System.out.println(Thread.currentThread().getName()+" before 1 ");
						Integer state = checkState(cid);
						System.out.println(Thread.currentThread().getName()+" state "+state);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();

				//Thread.sleep(5000);



				new Thread(()->{
					try {
						sendCommonCmd(cid,2,false);//关机
						System.out.println(Thread.currentThread().getName()+" before 2 ");
						Integer state = checkState(cid);
						System.out.println(Thread.currentThread().getName()+" state "+state);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
				 */

				//删除序号为1在节目
				//sendDeleteItemCmd(keyCardId,1);
				//删除序号为1,2,3在节目
				//sendDeleteItemCmd(keyCardId,new int[]{1,2,3});
				//删除全部
				//sendDeleteItemCmd(keyCardId);

			}else{
				ctx.writeAndFlush(msg); //发送心跳包
			}
		}else if(msg instanceof QueryStateCommand){//状态查询
			QueryStateCommand queryStateCommand = (QueryStateCommand)msg;
			int state = queryStateCommand.getState();
			if(0==state || 1==state){ //查询状态响应数据包
				//不阻塞NIO
				UdpClient executorUdpClient = udpClient;
				executor.execute(()->{
					executorUdpClient.setState(state);//状态设置...
					executorUdpClient.syncUnLockState();//解除Block
				});
			}
		}
		//刷新队列,并发送数据...
		writeCmdToCard(ctx, cmd, udpClient);
	}

	/**
	 * 写数据给设备
	 * @param ctx
	 * @param cmd
	 * @param udpClient
	 */
	private void writeCmdToCard(ChannelHandlerContext ctx, CommonCommand cmd, UdpClient udpClient) {
		Queue<CommonCommand> cmdItems = udpClient.getCmds();
		if(cmdItems.isEmpty()) return;
		CommonCommand cmdItem = cmdItems.poll();
		if (cmdItem != null) {
			cmdItem.setDatagramPacket(cmd.getDatagramPacket());
			ChannelFuture channelFuture = ctx.writeAndFlush(cmdItem);
			//if(channelFuture.isSuccess()){}
		}
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		logger.error("Handler ex:{}",cause.toString());
	}


	/**
	 * @param type 1 开机 2 关机 3 重启APP 4 硬件重启
	 * @param keyCardId 控制卡ID
	 * @return true 加入队列成功 false 控制卡不在线
	 */
	public boolean sendCommonCmd(long keyCardId,int type){
		UdpClient udpClient = mapCardUdpClient.get(keyCardId);
		if(udpClient!=null){
			byte[] cardIdBinary = udpClient.getCardIdBinary();
			byte[] data = null;
			switch (type){
				case 1:
					data = PackDataUtils.packOpenCmdByCardDeviceId(cardIdBinary);
					break;
				case 2:
					data = PackDataUtils.packCloseCmdByCardDeviceId(cardIdBinary);
					break;
				case 3:
					data = PackDataUtils.packRestartAppCmdByCardDeviceId(cardIdBinary);
					break;
				case 4:
					data = PackDataUtils.packRestartHardWareCmdByCardDeviceId(cardIdBinary);
					break;
			}
			if(null!=data){
				CommonCommand cmd = new CommonCommand(data,cardIdBinary);
				return udpClient.getCmds().offer(cmd);
			}
			return false;
		}
		return false;
	}

	/**
	 * 设置模版并进入模版方式
	 * @param keyCardId 控制卡id
	 * @param width 屏幕宽度
	 * @param height 屏幕高度
	 * @return true 加入队列成功 false 加入失败
	 */
	public boolean sendTemplateAndStepIntoCmd(long keyCardId,int width,int height){
		UdpClient udpClient = mapCardUdpClient.get(keyCardId);
		/**
		 * if(udpClient!=null){
		 * 			byte[] cardIdBinary = udpClient.getCardIdBinary();
		 * 			byte[] dataSetTemplate = PackDataUtils.setTemplate(width,height,cardIdBinary);
		 * 			CommonCommand cmd = new CommonCommand(data,cardIdBinary);
		 * 			udpClient.getCmds().add(cmd);
		 * 			data = PackDataUtils.stepIntoTemplate(cardIdBinary);
		 * 			cmd = new CommonCommand(data,cardIdBinary);
		 * 			udpClient.getCmds().add(cmd);
		 * 			return true;
		 * }
		 */
		if(udpClient!=null){
			byte[] cardIdBinary = udpClient.getCardIdBinary();
			byte[] dataSetTemplate = PackDataUtils.setTemplate(width,height,cardIdBinary);
			byte[] dataStepInto = PackDataUtils.stepIntoTemplate(cardIdBinary);
			byte[] newDataBinary = new byte[dataSetTemplate.length+dataStepInto.length];
			System.arraycopy(dataSetTemplate, 0, newDataBinary, 0, dataSetTemplate.length);
			System.arraycopy(dataStepInto, 0, newDataBinary, dataSetTemplate.length, dataStepInto.length);
			CommonCommand cmd = new CommonCommand(newDataBinary,cardIdBinary);
			return udpClient.getCmds().offer(cmd);
		}
		return false;
	}

	/**
	 * 发送文本数据
	 * @param keyCardId 控制卡id
	 * @param width 宽度
	 * @param height 高度
	 * @param xh 节目号 (1-10)
	 * @param font 字体 (2 =16号字体 3 =24号字体 4 =32号字体 5 =40号字体 6 =48号字体 7 =56号字体)
	 * @param time 时间 参考 默认填写1
	 * @param hy 样式 (0 立即显示 6 左移 7 右移 8	上移 9	下移  10	向上滚动  11	向左滚动 12	向右滚动) 参考 《显示特效编码》
	 * @param content 内容
	 * @return true 加入队列成功 false 加入失败
	 */
	public boolean sendContentCmd(long keyCardId,int width,int height,int xh,int font,int time,int hy,String content){
		UdpClient udpClient = mapCardUdpClient.get(keyCardId);
		if(udpClient!=null){
			byte[] cardIdBinary = udpClient.getCardIdBinary();
			List<byte[]> items = PackDataUtils.packSubcontract(width,height,xh,font,time,0,hy,content);
			//remainingCapacity
			if(udpClient.getCmds().remainingCapacity()<items.size()) throw new IllegalArgumentException("队列容量已经超过上线");
			//内容
			items.forEach(data->{
				//加入设备id
				byte[] binaryData = PackDataUtils.packDataAddCardDeviceId(cardIdBinary,data);
				CommonCommand cmd = new CommonCommand(binaryData,cardIdBinary);
				udpClient.getCmds().offer(cmd);
			});
			return true;
		}
		return false;
	}

	/**
	 * 删除节目,删除全部相当于清屏
	 * @param keyCardId 控制卡id
	 * @param itemNos 节目序号数组 如果为空 则删除全部
	 * @return true 加入队列成功 false 加入失败
	 */
	public boolean sendDeleteItemCmd(long keyCardId,int...itemNos){
		UdpClient udpClient = mapCardUdpClient.get(keyCardId);
		if(null!=udpClient){
			byte[] cardIdBinary = udpClient.getCardIdBinary();
			byte[] binaryData = PackDataUtils.packDeleteItem(cardIdBinary,itemNos);
			CommonCommand cmd = new CommonCommand(binaryData,cardIdBinary);
			return udpClient.getCmds().offer(cmd);
		}

		return false;
	}

	/**
	 * 检查控制卡是否在线
	 * @param keyCardId 控制卡id
	 * @return true 在线 false 不在线
	 */
	public boolean checkOnLine(Long keyCardId){
		return mapCardUdpClient.containsKey(keyCardId);
	}

	/**
	 * 检查控制卡是否开关屏
	 * @param keyCardId 控制卡id
	 * @return  1 开屏 0 关屏 其他 状态未知(如设备未上线,未读取到相应数据包)
	 * @throws Exception
	 */
	public  Integer checkState(Long keyCardId)throws Exception{
		/** other Solution
		 AttributeKey attributeKey = AttributeKey.valueOf("");
		 Attribute attribute = udpClient.getContext().channel().attr(attributeKey);
		 attribute.get();
		 */
		UdpClient udpClient = mapCardUdpClient.get(keyCardId);
		if(null==udpClient){
			return -1;
		}
		//synchronized (udpClient) { //lock 具体的控制卡...
			FutureTask<Integer> task = new FutureTask<Integer>(() -> {
				udpClient.syncLockState();//设置Block,将同步转换为异步驱动...
				int state = udpClient.getState();
				udpClient.setState(-1);
				return state;
			});
			byte[] cardIdBinary = udpClient.getCardIdBinary();
			byte[] packData = PackDataUtils.packQueryCmdByCardDeviceId(cardIdBinary);
			CommonCommand cmdQuery = new CommonCommand(packData, cardIdBinary);
			udpClient.getCmds().offer(cmdQuery);
			task.run();//start...
			return task.get();
		//}
	}

	//destory
	public void destory() {
		isRunCheckUdpClient = false;
		executor.shutdown();
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
