package com.led.netty.pojo;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    //状态
    private int state = -1;
    //lOCK
    private volatile Lock lock = new ReentrantLock();
    //Condition
    private volatile Condition condition = lock.newCondition();	//类Condition成员

    private Object key = new Object();

    public void syncLockState() throws InterruptedException{
        lock.lock();
        try {
            condition.await(1,TimeUnit.MINUTES); //long time, TimeUnit unit
        }finally {
            lock.unlock();
        }
    }

    public void syncUnLockState(){
        lock.lock();
        try {
            condition.signal();//唤醒一个在Condition等待队列中的线程
        }finally {
            lock.unlock();
        }
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

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

    public long getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public void setUnixTimeStamp(long unixTimeStamp) {
        this.unixTimeStamp = unixTimeStamp;
    }
}
