package com.led.netty.codec;

import com.led.netty.pojo.CommonCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.utils.LoggerUtils;
import io.netty.buffer.ByteBuf;
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
		if(msg instanceof HeartBeatCommand) {
			HeartBeatCommand cmd = (HeartBeatCommand)msg;
			ByteBuf buf = Unpooled.copiedBuffer(cmd.getDataBinary());
			DatagramPacket datagramPacket = new DatagramPacket(buf,cmd.getDatagramPacket().sender());
			out.add(datagramPacket);
			LoggerUtils.writeOutLog(2,cmd,logger);
		}else if(msg instanceof CommonCommand){
			CommonCommand cmd = (CommonCommand)msg;
			ByteBuf buf = Unpooled.copiedBuffer(cmd.getDataBinary());
			DatagramPacket datagramPacket = new DatagramPacket(buf,cmd.getDatagramPacket().sender());
			out.add(datagramPacket);
			LoggerUtils.writeOutLog(2,cmd,logger);
		}else{
			logger.error("unkonw cmd ...{}",msg);
		}
	}


	
}
