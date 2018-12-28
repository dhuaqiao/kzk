package com.led.netty.pojo;

import io.netty.channel.socket.DatagramPacket;

/**
 * CommonCommand公共命令
 */
public class CommonCommand {
	//原始数据
	private byte[] dataBinary = {};
	//原始数据
	private byte[] dataCardId = {};
	//UDP DatagramPacket
	private DatagramPacket datagramPacket;
	//标识
	private boolean code;

	public CommonCommand(){

	}

	public CommonCommand(byte[] dataBinary, byte[] dataCardId){
		this.dataBinary = dataBinary;
		this.dataCardId = dataCardId;
	}

	public CommonCommand(byte[] dataBinary, byte[] dataCardId, DatagramPacket datagramPacket){
		this.dataBinary = dataBinary;
		this.dataCardId = dataCardId;
		this.datagramPacket = datagramPacket;
	}

	public boolean getCode() {
		return code;
	}

	public void setCode(boolean code) {
		this.code = code;
	}

	public byte[] getDataBinary() {
		return dataBinary;
	}

	public void setDataBinary(byte[] dataBinary) {
		this.dataBinary = dataBinary;
	}

	public byte[] getDataCardId() {
		return dataCardId;
	}

	public void setDataCardId(byte[] dataCardId) {
		this.dataCardId = dataCardId;
	}

	public DatagramPacket getDatagramPacket() {
		return datagramPacket;
	}

	public void setDatagramPacket(DatagramPacket datagramPacket) {
		this.datagramPacket = datagramPacket;
	}
}
