package com.led.netty.codec;

import com.led.netty.pojo.CommonCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.utils.PackDataUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * encoder object channel to byte
 * @author Administrator
 *
 */
public class HardWareUdpEncoder extends MessageToMessageEncoder<Object> {

	private static final Logger logger = LoggerFactory.getLogger(HardWareUdpEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		logger.info("HardWareUdpEncoder encode ");
		if(msg instanceof HeartBeatCommand) {
			CommonCommand cmd = (CommonCommand)msg;
			ByteBuf buf = Unpooled.copiedBuffer(cmd.getDataBinary());
			DatagramPacket datagramPacket = new DatagramPacket(buf,cmd.getDatagramPacket().sender());
			out.add(datagramPacket);
			printOutEncode(cmd);
		}else if(msg instanceof CommonCommand){
			CommonCommand cmd = (CommonCommand)msg;
			ByteBuf buf = Unpooled.copiedBuffer(cmd.getDataBinary());
			DatagramPacket datagramPacket = new DatagramPacket(buf,cmd.getDatagramPacket().sender());
			out.add(datagramPacket);
			printOutEncode(cmd);
		}else{
			System.out.println(" HardWareUdpEncoder else");
		}
	}

	public void printOutEncode(CommonCommand cmd){
		StringBuilder _builder = new StringBuilder();
		String info = ByteBufUtil.hexDump(cmd.getDataBinary()).toUpperCase();
		for(int i =0;i<info.length();i++){
			if(i%2==0) _builder.append(" ");
			_builder.append(info.charAt(i));
		}
		logger.info("发送数据.msg:{},",_builder);
	}
	
}
