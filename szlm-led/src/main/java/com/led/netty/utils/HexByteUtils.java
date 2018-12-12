package com.led.netty.utils;

public class HexByteUtils {
	
	
	/**
     * 计算CRC16校验码
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }
    
    public static int getCrc16(byte[] arr_buff) {  
        int len = arr_buff.length;  
          
        //预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。  
        int crc = 0xFFFF;  
        int i, j;  
        for (i = 0; i < len; i++) {  
            //把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器  
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));  
            for (j = 0; j < 8; j++) {  
                //把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位  
                if ((crc & 0x0001) > 0) {  
                    //如果移出位为 1, CRC寄存器与多项式A001进行异或  
                    crc = crc >> 1;  
                    crc = crc ^ 0xA001;  
                } else  
                    //如果移出位为 0,再次右移一位  
                    crc = crc >> 1;  
            }  
        }  
        return crc;  
    }  
	
	// 十进制字符串转十六进制：
	/**
	 * 十进制字符串转十六进制
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param strPart
	 * @return
	 */
	public static String stringToHexString(String strPart) {
		String hexString = "";
		for (int i = 0; i < strPart.length(); i++) {
			int ch = (int) strPart.charAt(i);
			String strHex = Integer.toHexString(ch);
			hexString = hexString + strHex;
		}
		return hexString;
	}

	// 十六进制字符串转byte数组：
	/**
	 * 十六进制字符串转byte数组
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param s
	 * @return
	 */
	public static byte[] hexToBytes(String s) {
		s = s.toUpperCase();
		int len = s.length() / 2;
		int ii = 0;
		byte[] bs = new byte[len];
		char c;
		int h;
		for (int i = 0; i < len; i++) {
			c = s.charAt(ii++);
			if (c <= '9') {
				h = c - '0';
			} else {
				h = c - 'A' + 10;
			}
			h <<= 4;
			c = s.charAt(ii++);
			if (c <= '9') {
				h |= c - '0';
			} else {
				h |= c - 'A' + 10;
			}
			bs[i] = (byte) h;
		}
		return bs;
	}

	// byte数组转十进制字符串：
	/**
	 * byte数组转十进制字符串
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param bs
	 * @return
	 */
	public static String bytes2string(byte[] bs) {
		char[] cs = new char[bs.length];
		for (int p = 0; p < bs.length; p++) {
			cs[p] = (char) (bs[p] & 0xFF);
		}
		return new String(cs);
	}

	// byte数组转十六进制字符串：
	/**
	 * byte数组转十六进制字符串
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：</B><BR>
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

}
