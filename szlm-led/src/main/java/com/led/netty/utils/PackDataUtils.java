package com.led.netty.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.led.netty.pojo.AbstractCommand;
import com.led.netty.pojo.HeartBeatCommand;
import com.led.netty.pojo.LoginCommand;
import com.led.netty.pojo.SetUpTimeCommand;

//重启硬件、重启软件、清屏、读取目前节目、发送节目（向左滚动、向上滚动）、删除某个界面、注册、心跳
public class PackDataUtils {

	private static final Logger logger = LoggerFactory.getLogger(PackDataUtils.class);
	private static final Charset GB18030 = Charset.forName("GB18030");
	private static Map<Integer, Integer> fontSizeMap = new HashMap<Integer, Integer>();

	//open cmd 立即开屏 A5 68 32 01 76 01 00 01 00 00 00 00 00 00 00 13 01 AE
	public static byte[] PACKAGE_OPEN_CMD  = {(byte) 0xA5, 0x68, 0x32, 0x01, 0x76, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x13, 0x01, (byte) 0xAE};
	//close cmd 立即关屏
	public static byte[] PACKAGE_CLOSE_CMD = {(byte) 0xA5, 0x68, 0x32, 0x01, 0x76, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x12, 0x01, (byte) 0xAE};
	//restart app APP重启
	public static byte[] PACKAGE_RESTART_APP_CMD = {(byte) 0xA5, 0x68, 0x32, 0x01, (byte)0xFE, 0x01, 0x41, 0x50, 0x50, 0x21, (byte)0x9C, 0x02, (byte) 0xAE};
	//restart hardware 硬件重启屏
	public static byte[] PACKAGE_RESTART_HARDWARE_CMD = {(byte) 0xA5, 0x68, 0x32, 0x01, (byte)0x2D, 0x01, 0x00, (byte)0xC9, 0x00, (byte) 0xAE};

	//初始化字
	static {
		fontSizeMap.put(Integer.valueOf(0), Integer.valueOf(8));
		fontSizeMap.put(Integer.valueOf(1), Integer.valueOf(12));
		fontSizeMap.put(Integer.valueOf(2), Integer.valueOf(16));
		fontSizeMap.put(Integer.valueOf(3), Integer.valueOf(24));
		fontSizeMap.put(Integer.valueOf(4), Integer.valueOf(32));
		fontSizeMap.put(Integer.valueOf(5), Integer.valueOf(40));
		fontSizeMap.put(Integer.valueOf(6), Integer.valueOf(48));
		fontSizeMap.put(Integer.valueOf(7), Integer.valueOf(56));
	}

	//  A5 33 39 34 36 31 32 68 32 01 7b 01 0d 00 00 00 02 00 00 01 00 00 00 32 00 39 32 00 39 fd 01 AE
    // 校验码等于包类型到包数据所有数据之和

	public byte[] packSendTextToScreeCmd(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){

		} catch (Exception e) {
			return null;
		}
		return  null;
	}

	/**
	 * 根据控制卡设备ID 生成开启屏幕指令
	 * @param deviceId
	 * @return
	 */
	public static byte[] packOpenCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(cardDeviceId.length+PACKAGE_OPEN_CMD.length)){
			bos.write(0xA5);
			bos.write(cardDeviceId);
			bos.write(PACKAGE_OPEN_CMD,1,PACKAGE_OPEN_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			return PACKAGE_OPEN_CMD;
		}
	}

	/**
	 * 根据控制卡设备ID 生成关闭屏幕指令
	 * @param cardDeviceId
	 * @return
	 */
	public static byte[] packCloseCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			bos.write(cardDeviceId);
			bos.write(PACKAGE_CLOSE_CMD,1,PACKAGE_CLOSE_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			return PACKAGE_CLOSE_CMD;
		}
	}

	/**
	 * 根据控制卡设备ID 生成关闭屏幕指令
	 * @param cardDeviceId
	 * @return
	 */
	public static byte[] packRestartAppCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			bos.write(cardDeviceId);
			bos.write(PACKAGE_RESTART_APP_CMD,1,PACKAGE_RESTART_APP_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			return PACKAGE_RESTART_APP_CMD;
		}
	}


	/**
	 * 根据控制卡设备ID 生成关闭屏幕指令
	 * @param cardDeviceId
	 * @return
	 */
	public static byte[] packRestartHardWareCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			bos.write(cardDeviceId);
			bos.write(PACKAGE_RESTART_HARDWARE_CMD,1,PACKAGE_RESTART_HARDWARE_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			return PACKAGE_RESTART_HARDWARE_CMD;
		}
	}

	/**
	 * 設置屏幕亮度
	 * @param lightnessNumber
	 * @return
	 */
	public static byte[] packConfigLightness(int lightnessNumber) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xa5);//begin
			bos.write(0x68);
			bos.write(0x32);
			bos.write(0x01);
			bos.write(0x46);
			bos.write(0x01);//返回标识
			//数据
			bos.write(0x00);//包标识头
			for(int i=1;i<=24;i++){
				bos.write(lightnessNumber);
			}
			byte[] cd = bos.toByteArray();
			//计算效验码
			byte[] jym = new byte[cd.length - 1];// 校验码
			System.arraycopy(cd, 1, jym, 0, jym.length);
			int crc = calculationCRC(jym);// 校验码
			byte[] data = intToByteArray(crc);// 得到低位字节数组
			//debugData("校验码", data);
			byte[] destData = new byte[cd.length + 3];
			System.arraycopy(cd, 0, destData, 0, cd.length);
			destData[destData.length - 3] = data[0];// 低位数值
			destData[destData.length - 2] = data[1];// 高位
			destData[destData.length - 1] = (byte) 0xAE;// 固定值
			return destData;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 设置模版方式
	 * @param width
	 * @param height
	 * @return
	 */
	public synchronized static byte[] setTemplate(int width,int height){
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			//固定字符
			bos.write(0xA5);
			bos.write(0x68);
			bos.write(0x32);
			bos.write(0x01);
			bos.write(0x7B);
			bos.write(0x01);//1表示要接收返回值 0表示不要
			//数据长度
			bos.write(0x28);//
			bos.write(0x00);//
			bos.write(0x00);//当前包序号和末尾包序号，如只一包数据可固定不变
			bos.write(0x00);//当前包序号和末尾包序号，如只一包数据可固定不变
			bos.write(0x81);//设置模板指令
			bos.write(0x01);//灰度
			//int kd=196;//默认值
			//int gd=112;
			byte kd_size[] = intToByteArray(width); // 宽度
			bos.write(kd_size[1]);
			bos.write(kd_size[0]);
			//debugData("数据", kd_size);
			byte gd_size[] = intToByteArray(height);// 高度
			bos.write(gd_size[1]);
			bos.write(gd_size[0]);
			bos.write(0x01);//窗口数量
			bos.write(0x00);//保留
			bos.write(0x00);//停留时间
			bos.write(0x03);//停留时间
			bos.write(0x00);//速度
			bos.write(0x00);//文字大小
			bos.write(0x01);//文字颜色
			bos.write(0x00);//特效
			bos.write(0x00);//特效
			bos.write(0x00);//特效
			bos.write(0x47);//特效
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);//窗口起点Y坐标
			bos.write(0x00);
			bos.write(kd_size[1]);//宽度2
			bos.write(kd_size[0]);
			bos.write(gd_size[1]);//高度2
			bos.write(gd_size[0]);
			bos.write(0x01);//数据类型,01表示文本类型
			bos.write(0x00);//方式
			bos.write(0x00);//文字大小
			bos.write(0x01);//文字颜色
			bos.write(0x00);//速度
			bos.write(0x00);//停留时间
			bos.write(0x03 );//停留时间
			bos.write(0x00 );//对其方式
			bos.flush();
			byte[] cd =  bos.toByteArray();
			byte[] jym = new byte[cd.length - 1];// 校验码
			System.arraycopy(cd, 1, jym, 0, jym.length);
			int crc = calculationCRC(jym);// 校验码
			byte[] data = intToByteArray(crc);// 得到低位字节数组
			//debugData("校验码", data);
			byte[] destData = new byte[cd.length + 3];
			System.arraycopy(cd, 0, destData, 0, cd.length);
			destData[destData.length - 3] = data[0];// 低位数值
			destData[destData.length - 2] = data[1];// 高位
			destData[destData.length - 1] = (byte) 0xAE;// 固定值
			return destData;
		} catch (Exception e) {
			return null;
		}finally{
		}
	}


	/**
	 * 根据长度得到数据效验码
	 * @param data
	 * @return
	 */
	public static int calculationCRC(byte[] data) {
		int total = 0;
		for (int d : data) {
			if (d < 0) total += 256; //转换为正数
			total += d;
		}
		return total;
	}

	/**
	 * 高位转换成低位
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序
	 * @param n
	 * @return
	 */
	public static byte[] intToByteArray(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	public static String byteToHexString(byte b){
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1){
			return "0" + s;
		}else{
			return s;
		}
	}

	/**
	 * 数组转成十六进制字符串
	 * @param byte[]
	 * @return HexString
	 */
	public static String binaryToHexString(byte[] b){
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; ++i){
			buffer.append(byteToHexString(b[i]));
		}
		return buffer.toString();
	}

	public static AbstractCommand binaryTransCmd(byte[] datas) {
		return null;
	}
}
