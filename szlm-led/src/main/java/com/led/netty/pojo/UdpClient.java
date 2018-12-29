package com.led.netty.pojo;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * UdpClient
 */
public class UdpClient {
    //cardId Binary
    private byte[] cardIdBinary;
    //CardId
    private Long cardId;
    //存取CMD
    private LinkedBlockingDeque<CommonCommand> cmds = new LinkedBlockingDeque<CommonCommand>(100);
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

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public LinkedBlockingDeque<CommonCommand> getCmds() {
        return cmds;
    }

    public void setCmds(LinkedBlockingDeque<CommonCommand> cmds) {
        this.cmds = cmds;
    }

    public long getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setUnixTimeStamp(long unixTimeStamp) {
        this.unixTimeStamp = unixTimeStamp;
    }
}
