package com.led.netty.pojo;

/**
 * 协议编码 <B>系统名称：</B><BR>
 * <B>模块名称：</B><BR>
 * <B>中文类名：</B><BR>
 * <B>概要说明：</B><BR>
 * 
 * @author dhq（Administrator）
 * @since 2018年6月7日
 */
public class AbstractCommand {

	// 包头
	private byte dataHeader;
	// 长度低位
	private byte dataLengthLows;
	// 长度高位
	private byte dataLengthHigh;
	// 命令类别
	private byte dataCommandCategory;
	// 命令字
	private byte dataCommandWord;
	// 命令序号低位
	private byte dataCommandSequenceNumberLow;
	// 命令序号高位
	private byte dataCommandSequenceNumber;
	// 保留字节1
	private byte datapersist1;
	// 保留字节2
	private byte datapersist2;
	// 状态
	private byte dataState;
	// 8字节设备ID
	private byte[] dataDeviceId;
	// 8字节设备token
	private byte[] dataToken;
	// 数据区
	private byte[] dataArea;
	// CRC低位
	private byte dataCrcLow;
	// CRC高位
	private byte dataCrcHigh;
	// 包尾
	private byte dataFooter;
	//原始数据
	private byte[] dataBinary;
	
	public void setDataBinary(byte[] dataBinary) {
		this.dataBinary = dataBinary;
	}
	
	public byte[] getDataBinary() {
		return dataBinary;
	}
	

	public byte getDataHeader() {
		return dataHeader;
	}

	public void setDataHeader(byte dataHeader) {
		this.dataHeader = dataHeader;
	}

	public byte getDataLengthLows() {
		return dataLengthLows;
	}

	public void setDataLengthLows(byte dataLengthLows) {
		this.dataLengthLows = dataLengthLows;
	}

	public byte getDataLengthHigh() {
		return dataLengthHigh;
	}

	public void setDataLengthHigh(byte dataLengthHigh) {
		this.dataLengthHigh = dataLengthHigh;
	}

	public byte getDataCommandCategory() {
		return dataCommandCategory;
	}

	public void setDataCommandCategory(byte dataCommandCategory) {
		this.dataCommandCategory = dataCommandCategory;
	}

	public byte getDataCommandWord() {
		return dataCommandWord;
	}

	public void setDataCommandWord(byte dataCommandWord) {
		this.dataCommandWord = dataCommandWord;
	}

	public byte getDataCommandSequenceNumberLow() {
		return dataCommandSequenceNumberLow;
	}

	public void setDataCommandSequenceNumberLow(byte dataCommandSequenceNumberLow) {
		this.dataCommandSequenceNumberLow = dataCommandSequenceNumberLow;
	}

	public byte getDataCommandSequenceNumber() {
		return dataCommandSequenceNumber;
	}

	public void setDataCommandSequenceNumber(byte dataCommandSequenceNumber) {
		this.dataCommandSequenceNumber = dataCommandSequenceNumber;
	}

	public byte getDatapersist1() {
		return datapersist1;
	}

	public void setDatapersist1(byte datapersist1) {
		this.datapersist1 = datapersist1;
	}

	public byte getDatapersist2() {
		return datapersist2;
	}

	public void setDatapersist2(byte datapersist2) {
		this.datapersist2 = datapersist2;
	}

	public byte getDataState() {
		return dataState;
	}

	public void setDataState(byte dataState) {
		this.dataState = dataState;
	}

	public byte[] getDataDeviceId() {
		return dataDeviceId;
	}

	public void setDataDeviceId(byte[] dataDeviceId) {
		this.dataDeviceId = dataDeviceId;
	}

	public byte[] getDataToken() {
		return dataToken;
	}

	public void setDataToken(byte[] dataToken) {
		this.dataToken = dataToken;
	}

	public byte[] getDataArea() {
		return dataArea;
	}

	public void setDataArea(byte[] dataArea) {
		this.dataArea = dataArea;
	}

	public byte getDataCrcLow() {
		return dataCrcLow;
	}

	public void setDataCrcLow(byte dataCrcLow) {
		this.dataCrcLow = dataCrcLow;
	}

	public byte getDataCrcHigh() {
		return dataCrcHigh;
	}

	public void setDataCrcHigh(byte dataCrcHigh) {
		this.dataCrcHigh = dataCrcHigh;
	}

	public byte getDataFooter() {
		return dataFooter;
	}

	public void setDataFooter(byte dataFooter) {
		this.dataFooter = dataFooter;
	}

}
