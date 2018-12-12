package com.led.netty.codec;

import java.util.List;

import com.led.netty.pojo.AbstractCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.utils.PackDataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.led.netty.pojo.LoginCommand;
import com.led.netty.pojo.SetUpTimeCommand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * encoder object channel to byte
 * @author Administrator
 *
 */
public class HardWareEncoder extends MessageToMessageEncoder<AbstractCommand> {

	private static final Logger logger = LoggerFactory.getLogger(HardWareEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractCommand msg, List<Object> out) throws Exception {
		logger.info("HardWareEncoder encode ");
		System.out.println("HardWareEncoder encode ");
		if(msg instanceof LoginCommand) {
			System.out.println("HardWareEncoder 登录...");
			LoginCommand loginCmd = (LoginCommand) msg;
			byte[] datas = PackDataUtils.packReplayLoginData(loginCmd);
			ByteBuf buf = Unpooled.copiedBuffer(datas);
			out.add(buf);
		}else if(msg instanceof HeartBeatCommand) {
			System.out.println("HardWareEncoder 心跳...");
			byte[] datas = PackDataUtils.heart;
			ByteBuf buf = Unpooled.copiedBuffer(datas);
			out.add(buf);
		}else if(msg instanceof SetUpTimeCommand) {
			System.out.println("HardWareEncoder 授时...");
			SetUpTimeCommand stcCmd = (SetUpTimeCommand) msg;
			byte[] datas = PackDataUtils.packReplaySetUpTimeData(stcCmd);
			ByteBuf buf = Unpooled.copiedBuffer(datas);
			out.add(buf);
		}
	}
	

	
}
