package com.led.netty.utils;

import com.led.netty.pojo.CommonCommand;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    public static void closeQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Closeable closeable : closeables) {
            closeQuietly(closeable);
        }
    }

    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

   public static String log(CommonCommand cmd){
       byte[] datas = cmd.getDataBinary();
       StringBuilder _builder = new StringBuilder();
       String msg = ByteBufUtil.hexDump(datas).toUpperCase();
       for(int i =0;i<msg.length();i++){
           if(i%2==0) _builder.append(" ");
           _builder.append(msg.charAt(i));
       }
       return _builder.toString();
   }

    public static void logWrite(int type,CommonCommand cmd, Logger logger){
        byte[] datas = cmd.getDataBinary();
        String cardId = new String(cmd.getDataCardId());
        StringBuilder _builder = new StringBuilder();
        String msg = ByteBufUtil.hexDump(datas).toUpperCase();
        for(int i =0;i<msg.length();i++){
            if(i%2==0) _builder.append(" ");
            _builder.append(msg.charAt(i));
        }
        String info = _builder.toString();
        logger.info("{}:cardId:{},info:{}",1==type ? "Recipient 控制卡标识:"+cmd.getCode() : "Sender", cardId,info);
    }
}
