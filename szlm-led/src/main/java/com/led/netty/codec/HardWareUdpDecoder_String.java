package com.led.netty.codec;


import com.led.netty.pojo.CommonCommand;
import com.led.netty.utils.IOUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Decoder
 * @author Administrator
 *
 */
@Component
public class HardWareUdpDecoder_String extends ByteToMessageDecoder{
	
	private static final Logger logger = LoggerFactory.getLogger(HardWareUdpDecoder_String.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof DatagramPacket){
			DatagramPacket datagramPacket = (DatagramPacket)msg;
			ByteBuf in = datagramPacket.content();
			decodeReadByString(ctx,in);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		//igore
	}

	/**
	 * 先读然后在进行数据转义.
	 * @param ctx
	 * @param in
	 * @throws Exception
	 */
	protected void decodeReadByString(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		int bufferSize = in.readableBytes();
		byte[] buffer = new byte[bufferSize];
		in.readBytes(buffer);
		StringBuilder _builder = new StringBuilder();
		String msg = ByteBufUtil.hexDump(buffer).toUpperCase();
		for(int i =0;i<msg.length();i++){
			if(i%2==0) _builder.append(" ");
			_builder.append(msg.charAt(i));
		}
		System.out.println("原始网络数据:"+_builder);
		msg = msg.replaceAll("AA05","A5").replaceAll("AA0E","AE").replaceAll("AA0A","AA");
		System.out.println("转义之后数据:"+msg);
		if(msg.contains("E8")){

		}else {

		}
		byte[] dataSource = ByteBufUtil.decodeHexDump(msg);
		CommonCommand cmd = new CommonCommand();
		IOUtils.logWrite(1,cmd,logger);


	}


}
