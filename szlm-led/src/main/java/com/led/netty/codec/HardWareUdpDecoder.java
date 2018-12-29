package com.led.netty.codec;


import com.led.netty.pojo.CommonCommand;
import com.led.netty.pojo.HeartBeatCommand;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Decoder
 * @author Administrator
 *
 */
@Component
public class HardWareUdpDecoder extends ByteToMessageDecoder implements java.io.Closeable{
	
	private static final Logger logger = LoggerFactory.getLogger(HardWareUdpDecoder.class);

	public static final  Integer MAX_HEADER_SIZE = 1024*512;

	//开始读取,内容,完成
	private boolean isReady = true;
	//数据包存放区域...
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//转义字节
    private volatile AtomicBoolean escapeByte = new AtomicBoolean(false);

	//计数,计算包类型出现的位置...
	private AtomicInteger packageCount = new AtomicInteger();
	//验证消息成功与否
	private AtomicInteger vaildateCount = new AtomicInteger();
	//包类型出现位置
	private int indexPackageType = -1;
    //控制卡设备id
	private String controlCardId;
	//控制卡设备id 数组
	private byte[] cardDeviceId;
	//记录成功 0 成功 1 失败
	private int code = -1;

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
	//
	public static final byte DATA_CONVERT_0X7B = (byte)0x7B;
	//区分当前是心跳包还是正常数据包,默认是心跳包......
	private volatile boolean isHeartPackage = true;
	//
	private Queue<Object> queueData = new java.util.concurrent.LinkedBlockingDeque<Object>();


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof DatagramPacket){
			DatagramPacket datagramPacket = (DatagramPacket)msg;
			ByteBuf in = datagramPacket.content();
			decodeByByteRead(ctx,in,queueData,datagramPacket);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		//igore
	}



	/**
	 * RS232/RS485返回数据包
	 *
	 * @param ctx
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	protected void decodeByByteRead(ChannelHandlerContext ctx, ByteBuf in, Queue<Object> out,DatagramPacket datagramPacket) throws Exception {
		while(in.isReadable()) {
			byte data = in.readByte();
			packageCount.incrementAndGet();
			boolean is0xA5 = data==DATA_BEGIN;
			boolean is0xAE = data==DATA_END;
			if(vaildateCount.get()==1){ //读取消息是否成功标识
				vaildateCount.set(2);
				code = data;
			}
			if(data==(byte)0xE8 || data==(byte)0x68){ // 0xE8 0x68
				isHeartPackage = false;
				indexPackageType = packageCount.get();
			}else if(data==DATA_CONVERT_0X7B && vaildateCount.get()==0){ //回复消息,只判断第一个
				vaildateCount.set(1);
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
				CommonCommand cmd = null;
				//数据业务处理
				if(isHeartPackage){
					cardDeviceId = Arrays.copyOfRange(datas,1,datas.length-2);
					controlCardId = new String(cardDeviceId);
					cmd = new HeartBeatCommand();
					datas = PackDataUtils.packHeartHardWareCmdByCardDeviceId(cardDeviceId);
					cmd.setDataBinary(datas);
					cmd.setDatagramPacket(datagramPacket);
					cmd.setDataCardId(cardDeviceId);
				}else{
					if(indexPackageType!=-1 && indexPackageType>1) {
						cardDeviceId= Arrays.copyOfRange(datas, 1, indexPackageType - 1);
						controlCardId = new String(cardDeviceId);
						cmd = PackDataUtils.binaryTransCmd(datas,cardDeviceId,datagramPacket);
						cmd.setCode(code==0);

					}
				}

				//InetSocketAddress sender = datagramPacket.sender();
				//InetSocketAddress recipient = datagramPacket.recipient();
				//System.out.println("sender "+sender+" recipient "+recipient);

				if(null!=cmd) { //写数据
					//LoggerUtils.writeOutLog(1,cmd,logger);
					IOUtils.logWrite(1,cmd,logger);
					ctx.fireChannelRead(cmd);; //调用发送
				}
				//完整的数据包...
				reset();
			}else {
				checkDataAndReset(baos);
				transCoding(in, data,baos);
			}
		}
	}



	public void checkDataAndReset(ByteArrayOutputStream baos){
		if(validateHead(baos)){
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
		indexPackageType = -1;
		baos.reset();//重置...
		isHeartPackage = true; //还原标识信息,假设是心跳包...
		packageCount.set(0);
		vaildateCount.set(0);
	}
	
	/**
	 * 转码
	 接收：
	 接收到符号0xa5，表示一个包的开始
	 接收到符号0xae，表示一个包的结束
	 在0xa5，0xae之间接收的数据，当接收到0xaa时，需要与其后的一个字节合成还原为转义前的字符。具体为：
	 0xaa 0x05  0xa5
	 0xaa 0x0e  0xae
	 0xaa 0x0a  0xaa
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param in
	 * @param data
	 * @param baos
	 * @return
	 */
	protected int transCoding(ByteBuf in,byte data,ByteArrayOutputStream baos) {
		if(DATA_NEED==data || escapeByte.get()) {//转码
			logger.info("转码,hex:{}",PackDataUtils.byteToHexString(data));
			if(in.isReadable()) {
				escapeByte.set(false);
				if(DATA_NEED==data){
					byte dataNext = in.readByte();
					packageCount.incrementAndGet();
					_doEscapeByte(dataNext,baos,false);
					return data;
				}
				_doEscapeByte(data,baos,false);
			}else {
				escapeByte.set(true);
				_doEscapeByte(data,baos,true);
			}
		}else{
			baos.write(data);
		}
		return data;
	}

	public void _doEscapeByte(byte data,ByteArrayOutputStream baos,boolean isEscape){
		if (DATA_CONVERT_0X05 == data) {
			baos.write(0xa5);
			escapeByte.set(false);
		}else if (DATA_CONVERT_0X0E == data) {
			baos.write(0xae);
			escapeByte.set(false);
		}else if (DATA_CONVERT_0X0A== data) {
			baos.write(0xaa);
			escapeByte.set(false);
		}else{
			if(!isEscape) baos.write(data);
		}
	}


	/**
	 * 先读然后在进行数据转义.
	 * @param ctx
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	protected void decodeReadByString(ChannelHandlerContext ctx, ByteBuf in, Queue<Object> out) throws Exception {
		while (in.isReadable()) {
			byte data = in.readByte();
			baos.write(data);
			boolean is0xA5 = data == DATA_BEGIN;
			boolean is0xAE = data == DATA_END;
			if(isReady && is0xA5) {//数据开头
				System.out.println("数据开头");
				checkDataAndReset(baos);
				isReady = false;
			}else if(is0xAE) {//数据结尾
				System.out.println("数据结尾");
				checkDataAndReset(baos);
				isReady = true;
				//完成数据读取...
				byte[] datas = baos.toByteArray();
				baos.reset();//重置数据,可能是无效的数据
				StringBuilder _builder = new StringBuilder();
				String msg = ByteBufUtil.hexDump(datas).toUpperCase();
				for(int i =0;i<msg.length();i++){
					if(i%2==0) _builder.append(" ");
					_builder.append(msg.charAt(i));
				}
				System.out.println("原始网络数据:"+_builder);
				//out.add(datas);
				//转换 AA05 AA0E AA0A
				msg = msg.replaceAll("AA05","A5").replaceAll("AA0E","AE").replaceAll("AA0A","AA");
				System.out.println("转义之后数据:"+msg);
				byte[] zhDatas = msg.getBytes();
				out.add(datas);
			}
		}
	}


	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(baos);
	}

}
