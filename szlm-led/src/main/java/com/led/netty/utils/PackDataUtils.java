package com.led.netty.utils;

import java.io.ByteArrayOutputStream;

import com.led.netty.pojo.AbstractCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.pojo.LoginCommand;
import com.led.netty.pojo.SetUpTimeCommand;

public class PackDataUtils {
	
	private static HeartBeatCommand heartBeatCommand = new HeartBeatCommand();
	
	public static byte[] heart = {0x55,0x13,0x00,(byte) 0xa0,0x01,0x00,0x34,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x31,0x32,(byte) 0x97,0x72,0x55};
	
	/**
	 *  1 包头 0x55
		1 长度低位
		1 长度高位
		1 命令类别
		1 命令字
		1 命令序号低位
		1 命令序号高位
		2 保留 保留
		1 状态
        8 设备 ID 16 进制,设备号,不足前面补 0
		8 Token 8Byte 通信令牌
		N 数据区 传输的数据
		1 CRC 低位
		1 CRC 高位
		1 包尾 0x55
	 */
	public static byte[] packReplayLoginData(LoginCommand loginCmd) {
		byte[] datas = null;
		byte[] bytes = loginCmd.getDataBinary();
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			bytes[9]=0x00;//设置状态
			
			/***/
			//55 25 00 a0 00 00 00 fb 2b 02 53 08 d7 b2 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 c3 13 55
			//55 25 00 A0 00 00 00 FB 2B 00 53 08 D7 B2 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 71 B8 55 
			baos.write(0x55);//包头
			baos.write(0x25);//长度低位 25个字节
			baos.write(0x00);//长度高位
			baos.write(0xa0);// 命令类别
			baos.write(0x00);// 命令字
			baos.write(0x00);// 命令序号低位
			baos.write(0x00);// 命令序号高位
			baos.write(0xfb);// 保留
			baos.write(0x2b);// 保留
			baos.write(0x00);// 状态
			//53 08 d7 b2 04 00 00 00 00 00 00 00 00 00 00 00
			baos.write(0x53);// 1
			baos.write(0x08);// 1
			baos.write(0xd7);// 1
			baos.write(0xb2);// 1
			baos.write(0x04);// 1
		
			for(int i=0;i<19;i++) {
				baos.write(0x00);
			}
			baos.write(0x71);// CRC 低位71 B8
			baos.write(0xB8);// CRC 高位			
			baos.write(0x55);//包尾
			System.out.println(baos.size());
			datas = baos.toByteArray();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}
	/**
	 * 请求设置授时
	 * @param loginCmd
	 * @return
	 */

	public static byte[] packReplaySetUpTimeData(SetUpTimeCommand stcCmd) {
		byte[] datas = null;
		byte[] bytes = stcCmd.getDataBinary();
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
			bytes[9]=0x00;//设置状态			
			/***/
			//55 1d 00 f0 89 00 00 6d 37 02 53 08 d7 b2 04 00 00 00 00 00 00 00 00 00 00 00 b9 55【请求授时】
			//55 1d 00 f0 88 00 00 6d 37 00 53 08 d7 b2 04 00 00 00 07 e2 06 09 10 32 3b 06 a7 c7 55【回复】
			//55 1D 00 F0 88 00 00 6D 37 00 53 08 D7 B2 04 00 00 00 07 E2 06 09 10 32 3B 06
			baos.write(0x55);//包头
			baos.write(0x1d);//长度低位
			baos.write(0x00);//长度高位
			baos.write(0xf0);// 命令类别
			baos.write(0x88);// 命令字
			baos.write(0x00);// 命令序号低位
			baos.write(0x00);// 命令序号高位
			baos.write(0x6d);// 保留
			baos.write(0x37);// 保留
			baos.write(0x00);// 状态
			//53 08 d7 b2 04 00 00 00 00 00 00 00 00 00 00 00
			baos.write(0x53);// 1
			baos.write(0x08);// 2
			baos.write(0xd7);// 3
			baos.write(0xb2);// 4
			baos.write(0x04);// 5
			baos.write(0x00);//6
			baos.write(0x00);//7
			baos.write(0x00);//8
			//数据区 07 e2 06 09 10 32 3b 06(年月日时分秒星期)
			baos.write(0xe2);//年
			baos.write(0x07);//年
			baos.write(0x06);//月
			baos.write(0x09);//日
			baos.write(0x10);//时
			baos.write(0x32);//分
			baos.write(0x3b);//秒
			baos.write(0x06);//星期
			//CRC效验
			baos.write(0x1E);// CRC 低位71 B8
			baos.write(0xBD);// CRC 高位			
			baos.write(0x55);//包尾
			System.out.println(baos.size());
			datas = baos.toByteArray();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}
	public static AbstractCommand binaryTransCmd(byte[] datas) {
		if(null==datas || datas.length<10) return null;//无效数据
		AbstractCommand cmd = null;
		int cmdType = datas[4];
		if(cmdType<0)cmdType+=256;
		System.out.println(cmdType);
		if(cmdType==0x00) {//登录
			cmd = new LoginCommand();
			//解析body
			//55 25 00 a0 00 00 00 fb 2b 02 
			//53 08 d7 b2 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 c3 55
			byte[] deviceId = new byte[8];
			System.arraycopy(datas, 10, deviceId, 0, 8);
			System.out.println("deviceId:"+new String(deviceId));
		}else if(cmdType==0x01) {//心跳
			//55 13 00 a0 01 00 34 30 30 30 30 30 30 30 31 32 97 72 55
			cmd = heartBeatCommand;
		}else if(cmdType==0x89) {//请求授时
			// [85, 29, 0, -16, -119, 0, 0, 109, 55, 2, 83, 8, -41, -78, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -71, 85]
			System.out.println("请求授时...");
			cmd = new SetUpTimeCommand();
		}
		//解析公共头信息...
		parseHeader(datas, cmd);
		return cmd;
	}
	
	/**
	 * 解析公共头信息...
	 * @param datas
	 * @param cmd
	 */
	public static void parseHeader(byte[] datas, AbstractCommand cmd) {
		if(cmd!=null){ //解析公共头
			cmd.setDataHeader(datas[0]);
			cmd.setDataLengthLows(datas[1]);
			cmd.setDataLengthHigh(datas[2]);
			cmd.setDataCommandCategory(datas[3]);
			cmd.setDataCommandWord(datas[4]);
			cmd.setDataCommandSequenceNumberLow(datas[5]);
			cmd.setDataCommandSequenceNumber(datas[6]);
			cmd.setDatapersist1(datas[7]);
			cmd.setDatapersist2(datas[8]);
			cmd.setDataState(datas[9]);
		}
	}

}
