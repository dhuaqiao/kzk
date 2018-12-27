package com.led.netty.pojo;

import io.netty.channel.socket.DatagramPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * 协议编码 <B>系统名称：</B><BR>
 * <B>模块名称：</B><BR>
 * <B>中文类名：</B><BR>
 * <B>概要说明：</B><BR>
 * 
 * @author dhq（Administrator）
 * @since 2018年6月7日
 */


@Data
public class AbstractCommand {

	// 包头
	private byte dataHeader;
	// CRC低位
	private byte dataCrcLow;
	// CRC高位
	private byte dataCrcHigh;
	// 包尾
	private byte dataFooter;
	//原始数据
	private byte[] dataBinary;
	//原始数据
	private byte[] dataCardId;
	//
	private DatagramPacket datagramPacket;

	public  AbstractCommand(){

	}

	public  AbstractCommand(byte[] dataBinary,byte[] dataCardId,DatagramPacket datagramPacket){
		this.dataBinary = dataBinary;
		this.dataCardId = dataCardId;
		this.datagramPacket = datagramPacket;
	}

}
