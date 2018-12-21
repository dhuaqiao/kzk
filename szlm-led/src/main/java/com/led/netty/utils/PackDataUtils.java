package com.led.netty.utils;

import com.led.netty.pojo.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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
	//模版控制方法
	public static byte[] PACKAGE_TEMPLDATE_CMD = { (byte) 0xA5, 0x68, 0x32, 0x01, 0x7B, 0x01, 0x02, 0x00, 0x00, 0x00, (byte) 0x82, 0x11, (byte) 0xAC, 0x01, (byte) 0xAE };

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
	public static byte[] setTemplate(int width,int height){
		return setTemplate(width, height,null);
	}
	/**
	 * 设置模版方式
	 * @param width 宽度
	 * @param height 高度
	 * @param cardDeviceId 控制卡id
	 * @return
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
		}
	}

	/**
	 *
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
			return PACKAGE_TEMPLDATE_CMD;
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
	 * 组装流明控制卡数据包
	 * 分多包的数据情况
	 * @param info
	 * @return
	 */
	public static List<byte[]> _packData() {
		/**0x00: 立即显示
		 0x01: 从左边展开
		 0x02: 从左边展开
		 0x03: 从中间向两边展开
		 0x04: 从中间向上下展开
		 0x05: 分段展开
		 0x06: 向左边移出
		 0x07: 向右边移出
		 0x08: 向上边移出
		 0x09: 向下边移出
		 0x0A: 向上滚动
		 0x0B: 向左滚动
		 0x0C: 向右滚动*/
		//组装相关的指令信息 相关的逻辑判断
		int width=128;
		int height=48;
		int xh=1;//节目序号
		byte font = 24;// 文字大小 16号2,24号3,32号4
		int time = 3;//停留时间?
		byte sd = 3;// 速度 1～100 1 数值越小，速度越快 1:最快2:快速3:中速4:慢速5:最慢
		switch(sd){
			case 1:
				sd =0;//最快
				break;
			case 2:
				sd = 3;//快速
				break;
			case 3:
				sd = 10;//中速
				break;
			case 4:
				sd = 50;//慢速
				break;
			case 5:
				sd = 100;//最慢
				break;
			default:
				sd = 0;//立即打出
				break;
		}
		byte hy=1;//显示花样 1:左移2:右移3:上移4:下移5:立即打出
		switch(hy){
			case 1:
				hy =0x06;//左移
				break;
			case 2:
				hy = 0x07;//右移
				break;
			case 3:
				hy = 0x08;//上移
				break;
			case 4:
				hy = 0x09;//下移
				break;
			case 5:
				hy = 0;//立即打出
				break;
			default:
				hy = 0;//立即打出
				break;
		}
		//hy = 63;
		String title = "title";
		String content = "getContent";
		String inscribed = "getInscribed";
		for (int i = 1; i <= 10; i++) {
			title += "title>" + i;
			content += "content >" + i;
			inscribed += "content" + i;
		}
		return packDataByMany(width, height, xh, font, time, sd, hy, title, content, inscribed);
	}


	/**
	 * 组装数据包
	 * 分多包的数据情况
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param xh
	 *            序号
	 * @param font
	 *            字体
	 * @param time
	 *            时间
	 * @param sd
	 *            速度
	 * @param hy
	 *            花样
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param inscribed
	 *            落款
	 * @return
	 */

	public static List<byte[]> packDataByMany(int width, int height, int xh,byte font, int time, byte sd, byte hy, String title,String content, String inscribed) {
		logger.info(("设备宽度：{} 设备高度：{} 节目序号为：{}"), new Object[] { width, height, xh });
		Charset GB18030 = Charset.forName("GB18030");
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			StringBuilder sbuilderInfo = new StringBuilder();
			if(title!=null && !"".equals(title.trim())){
				int fontSize = 16;
				switch(font){
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
				countFonts = countFonts > 0 ? countFonts==1 ? 1 : countFonts / 2 : 0;// 标题添加多少空格
				boolean isZc = maxFontSize%2==0;
				for (int count = 0; count < countFonts; count++) {
					if(isZc){
						sbuilderInfo.append("  ");
					}else{
						sbuilderInfo.append(" ");
					}
				}
				sbuilderInfo.append(title).append("\r\n");//添加标题
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
			bos.write(0x01);// 窗口数据，这里只分一个窗口，固定
			//窗口信息表数据
			//附录1：窗口位置及属性
			//窗口起点X。高字节在前
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
			//17~19字节为窗口数据偏移，20~22字节为窗口数据长度。高字节在前。无数据则偏移和长度均为0.
			//窗口数据偏移
			bos.write(0x00);// 窗口偏移，固定
			bos.write(0x00);// 窗口偏移，固定
			bos.write(0x00);// 窗口偏移，固定
			List<byte[]> packList = new CopyOnWriteArrayList<byte[]>();
			//定义包的大小
			//int sizeData = 800;
			int sizeData = 300;
			//sizeData = 200;
			//11+22*1 = 33
			//数据区域长度
			//窗口数据长度
			byte[] msgDataInfo = sbuilderInfo.toString().getBytes(GB18030);
			byte wznr_length[] = intToByteArray(msgDataInfo.length);
			bos.write(wznr_length[2]);
			bos.write(wznr_length[1]);
			bos.write(wznr_length[0]);
			//写入固定数据
			//窗口数据		变长	窗口要播放的数据，如“文本”、“图片”等。第1字节：数据类型(1文本；4图片)第2字节：数据格式（同发文本或发图片的定义）第3字节起：文本或图片数据。
			bos.write(0x01);
			bos.write(0x00);
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
			for (int i = 0; i < dsCount; i++) {
				bos.reset();
				bos.write(0xA5);
				bos.write(0x68);
				bos.write(0x32);
				bos.write(0x01);
				bos.write(0x7B);
				bos.write(0x01);
				//写固定长度数据 写入包的数据长度信息
				if (i != dsCount - 1) {
					byte[] tmp = intToByteArray(sizeData);
					bos.write(tmp[0]);
					bos.write(tmp[1]);
				} else {
					byte[] tmp = intToByteArray(lastPack);
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
				} else{
					jym = new byte[lastPack];
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
				byte[] tmpArrays = bos.toByteArray();
				bos.reset();
				bos.write(0xa5);
				// 0xa5  0xaa 0x05。目的是避免与起始符0xa5相同
				// 0xae  0xaa 0x0e。目的是避免与结束符0xae相同。
				// 0xaa  0xaa 0x0a。目的是避免与转义符0xaa相同。
				for (int j = 1; j < tmpArrays.length - 1; j++) {
					if (tmpArrays[j] == -91) {
						bos.write(0xaa);
						bos.write(0x05);
					} else if (tmpArrays[j] == -82) {
						bos.write(0xaa);
						bos.write(0x0e);
					} else if (tmpArrays[j] == -86) {
						bos.write(0xaa);
						bos.write(0x0a);
					} else {
						bos.write(tmpArrays[j]);
					}
				}
				bos.write(0xae);
				tmpArrays = bos.toByteArray();
				packList.add(tmpArrays);
			}
			return packList;
		} catch (Exception e) {
			logger.error("组装数据包出错:{}", e);
			return null;
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
