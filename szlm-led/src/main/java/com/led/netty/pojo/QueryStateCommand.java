package com.led.netty.pojo;

import io.netty.channel.socket.DatagramPacket;

//查询当前屏幕状态
public class QueryStateCommand extends CommonCommand {

    //1 开 0 关
    private int state = -1;

    public QueryStateCommand(byte[] dataBinary, byte[] dataCardId, DatagramPacket datagramPacket,int state) {
        super(dataBinary, dataCardId, datagramPacket);
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
