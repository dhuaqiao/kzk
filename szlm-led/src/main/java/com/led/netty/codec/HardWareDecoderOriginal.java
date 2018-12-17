package com.led.netty.codec;


import com.led.netty.pojo.AbstractCommand;
import com.led.netty.utils.DebugUtils;
import com.led.netty.utils.IOUtils;
import com.led.netty.utils.PackDataUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Decoder 標識
 * @author Administrator
 *
 */
public class HardWareDecoderOriginal extends ByteToMessageDecoder implements java.io.Closeable{

	private static final Logger logger = LoggerFactory.getLogger(HardWareDecoder.class);

	public static final AttributeKey<String> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel.user_pack");

	public static final  Integer MAX_HEADER_SIZE = 10000;

	//开始读取,内容,完成
	private boolean isReady = true;
	//数据包存放区域...
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//前一个数据
	private byte previousData = -1;
	//计数,计算包类型出现的位置...
	private AtomicInteger packageCount = new AtomicInteger();
	//包类型出现位置
	private int indexPackageType = -1;
	//控制卡设备id
	private String controlCardId;
	//控制卡设备id 数组
	private byte[] cardDeviceId;

	//包头
	public static final byte DATA_BEGIN = (byte)0xA5;
	//包尾
	public static final byte DATA_END = (byte)0xAE;
	//转码标识
	public static final byte DATA_NEED = (byte)0xAA;
	// convert
	public static final byte DATA_CONVERT_0X05 = (byte)0x05;
	//
	public static final byte DATA_CONVERT_0X0E = (byte)0x0E;
	//
	public static final byte DATA_CONVERT_0X0A = (byte)0x0A;
	//区分当前是心跳包还是正常数据包,默认是心跳包......
	private volatile boolean isHeartPackage = true;


	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while(in.isReadable()) {
			byte data = in.readByte();
			packageCount.incrementAndGet();
			//System.out.println(Integer.toHexString(data) +" "+data);
			boolean is0xA5 = data==DATA_BEGIN;
			boolean is0xAE = data==DATA_END;
			if(data==(byte)0x68){
				isHeartPackage = false;
				indexPackageType = packageCount.get();
			}
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
				if(isHeartPackage){
					System.out.println("心跳数据包...");
					cardDeviceId = Arrays.copyOfRange(datas,1,datas.length-2);
					controlCardId = new String(cardDeviceId);
					System.out.println("controlCardId "+controlCardId);
				}else{
					System.out.println("不是心跳数据包...");
					DebugUtils.debugData("decoder", datas);
					String hexDump = ByteBufUtil.hexDump(datas);
					System.out.println(hexDump);
					if(indexPackageType!=-1) {
						cardDeviceId= Arrays.copyOfRange(datas, 1, indexPackageType - 1);
						controlCardId = new String(cardDeviceId);
						System.out.println("controlCardId " + controlCardId);
						byte[] crcData = Arrays.copyOfRange(datas,indexPackageType-1,datas.length-3);
						//計算crc
						int crcNumber = PackDataUtils.calculationCRC(crcData);
						byte[] crcs = PackDataUtils.intToByteArray(crcNumber);
						System.out.println(crcs[0]+" "+crcs[1]+" "+PackDataUtils.binaryToHexString(crcs));
					}
				}

				AbstractCommand cmd = PackDataUtils.binaryTransCmd(datas);
				if(null!=cmd) { //写数据
					cmd.setDataBinary(datas);
					out.add(cmd);
				}else{
					out.add(datas);
					//byte[] openCmd = PackDataUtils.packOpenCmdByCardDeviceId(cardDeviceId);
					//out.add(openCmd);
					//byte[] closeCmd = PackDataUtils.packCloseCmdByCardDeviceId(cardDeviceId);
					//out.add(closeCmd);

					//closeCmd = PackDataUtils.packRestartAppCmdByCardDeviceId(cardDeviceId);
					//out.add(closeCmd);

					//closeCmd = PackDataUtils.packRestartHardWareCmdByCardDeviceId(cardDeviceId);
					//out.add(closeCmd);
				}
				//完整的数据包...
				reset();
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
		int size = baos.size();
		return MAX_HEADER_SIZE <= size;
	}

	/**
	 * 重置
	 */
	public void reset(){
		previousData = -1;
		indexPackageType = -1;
		baos.reset();//重置...
		isHeartPackage = true; //还原标识信息,假设是心跳包...
		packageCount.set(0);
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
		if(DATA_NEED==data || previousData==DATA_NEED) {//转码
			logger.info("转码");
			if(in.isReadable()) {
				byte dataNext = in.readByte();
				packageCount.incrementAndGet();
				if (DATA_CONVERT_0X05 == dataNext) {
					baos.write(0xa5);
				}else if (DATA_CONVERT_0X0E == dataNext) {
					baos.write(0xae);
				}else if (DATA_CONVERT_0X0A== dataNext) {
					baos.write(0xaa);
				}else{
					baos.write(data);
					baos.write(dataNext);
				}
			}else { //channel无最新数据,数据粘包
				System.out.println("channel无最新数据,数据粘包");
				previousData = data;
				baos.write(data);
			}
		}else{
			baos.write(data);
		}
		return data;
	}




	@Override
	public void close() throws IOException {
		System.out.println("DECODER... closeQuietly");
		IOUtils.closeQuietly(baos);
	}

}
