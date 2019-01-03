package com.led.netty.codec;


import com.led.netty.pojo.CommonCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.pojo.QueryStateCommand;
import com.led.netty.utils.IOUtils;
import com.led.netty.utils.PackDataUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * Decoder
 * @author Administrator
 *
 */
@Component
public class HardWareUdpDecoder extends ByteToMessageDecoder{
	
	private static final Logger logger = LoggerFactory.getLogger(HardWareUdpDecoder.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		DatagramPacket datagramPacket = (DatagramPacket)msg;
		ByteBuf in = datagramPacket.content();
		decodeReadByString(ctx,in,datagramPacket);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		//igore
	}

	/**
	 * 先读然后在进行数据转义.
	 * @param ctx
	 * @param byteBuf
	 * @param datagramPacket
	 * @throws Exception
	 */
	protected void decodeReadByString(ChannelHandlerContext ctx, ByteBuf byteBuf,DatagramPacket datagramPacket) throws Exception {
		int bufferSize = byteBuf.readableBytes();
		byte[] buffer = new byte[bufferSize];
		byteBuf.readBytes(buffer);
		StringBuilder _builder = new StringBuilder();
		String infoHexDump = ByteBufUtil.hexDump(buffer).toUpperCase();
		String msgConverter = infoHexDump.substring(0,infoHexDump.length()-6)
				.replaceAll("AA05","A5")
				.replaceAll("AA0E","AE")
				.replaceAll("AA0A","AA");
		String msg = _builder.append(msgConverter).append(infoHexDump.substring(infoHexDump.length()-6)).toString();
		CommonCommand cmd = null;
		byte[] datas = ByteBufUtil.decodeHexDump(msg);
		IOUtils.logWrite(datas,logger);//日志...
		if(datas.length>2 && msg.startsWith("A5") && msg.endsWith("AE")) {
			int indexE8 = msg.indexOf("E8");
			if (indexE8 != -1) {
				int e8Length = indexE8/2;
				byte[] cardDeviceId = Arrays.copyOfRange(datas, 1, e8Length);
				if(datas.length>e8Length+6){
					System.out.println("datas[e8Length+3]: "+PackDataUtils.byteToHexString(datas[e8Length+3]));
					if(datas[e8Length+3]==0x76){// 软件开关屏控制
						//A5 33 39 34 36 31 33 E8 32 01 76 01 00 00 00 00 00 00 00 00 94 01 AE
						//A5 33 39 34 36 31 33 E8 32 01 76 01 01 00 00 00 00 00 00 00 93 01 AE
						if(msg.endsWith("E83201760100000000000000009401AE")){//关-屏幕当前状态
							cmd = new QueryStateCommand(datas,cardDeviceId,datagramPacket,0);
						}else if(msg.endsWith("E83201760101000000000000009301AE")){//开-屏幕当前状态
							cmd = new QueryStateCommand(datas,cardDeviceId,datagramPacket,1);
						}else {
							cmd = new QueryStateCommand(datas,cardDeviceId,datagramPacket,-1);
						}
					}
					if(null==cmd){ //其他 //指令回复
						cmd = PackDataUtils.binaryTransCmd(datas, cardDeviceId, datagramPacket);
						cmd.setCode(datas[e8Length+4] == 0);
					}
				}else{
					cmd = PackDataUtils.binaryTransCmd(datas, cardDeviceId, datagramPacket);
				}
			} else{ //心跳
				byte[] cardDeviceId = Arrays.copyOfRange(datas, 1, datas.length - 2);
				cmd = new HeartBeatCommand();
				datas = PackDataUtils.packHeartHardWareCmdByCardDeviceId(cardDeviceId);
				cmd.setDataBinary(datas);
				cmd.setDatagramPacket(datagramPacket);
				cmd.setDataCardId(cardDeviceId);
			}
		}else{
			logger.warn("unknown cmd => {}",new String(datas,"GBK"));
		}
		if(null!=cmd) { //写数据
			IOUtils.logWrite(1,cmd,logger);
			ctx.fireChannelRead(cmd);; //调用发送
		}
	}


}
