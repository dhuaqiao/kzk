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

    //控制卡设备id
	private String controlCardId;

	//包头
	public static final byte DATA_BEGIN = (byte)0xA5;
	//包尾
	public static final byte DATA_END = (byte)0xAE;
	//转码标识
	public static final byte DATA_NEED = (byte)0xAA;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while(in.isReadable()) {
			byte data = in.readByte();
			System.out.println(Integer.toHexString(data) +" "+data);
			boolean is0xA5 = data==DATA_BEGIN;
			boolean is0xAE = data==DATA_END;
			if(isReady && is0xA5) {//数据开头
				checkDataAndReset(baos);
				isReady = false;
				baos.reset();//重置数据,可能是无效的数据
				baos.write(data);
			}else if(is0xAE) {//数据结尾
				checkDataAndReset(baos);
				baos.write(data);
				isReady = true;
				//完成数据读取...
				byte[] datas = baos.toByteArray();
				DebugUtils.debugData("decoder", datas);
				String hexDump = ByteBufUtil.hexDump(datas);
				System.out.println(hexDump);
				System.out.println("hexDump:"+hexDump.length());
				AbstractCommand cmd = PackDataUtils.binaryTransCmd(datas);
				if(null!=cmd) { //写数据
					cmd.setDataBinary(datas);
					out.add(cmd);
				}else{
					out.add(datas);
				}
				//完整的数据包...
				previousData = -1;
				baos.reset();//重置...
			}else {
				checkDataAndReset(baos);
				transCoding(in, data,baos);//转码...
			}
		}
	}

	public void checkDataAndReset(ByteArrayOutputStream baos){
		if(validateHead(baos)){
			System.out.println("error....");
			baos.reset();//超过了数据包长度,丢弃无效数据
		}
	}
	
	public boolean validateHead(ByteArrayOutputStream baos){
		return MAX_HEADER_SIZE <= baos.size();
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
		if(DATA_NEED==data || previousData==DATA_NEED) {//转码
			logger.info("转码");
			if(in.isReadable()) {
				byte dataNext = in.readByte();
				if ((byte)0x05 == dataNext) {
					baos.write(0xa5);
				}else if ((byte)0x0e == dataNext) {
					baos.write(0xae);
				}else if ((byte)0x0a == dataNext) {
					baos.write(0xaa);
				}else{
					baos.write(data);
					baos.write(dataNext);
					if((byte)0x68==dataNext) {//数据协议开始
						byte[] binaryCard = baos.toByteArray();
						controlCardId = ByteBufUtil.hexDump(binaryCard);
						System.out.println("controlCardId "+controlCardId);
					}
				}
			}else { //channel无最新数据,数据粘包
				System.out.println("channel无最新数据,数据粘包");
				previousData = data;
				baos.write(data);
			}
		}else{
			if((byte)0x68==data){//数据协议开始
				baos.write(0x68);
				byte[] binaryCard = baos.toByteArray();
				controlCardId = ByteBufUtil.hexDump(binaryCard);
				System.out.println("controlCardId "+controlCardId);
			}else {
				baos.write(data);
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
