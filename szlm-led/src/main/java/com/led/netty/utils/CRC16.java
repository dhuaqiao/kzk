package com.led.netty.utils;

/**
 * 
 * @author CRC
CRC16的C语言算法：
#define PRESET_VALUE 0xFFFF
#define POLYNOMIAL  0x8408
unsigned int uiCrc16Cal(unsigned char const * pucY, unsigned char ucX)
{
     unsignedchar ucI,ucJ;
     unsignedshort int  uiCrcValue = PRESET_VALUE;
 
         for(ucI = 0; ucI < ucX; ucI++)
        {
               uiCrcValue = uiCrcValue ^ *(pucY + ucI);
             for(ucJ = 0; ucJ < 8; ucJ++)
           {
                  if(uiCrcValue& 0x0001)
                {
                      uiCrcValue= (uiCrcValue >> 1) ^ POLYNOMIAL;
                }
                  else
                {
                      uiCrcValue= (uiCrcValue >> 1);
                }
            }
    }
return uiCrcValue;
}
 */
public class CRC16 {
	
	
	/**
	 * 获取字节数组CRC16值
	 * @param Buf
	 * @param Len
	 * @return
	 */
	public static int getCRC16(byte[] buf) {
		int len = buf.length;
		int cRC;
		int i, temp;
		cRC = 0xffff;
		for (i = 0; i < len; i++) {
			cRC = cRC ^ byteToInteger(buf[i]);
			for (temp = 0; temp < 8; temp++) {
				if ((cRC & 0x01) == 1)
					cRC = (cRC >> 1) ^ 0xA001;
				else
					cRC = cRC >> 1;
			}
		}
		return cRC;
	}
	
	/**
	 * 获取字节数组CRC16值
	 * @param Buf
	 * @param Len
	 * @return
	 */
	public static int getCRC16(byte[] Buf, int Len) {
		int CRC;
		int i, Temp;
		CRC = 0xffff;
		for (i = 0; i < Len; i++) {
			CRC = CRC ^ byteToInteger(Buf[i]);
			// System.out.println(byteToInteger(Buf[i]));
			for (Temp = 0; Temp < 8; Temp++) {
				if ((CRC & 0x01) == 1)
					CRC = (CRC >> 1) ^ 0xA001;
				else
					CRC = CRC >> 1;
			}
		}
		return CRC;
	}
	
	/**
	 * 保留低8位，转换成int型
	 * @param b
	 * @return
	 */
	private static int byteToInteger(byte b) {
		int value;
		value = b & 0xff;
		return value;
	}

}
