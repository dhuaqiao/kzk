package com.led.netty.utils;

import com.led.netty.pojo.CommonCommand;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Pack工具类
 */
public class PackDataUtils {

	private static final Logger logger = LoggerFactory.getLogger(PackDataUtils.class);
	private static final Charset GB18030 = Charset.forName("GB18030");
	private static Map<Integer, Integer> fontSizeMap = new HashMap<Integer, Integer>();

	//open cmd 立即开屏 A5 68 32 01 76 01 00 01 00 00 00 00 00 00 00 13 01 AE
	public final static byte[] PACKAGE_OPEN_CMD  = {(byte) 0xA5, 0x68, 0x32, 0x01, 0x76, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x13, 0x01, (byte) 0xAE};
	//close cmd 立即关屏
	public final static byte[] PACKAGE_CLOSE_CMD = {(byte) 0xA5, 0x68, 0x32, 0x01, 0x76, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x12, 0x01, (byte) 0xAE};
	//restart app APP重启
	public final static byte[] PACKAGE_RESTART_APP_CMD = {(byte) 0xA5, 0x68, 0x32, 0x01, (byte)0xFE, 0x01, 0x41, 0x50, 0x50, 0x21, (byte)0x9C, 0x02, (byte) 0xAE};
	//restart hardware 硬件重启屏
	public final static byte[] PACKAGE_RESTART_HARDWARE_CMD = {(byte) 0xA5, 0x68, 0x32, 0x01, (byte)0x2D, 0x01, 0x00, (byte)0xC9, 0x00, (byte) 0xAE};
	//clear cmd
	public final static byte[] PACKAGE_CLEAR_HARDWARE_CMD = {(byte) 0xA5, 0x68, 0x32, 0x01, 0x7B, 0x01, 0x04, 0x00, 0x00, 0x00, 0x07, 0x00, 0x00, 0x00, 0x22, 0x01 , (byte)0xAE};
	//heart hardware 心跳回复包 A5  91 AE
	public final static byte[] PACKAGE_HEART_HARDWARE_CMD = {(byte) 0xA5, (byte)0x91, (byte) 0xAE};
	//查询状态
	public final static byte[] PACKAGE_QUERY_STATE = {(byte) 0xA5, 0x68,0x32, 0x01, 0x76, 0x01, 0x01, 0x13, 0x01 , (byte) 0xAE};
	//模版控制方法
	public final static byte[] PACKAGE_TEMPLDATE_CMD = { (byte) 0xA5, 0x68, 0x32, 0x01, 0x7B, 0x01, 0x02, 0x00, 0x00, 0x00, (byte) 0x82, 0x11, (byte) 0xAC, 0x01, (byte) 0xAE };

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

	/**
	 * 根据控制卡设备ID 生成开启屏幕指令
	 * @param cardDeviceId 控制卡id
	 * @return 生成开启屏幕指令
	 */
	public static byte[] packOpenCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(cardDeviceId.length+PACKAGE_OPEN_CMD.length)){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(PACKAGE_OPEN_CMD,1,PACKAGE_OPEN_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packOpenCmdByCardDeviceId :{}",e);
			return null;
		}
	}

	/**
	 * 根据控制卡设备ID 生成开启屏幕指令
	 * @param cardDeviceId 控制卡id
	 * @return 生成开启屏幕指令
	 */
	public static byte[] packQueryCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(cardDeviceId.length+PACKAGE_QUERY_STATE.length)){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(PACKAGE_QUERY_STATE,1,PACKAGE_QUERY_STATE.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packQueryCmdByCardDeviceId :{}",e);
			return null;
		}
	}

	/**
	 * 根据控制卡设备ID 生成关闭屏幕指令
	 * @param cardDeviceId 控制卡id
	 * @return 关闭屏幕指令
	 */
	public static byte[] packCloseCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(PACKAGE_CLOSE_CMD,1,PACKAGE_CLOSE_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packCloseCmdByCardDeviceId :{}",e);
			return null;
		}
	}

	/**
	 * 根据控制卡设备ID 生成清除屏幕指令
	 * @param cardDeviceId 控制卡id
	 * @return 清除屏幕指令
	 */
	public static byte[] packClearCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(PACKAGE_CLEAR_HARDWARE_CMD,1,PACKAGE_CLEAR_HARDWARE_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packClearCmdByCardDeviceId :{}",e);
			return null;
		}
	}

	/**
	 * 根据控制卡设备ID 重启APP
	 * @param cardDeviceId 控制卡id
	 * @return 重启APP
	 */
	public static byte[] packRestartAppCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(PACKAGE_RESTART_APP_CMD,1,PACKAGE_RESTART_APP_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packRestartAppCmdByCardDeviceId :{}",e);
			return null;
		}
	}


	/**
	 * 根据控制卡设备ID 重启硬件指令
	 * @param cardDeviceId 控制卡id
	 * @return 重启硬件指令
	 */
	public static byte[] packRestartHardWareCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(PACKAGE_RESTART_HARDWARE_CMD,1,PACKAGE_RESTART_HARDWARE_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packRestartHardWareCmdByCardDeviceId :{}",e);
			return null;
		}
	}

	/**
	 * 根据控制卡设备ID 生成心跳指令
	 * @param cardDeviceId 控制卡id
	 * @return 生成心跳指令
	 */
	public static byte[] packHeartHardWareCmdByCardDeviceId(byte[] cardDeviceId){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(PACKAGE_HEART_HARDWARE_CMD,1,PACKAGE_HEART_HARDWARE_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packHeartHardWareCmdByCardDeviceId :{}",e);
			return null;
		}
	}

	/**
	 *  数据包加入设备标识
	 * @param cardDeviceId  控制卡id
	 * @param binary 数据包内容(不带控制卡id)
	 * @return
	 */
	public static byte[] packDataAddCardDeviceId(byte[] cardDeviceId,byte[] binary){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
			bos.write(binary,1,binary.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 查询开关屏信息
	 * @param cardDeviceId 控制卡id
	 * @return
	public static byte[] packConfigQueryState(byte[] cardDeviceId) {
		// a5 68 32 01 76 01 01 13 01 ae
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();ByteArrayOutputStream bosCc = new ByteArrayOutputStream()){
			bos.write(0xA5);
			int cardIdLength = 0;
			if(Objects.nonNull(cardDeviceId)){//设置控制卡id
				bos.write(cardDeviceId);
				cardIdLength = cardDeviceId.length;
			}
			bos.write(0x68);
			bos.write(0x32);
			bos.write(0x01);
			bos.write(0x7B);
			bos.write(0x01);//1表示要接收返回值 0表示不要
			bosCc.write(0x76); //CMD
			bosCc.write(0x01); //0x01: 查询软件开关屏信息
			byte[] textBinary = bosCc.toByteArray();
			byte[] dataCc = PackDataUtils.intToByteArray(textBinary.length);
			bos.write(dataCc[0]);//包数据长度LL LH
			bos.write(dataCc[1]);//二字节的长度数，表示后面“CC。。。。。。”内容部分的长度，低字节在前
			bos.write(0x00);//包序号PO
			bos.write(0x00);//最末包序号TP
			bos.write(textBinary);
			byte[] cd =  bos.toByteArray();
			byte[] jym = new byte[cd.length - 1-cardIdLength];// 校验码
			System.arraycopy(cd, 1+cardIdLength, jym, 0, jym.length);
			int crc = PackDataUtils.calculationCRC(jym);// 校验码
			byte[] data = PackDataUtils.intToByteArray(crc);// 得到低位字节数组
			byte[] destData = new byte[cd.length + 3];
			System.arraycopy(cd, 0, destData, 0, cd.length);
			destData[destData.length - 3] = data[0];// 低位数值
			destData[destData.length - 2] = data[1];// 高位
			destData[destData.length - 1] = (byte) 0xAE;// 固定值
			bos.reset();
			bos.write(0xa5);
			transcoding(bos, destData);
			bos.write(0xae);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packConfigQueryState :{}",e);
			return null;
		}
	}
	*/

	/**
	 * 設置屏幕亮度
	 * @param lightnessNumber lightnessNumber 参数 0-31
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
			bos.write(0x00);//包标识头
			for(int i=1;i<=24;i++){
				bos.write(lightnessNumber);
			}
			byte[] cd = bos.toByteArray();
			byte[] jym = new byte[cd.length - 1];// 校验码
			System.arraycopy(cd, 1, jym, 0, jym.length);
			int crc = calculationCRC(jym);// 校验码
			byte[] data = intToByteArray(crc);// 得到低位字节数组
			byte[] destData = new byte[cd.length + 3];
			System.arraycopy(cd, 0, destData, 0, cd.length);
			destData[destData.length - 3] = data[0];// 低位数值
			destData[destData.length - 2] = data[1];// 高位
			destData[destData.length - 1] = (byte) 0xAE;// 固定值
			return destData;
		} catch (Exception e) {
			logger.error("packConfigLightness :{}",e);
			return null;
		}
	}

	/**
	 * 设置模版方式
	 * @param width
	 * @param height
	 * @return
	 */
	public static byte[] setTemplate(int width,int height){
		return setTemplate(width, height,null);
	}
	/**
	 * 设置模版方式
	 * @param width 宽度
	 * @param height 高度
	 * @param cardDeviceId 控制卡id
	 * @return 设置模版指令
	 */
	public static byte[] setTemplate(int width,int height,byte[] cardDeviceId){
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			//固定字符
			bos.write(0xA5);
			//设置控制卡id
			if(Objects.nonNull(cardDeviceId)){
				bos.write(cardDeviceId);
			}
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
			byte kd_size[] = intToByteArray(width); // 宽度
			bos.write(kd_size[1]);
			bos.write(kd_size[0]);
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
			byte[] dataSource =  bos.toByteArray();
			byte[] jym = new byte[dataSource.length - 1-cardDeviceId.length];// 校验码
			System.arraycopy(dataSource, 1+cardDeviceId.length, jym, 0, jym.length);
			int crc = calculationCRC(jym);// 校验码
			byte[] data = intToByteArray(crc);// 得到低位字节数组
			byte[] destData = new byte[dataSource.length + 3];
			System.arraycopy(dataSource, 0, destData, 0, dataSource.length);
			destData[destData.length - 3] = data[0];// 低位数值
			destData[destData.length - 2] = data[1];// 高位
			destData[destData.length - 1] = (byte) 0xAE;// 固定值
			return destData;
		} catch (Exception e) {
			logger.error("stepTemplate :{}",e);
			return null;
		}
	}

	/**
	 * 进入节目模板方式
	 * @return
	 */
	public static byte[] stepIntoTemplate(byte[] cardDeviceId) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			bos.write(0xA5);
			bos.write(cardDeviceId);
			bos.write(PACKAGE_TEMPLDATE_CMD,1,PACKAGE_TEMPLDATE_CMD.length-1);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("stepIntoTemplate :{}",e);
			return null;
		}
	}

	/**
	 * 进入节目模板方式
	 * @return
	 */
	public static byte[] stepIntoTemplate() {
		return PACKAGE_TEMPLDATE_CMD;
	}

	/**
	 * 打包 单个内容
	 * @param width
	 * @param height
	 * @param xh
	 * @param font
	 * @param time
	 * @param sd
	 * @param hy
	 * @param content
	 * @return
	 */
	public static List<byte[]> packSubcontract(int width, int height, int xh,int font, int time, int sd, int hy, String content) {
		return packSubcontract(width, height, xh, font, time, sd, hy, content,null,null);
	}

	/**
	 * 删除节目：CC=0x84: items为空或者{} 则删除全部 否则则删除指定的节目号
	 * @param cardDeviceId 设备控制卡
	 * @param items 节目号集合
	 * @return
	 */
	public static byte[] packDeleteItem(byte[] cardDeviceId,int... items){
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();ByteArrayOutputStream bosCc = new ByteArrayOutputStream()){
			bos.write(0xA5);
			int cardIdLength = 0;
			if(Objects.nonNull(cardDeviceId)){//设置控制卡id
				bos.write(cardDeviceId);
				cardIdLength = cardDeviceId.length;
			}
			bos.write(0x68);
			bos.write(0x32);
			bos.write(0x01);
			bos.write(0x7B);
			bos.write(0x01);//1表示要接收返回值 0表示不要
			bosCc.write(0x84); //CMD
			if(null==items || items.length==0){
				bosCc.write(0x00);//删除全部
			}else{
				bosCc.write(0x01); //删除指定
				bosCc.write(items.length);
				for(int i=0;i<items.length;i++){
					bosCc.write(items[i]);
				}
			}
			byte[] textBinary = bosCc.toByteArray();
			byte[] dataText = PackDataUtils.intToByteArray(textBinary.length);
			bos.write(dataText[0]);
			bos.write(dataText[1]);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(textBinary);
			byte[] cd =  bos.toByteArray();
			byte[] jym = new byte[cd.length - 1-cardIdLength];// 校验码
			System.arraycopy(cd, 1+cardIdLength, jym, 0, jym.length);
			int crc = PackDataUtils.calculationCRC(jym);// 校验码
			byte[] data = PackDataUtils.intToByteArray(crc);// 得到低位字节数组
			byte[] destData = new byte[cd.length + 3];
			System.arraycopy(cd, 0, destData, 0, cd.length);
			destData[destData.length - 3] = data[0];// 低位数值
			destData[destData.length - 2] = data[1];// 高位
			destData[destData.length - 1] = (byte) 0xAE;// 固定值
			bos.reset();
			bos.write(0xa5);
			transcoding(bos, destData);
			bos.write(0xae);
			return bos.toByteArray();
		} catch (Exception e) {
			logger.error("packDeleteItem :{}",e);
			return null;
		}
	}

	/**
	 *  打包 带有标题 内容 结尾的样式
	 * @param width
	 * @param height
	 * @param xh
	 * @param font
	 * @param time
	 * @param sd
	 * @param hy
	 * @param title
	 * @param content
	 * @param inscribed
	 * @return
	 */
	public static List<byte[]> packSubcontract(int width, int height, int xh,int font, int time, int sd, int hy, String title,String content, String inscribed) {
		logger.info(("设备宽度：{} 设备高度：{} 节目序号为：{}"), new Object[] { width, height, xh });
		Charset GB18030 = Charset.forName("GB18030");
		List<byte[]> packList = new CopyOnWriteArrayList<byte[]>();
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			StringBuilder builderText = new StringBuilder();
			if(title!=null && !"".equals(title.trim())){
				calculateDataOnScreen(width, font, title, builderText);
			}
			if(content!=null && !"".equals(content.trim())){
				builderText.append(content);//添加内容
			}
			if(inscribed!=null && !"".equals(inscribed.trim())){//添加4个空格
				builderText.append(inscribed);//落款
			}
			bos.write(0x88);// CC 0x88 发送独立节目
			bos.write(0x00);// 用户附加码，固定
			bos.write(0x00);// 用户附加码，固定
			bos.write(0x00);// 用户附加码，固定
			bos.write(0x01);// 用户附加码，固定
			bos.write(xh);// 节目序号<!--序号----->
			bos.write(0x00);// 控制属性，固定
			bos.write(0x00);// 保留，固定
			bos.write(0x00);// 保留，固定
			bos.write(0x00);// 保留，固定
			bos.write(0x01);// 窗口数据
			bos.write(0x00);// 窗口起始坐标点，固定
			bos.write(0x00);// 窗口起始坐标点，固定
			//窗口起点Y。高字节在前
			bos.write(0x00);// 窗口起始坐标点，固定
			bos.write(0x00);// 窗口起始坐标点，固定
			//加入其他
			byte kd_size[] = intToByteArray(width); // 宽度
			//窗口宽度。高字节在前
			bos.write(kd_size[1]);
			bos.write(kd_size[0]);
			byte gd_size[] = intToByteArray(height);// 高度
			//窗口高度。高字节在前
			bos.write(gd_size[1]);
			bos.write(gd_size[0]);
			//窗口缺省类型以及参数
			bos.write(0x01);// 代表数据是文本类型，固定
			bos.write(hy); // 特效，显示方式
			bos.write(font); // 文字大小
			bos.write(0x01);// 文字颜色，固定
			bos.write(sd); // 速度
			bos.write(0x00);// 00 03 停留时间/滚动重复
			bos.write(0x03);// 00 03 停留时间/滚动重复
			bos.write(0x00);//对齐方式
			//窗口数据偏移
			bos.write(0x00);// 窗口偏移，固定
			bos.write(0x00);// 窗口偏移，固定
			bos.write(0x00);// 窗口偏移，固定
			//定义包的大小
			//int sizeData = 800;
			int sizeData = 500;
			byte[] msgDataInfo = builderText.toString().getBytes(GB18030);
			byte textBinary[] = intToByteArray(msgDataInfo.length);
			bos.write(textBinary[2]);
			bos.write(textBinary[1]);
			bos.write(textBinary[0]);
			//写入固定数据
			bos.write(0x01);
			bos.write(0x00);
			byte[] commHeader = bos.toByteArray();//Header包
			//内容...
			int sizeCd = msgDataInfo.length + commHeader.length;//0x88开始的数据包
			byte[] newMsgDataInfo =  new byte[sizeCd];
			System.arraycopy(commHeader, 0, newMsgDataInfo, 0, commHeader.length);
			System.arraycopy(msgDataInfo, 0, newMsgDataInfo, commHeader.length, msgDataInfo.length);
			msgDataInfo = newMsgDataInfo;
			int lastPackSize = sizeCd % sizeData;
			int dsCount = lastPackSize == 0 ? sizeCd / sizeData : sizeCd / sizeData+ 1;
			logger.info("数据包分包数量: {}",dsCount);
			calculateSubpackage(bos, packList, sizeData, msgDataInfo, sizeCd, lastPackSize, dsCount);
			return packList;
		} catch (Exception e) {
			logger.error("组装数据包出错:{}", e);
			return packList;
		}
	}

	private static void calculateSubpackage(ByteArrayOutputStream bos, List<byte[]> packList, int sizeData, byte[] msgDataInfo, int sizeCd, int lastPackSize, int dsCount) throws IOException {
		for (int i = 0; i < dsCount; i++) {
			bos.reset();
			bos.write(0xA5);
			bos.write(0x68);
			bos.write(0x32);
			bos.write(0x01);
			bos.write(0x7B);
			bos.write(0x01);
			if (i != dsCount - 1) {
				byte[] tmp = intToByteArray(sizeData);
				bos.write(tmp[0]);
				bos.write(tmp[1]);
			} else {
				byte[] tmp = intToByteArray(lastPackSize);
				bos.write(tmp[0]);
				bos.write(tmp[1]);
			}
			//包序号PO
			bos.write(i);
			//最末包序号TP
			bos.write(dsCount - 1);
			byte[] jym = (byte[]) null;
			if ((i != dsCount - 1) && (sizeCd >= sizeData)) {
				jym = new byte[sizeData];
			} else {
				jym = new byte[lastPackSize];
			}
			System.arraycopy(msgDataInfo, i * sizeData, jym, 0, jym.length);
			bos.write(jym);
			byte[] arrayData = bos.toByteArray();
			byte[] jymData = new byte[arrayData.length - 1];
			System.arraycopy(arrayData, 1, jymData, 0, jymData.length);
			int crc = calculationCRC(jymData);
			byte[] data = intToByteArray(crc);
			bos.write(data[0]);
			bos.write(data[1]);
			bos.write(0xae);
			byte[] packBinary = bos.toByteArray();
			bos.reset();
			bos.write(0xa5);
			transcoding(bos, packBinary);
			bos.write(0xae);
			packBinary = bos.toByteArray();
			packList.add(packBinary);
		}
	}

	private static void calculateDataOnScreen(int width, int font, String title, StringBuilder builderText) {
		int fontSize = 16;
		switch (font) {
			case 2:
				fontSize = 16;
				break;
			case 3:
				fontSize = 24;
				break;
			case 4:
				fontSize = 32;
				break;
			case 5:
				fontSize = 40;
				break;
			case 6:
				fontSize = 48;
				break;
			case 7:
				fontSize = 56;
				break;
		}
		int maxFontSize = width / fontSize;// 一行最多显示的字数
		int titleFontSize = title.length();// 标题的长度
		int countFonts = maxFontSize - titleFontSize;// 添加空格数
		countFonts = countFonts > 0 ? countFonts == 1 ? 1 : countFonts / 2 : 0;// 标题添加多少空格
		boolean isZc = maxFontSize % 2 == 0;
		for (int count = 0; count < countFonts; count++) {
			if (isZc) {
				builderText.append("  ");
			} else {
				builderText.append(" ");
			}
		}
		builderText.append(title).append("\r\n");//添加标题
	}

	/**
	 *
	 * @param width
	 * @param height
	 * @param xh
	 * @param font 4 32号字体
	 * @param time
	 * @param sd
	 * @param hy
	 * @param title
	 * @param content
	 * @param inscribed
	 * @return
	 */
	public static List<byte[]> packData0x85(int width, int height, int xh,int font, int time, int sd, int hy, String title,String content, String inscribed) {
		logger.info(("设备宽度：{} 设备高度：{} 节目序号为：{}"), new Object[] { width, height, xh });
		Charset GB18030 = Charset.forName("GB18030");
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			StringBuilder sbuilderInfo = new StringBuilder();
			if(StringUtils.hasText(title)){
				calculateDataOnScreen(width, font, title, sbuilderInfo);
			}
			if(content!=null && !"".equals(content.trim())){
				sbuilderInfo.append(content);//添加内容
			}
			if(inscribed!=null && !"".equals(inscribed.trim())){//添加4个空格
				sbuilderInfo.append(inscribed);//落款
			}
			System.out.println("完整信息:"+sbuilderInfo);
			/**
			 * 开始分包
			 */
			bos.write(0x85);// CC 0x85 发送独立节目
			bos.write(0x00);// 用户附加码，固定
			bos.write(0x00);// 用户附加码，固定
			bos.write(0x00);// 用户附加码，固定
			bos.write(0x01);// 用户附加码，固定
			bos.write(xh);// 节目序号<!--序号----->
			bos.write(0x00); //普通节目
			/**
			bos.write(0x01);// 窗口号
			bos.write(0x01);// 控制属性，固定
			//显示格式
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x01);
			bos.write(0x01);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			bos.write(0x00);
			 */
			//内容
			byte[] msgDataInfo = sbuilderInfo.toString().getBytes(GB18030);
			bos.write(msgDataInfo);
			bos.write(0x00);

			List<byte[]> packList = new CopyOnWriteArrayList<byte[]>();
			//定义包的大小
			//int sizeData = 800;
			int sizeData = 300;

			byte[] commHeader = bos.toByteArray();//Header包
			//内容...
			int sizeCd = msgDataInfo.length + commHeader.length;//0x88开始的数据包
			byte[] newMsgDataInfo =  new byte[sizeCd];
			System.arraycopy(commHeader, 0, newMsgDataInfo, 0, commHeader.length);
			System.arraycopy(msgDataInfo, 0, newMsgDataInfo, commHeader.length, msgDataInfo.length);
			msgDataInfo = newMsgDataInfo;
			int lastPack = sizeCd % sizeData;
			int dsCount = lastPack == 0 ? sizeCd / sizeData : sizeCd / sizeData+ 1;
			System.out.println("数据包数量: " + dsCount);
			logger.info("数据包分包数量: {}",dsCount);
			calculateSubpackage(bos, packList, sizeData, msgDataInfo, sizeCd, lastPack, dsCount);
			return packList;
		} catch (Exception e) {
			logger.error("组装数据包出错:{}", e);
			return null;
		}
	}


	public static byte[] pack0X85(String title){
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream();ByteArrayOutputStream bosCc = new ByteArrayOutputStream()){
			//固定字符
			bos.write(0xA5);
			bos.write(0x68);
			bos.write(0x32);
			bos.write(0x01);
			bos.write(0x7B);
			bos.write(0x01);//1表示要接收返回值 0表示不要
			//
			//包数据长度LL LH	0x0000~0xffff	2	二字节的长度数，表示后面“CC。。。。。。”内容部分的长度，低字节在前
			//包序号PO 	0x00~0x255	1	当包序号等于最末包序号时，表明这是最后一个包。
			//最末包序号TP	0x00~0x255	1	总包数减去1。
			//包数据	CC 。。。。。。	变长	协议命令和数据
			//CC
			bosCc.write(0x85);
			//用户附加码
			bosCc.write(0x00);
			bosCc.write(0x00);
			bosCc.write(0x00);
			bosCc.write(0x01);
			//节目号
			bosCc.write(0x01);
			//窗口号
			bosCc.write(0x01);
			//属性
			bosCc.write(0x00);
			//文字数据
			//bosCc.write("hello_1".getBytes());
			bosCc.write(title.getBytes());
			bosCc.write(0x00);
			byte[] textBinary = bosCc.toByteArray();

			//写入内容
			byte[] dataText = PackDataUtils.intToByteArray(textBinary.length); //得到低位字节数组
			//包数据长度LL LH	0x0000~0xffff	2	二字节的长度数，表示后面“CC。。。。。。”内容部分的长度，低字节在前
			//包序号PO 	0x00~0x255	1	当包序号等于最末包序号时，表明这是最后一个包。
			//最末包序号TP	0x00~0x255	1	总包数减去1。
			bos.write(dataText[0]);
			bos.write(dataText[1]);
			//包序号PO
			bos.write(0x00);
			//最末包序号TP
			bos.write(0x00);
			//内容
			bos.write(textBinary);
			/**
			 数据项	数值	长度(字节)	意义描述
			 CC	0x85	1	说明本数据包是发送文本
			 用户附加码		4	用户给出的附加码。高字节在前
			 节目号		1	有效值1～100
			 窗口号		1	有效值1～10，超出节目模板定义时无效
			 属性		1	Bit0~3: 文本类型
			 0：普通文本
			 1：格式文本
			 Bit4: 显示格式。0缺省格式,1指定格式
			 Bit5: 是否立即播放。（1立即播放）
			 Bit6~7: 保留
			 显示格式

			 (注意：当”属性”的显示格式为0时不需要此项数据)	停留时间/滚动重复次数	2	停留时间/滚动重复次数。高字节在前。显示特效为“滚动”方式时表示滚动重复次数(0滚动1次，1滚动2次，…)，其它显示特效时是特效展示完之后的停留时间，单位是秒。
			 速度	1	速度。该值越小，速度越快
			 文字大小	1	Bit 0~3:文字大小。参见”文字大小编码”
			 Bit 4~6:文字类型，参见文字类型编码
			 文字颜色	1	文字颜色。参见单字节”文字颜色编码”
			 显示特效	1	显示特效。参见”显示特效编码”
			 对齐	1	文本的对齐方式、行间距
			 Bit0~1: 水平对齐。0左，1中，2右
			 Bit2~3: 垂直对齐。0上，1中，2下
			 Bit4~7: 行间距0~15点
			 保留	1	保留备用，填0
			 文字数据		变长	文字数据根据文本类型不同而不同，文本类型见“属性”的定义。
			 普通文本：
			 文字串内容，以0x00结束。
			 格式文本：
			 第一字节是0x01,后面跟随的是Rich3文本，详细说明见格式化文本数据部分。
			 */

			bos.flush();
			byte[] cd =  bos.toByteArray();
			byte[] jym = new byte[cd.length - 1];// 校验码
			System.arraycopy(cd, 1, jym, 0, jym.length);
			int crc = PackDataUtils.calculationCRC(jym);// 校验码
			byte[] data = PackDataUtils.intToByteArray(crc);// 得到低位字节数组
			byte[] destData = new byte[cd.length + 3];
			System.arraycopy(cd, 0, destData, 0, cd.length);
			destData[destData.length - 3] = data[0];// 低位数值
			destData[destData.length - 2] = data[1];// 高位
			destData[destData.length - 1] = (byte) 0xAE;// 固定值

			bos.reset();
			bos.write(0xa5);
			transcoding(bos, destData);
			bos.write(0xae);
			destData = bos.toByteArray();
			return destData;
		}catch (Exception e){
			return null;
		}
	}

	// 0xa5  0xaa 0x05。目的是避免与起始符0xa5相同
	// 0xae  0xaa 0x0e。目的是避免与结束符0xae相同。
	// 0xaa  0xaa 0x0a。目的是避免与转义符0xaa相同。
	private static void transcoding(ByteArrayOutputStream bos, byte[] destData) {
		for (int j = 1; j < destData.length - 1; j++) {
			if (destData[j] == -91) {
				bos.write(0xaa);
				bos.write(0x05);
			} else if (destData[j] == -82) {
				bos.write(0xaa);
				bos.write(0x0e);
			} else if (destData[j] == -86) {
				bos.write(0xaa);
				bos.write(0x0a);
			} else {
				bos.write(destData[j]);
			}
		}
	}

	/**
	 * 根据长度得到数据效验码
	 * @param data
	 * @return
	 */
	public static int calculationCRC(byte[] data) {
		int sum = 0;
		for (int d : data) {
			if (d < 0) sum += 256; //转换为正数
			sum += d;
		}
		return sum;
	}

	/**
	 * 高位转换成低位
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序
	 * @param val
	 * @return
	 */
	public static byte[] intToByteArray(int val) {
		byte[] data = new byte[4];
		data[0] = (byte) (val & 0xff);
		data[1] = (byte) (val >> 8 & 0xff);
		data[2] = (byte) (val >> 16 & 0xff);
		data[3] = (byte) (val >> 24 & 0xff);
		return data;
	}

	public static String byteToHexString(byte b){
		String hex = Integer.toHexString(b & 0xFF).toUpperCase();
		if (hex.length() == 1){
			return "0" + hex;
		}else{
			return hex;
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
			if(i%1==0)buffer.append(" ");
			buffer.append(byteToHexString(b[i]));
		}
		return buffer.toString();
	}

	/**
	 * 转换为Cmd
	 * @param datas 内容
	 * @param cardId 控制卡id
	 * @param datagramPacket packet
	 * @return CMD 命令
	 */
	public static CommonCommand binaryTransCmd(byte[] datas, byte[] cardId, DatagramPacket datagramPacket) {
		CommonCommand cmd = new CommonCommand();
		cmd.setDataBinary(datas);
		cmd.setDataCardId(cardId);
		cmd.setDatagramPacket(datagramPacket);
		return cmd;
	}


}
