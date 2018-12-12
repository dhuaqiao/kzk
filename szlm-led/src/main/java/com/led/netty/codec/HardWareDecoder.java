package com.led.netty.codec;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.led.netty.pojo.AbstractCommand;
import com.led.netty.utils.DebugUtils;
import com.led.netty.utils.IOUtils;
import com.led.netty.utils.PackDataUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;


/**
 * Decoder
 * @author Administrator
 *
 */
public class HardWareDecoder extends ByteToMessageDecoder implements java.io.Closeable{
	
	private static final Logger logger = LoggerFactory.getLogger(HardWareDecoder.class);
	
	public static final AttributeKey<String> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel.user_pack");

	public static final  Integer MAX_HEADER_SIZE = 10000;
	
	//开始读取,内容,完成
	private boolean isReady = true;
	//数据包存放区域...
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//前一个数据
    private byte previousData = -1;
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while(in.isReadable()) {
			byte data = in.readByte();
			boolean is0xA5 = 0xA5==data;
			boolean is0xAE = 0xAE==data;
			if(isReady && is0xA5) {//数据开头
				isReady = false;
				baos.reset();//重置数据,可能是无效的数据
				baos.write(data);
			}else if(is0xAE) {//数据结尾
				baos.write(data);
				isReady = true;
				//完成数据读取...
				byte[] datas = baos.toByteArray();
				DebugUtils.debugData("decoder", datas);
				String hexDump = ByteBufUtil.hexDump(datas);
				System.out.println(hexDump);
				System.out.println("hexDump:"+hexDump.length());
				AbstractCommand cmd = PackDataUtils.binaryTransCmd(datas);
				if(null!=cmd) {
					cmd.setDataBinary(datas);
					out.add(cmd);
				}
				//完整的数据包...
				previousData = -1;
				baos.reset();//重置...
			}else {
				if(in.readableBytes()>1) {
					transCoding(in, data,baos);//转码...
				}
				if(baos.size()>MAX_HEADER_SIZE){
					System.out.println("error....");
					baos.reset();//超过了数据包长度,丢弃无效数据
				}
			}
		}
	}
	
	
	
	/**
	 * 转码
	 *  0x54 0x01 -> 0x55
	 *	0x54 0x02 -> 0x54
	 * 0xaa 0x05 ->  0xa5
	 * 0xaa 0x0e ->  0xae
	 * 0xaa 0x0a ->  0xaa
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param in
	 * @param data
	 * @param baos
	 * @return
	 */
	protected int transCoding(ByteBuf in,byte data,ByteArrayOutputStream baos) {
		return transCoding(in,data,false,baos);
	}
	
	/**
	 * 转码
	 *  0x54 0x01 -> 0x55
	 *	0x54 0x02 -> 0x54 
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param in
	 * @param data
	 * @param isTransCode 是否需要转码
	 * @return
	 */
	protected int transCoding(ByteBuf in,byte data,boolean isTransCode,ByteArrayOutputStream baos) {
		if(0xAA==data || previousData==0xAA) {//转码
			logger.info("转码");
			if(in.isReadable()) {
				byte dataNext = in.readByte();
				if (0x05 == dataNext) {
					baos.write(0xa5);
				}else if (0x0e == dataNext) {
					baos.write(0xae);
				}else if (0x0a == dataNext) {
					baos.write(0xaa);
				}
			}else { //channel无最新数据,数据粘包
				previousData = data;
			}
		}
		return data;
	}



	@Override
	public void close() throws IOException {
		System.out.println("DECODER... closeQuietly");
		IOUtils.closeQuietly(baos);
	}

}
