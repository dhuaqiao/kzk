package com.led.netty.utils;

import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class DebugUtils {

	public static void debugData(String desc, byte[] data) {
		StringBuilder _builderCtx = new StringBuilder();
		int count = 0;
		for (int i = 0; i < data.length; i++) {
			int b = data[i];
			if (b < 0) {
				b += 256;
			}
			String hexString = Integer.toHexString(b);
			hexString = (hexString.length() == 1) ? "0" + hexString : hexString;
			System.out.print(hexString + " ");
			_builderCtx.append(hexString).append(" ");
			count++;
			if (count % 4 == 0) {
				 System.out.print("  ");
			}
			if (count % 32 == 0) {
				System.out.println();
			}
		}
		log.info("info:{}",_builderCtx.toString());
	}


}
