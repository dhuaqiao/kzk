package com.led.netty.pojo;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * UdpClient
 */
public class UdpClient {
    //cardId Binary
    private byte[] cardIdBinary;
    //CardId
    private String cardId;
    //存取CMD
    private Queue<CommonCommand> cmds = new LinkedBlockingDeque<CommonCommand>(100);
    //记录时间戳
    private long unixTimeStamp;
    //
    private boolean isInit = true;

    public void setInit(boolean init) {
        isInit = init;
    }

    public boolean isInit() {
        return isInit;
    }

    public byte[] getCardIdBinary() {
        return cardIdBinary;
    }

    public void setCardIdBinary(byte[] cardIdBinary) {
        this.cardIdBinary = cardIdBinary;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Queue<CommonCommand> getCmds() {
        return cmds;
    }

    public void setCmds(Queue<CommonCommand> cmds) {
        this.cmds = cmds;
    }

    public long getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setUnixTimeStamp(long unixTimeStamp) {
        this.unixTimeStamp = unixTimeStamp;
    }
}
