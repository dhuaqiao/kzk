package com.led.netty.pojo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
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

    public BlockingQueue<Integer> stateQueue = new ArrayBlockingQueue<>(1);

    private ChannelHandlerContext ctx;

    private DatagramPacket datagramPacket;

    private volatile Lock lock = new ReentrantLock();

    private volatile Condition condition = lock.newCondition();	//类Condition成员

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void lockState() throws InterruptedException{
        try {
            System.err.println(Thread.currentThread().getName()+" lockState lock");
           // lock.lock();
            System.err.println(Thread.currentThread().getName()+" lockState await");
            //condition.await();
            countDownLatch.await();

            synchronized (lock){
                //lock.wait();
            }
            System.err.println(Thread.currentThread().getName()+" lockState println");
        }finally {
            System.err.println(Thread.currentThread().getName()+" lockState unlock");
           // lock.unlock();
        }
    }

    public void unLockState(){
        try {
            System.err.println(Thread.currentThread().getName()+" unLockState lock");
            //lock.lock();
            System.err.println(Thread.currentThread().getName()+" unLockState signal");
            //condition.signal();//唤醒一个在Condition等待队列中的线程
            System.err.println(Thread.currentThread().getName()+" unLockState println");
            synchronized (lock){
                //lock.notify();
            }
            System.out.println(" lock.notifyAll(); ");
            countDownLatch.countDown();
        }finally {
            System.err.println(Thread.currentThread().getName()+" unLockState unlock");
            //lock.unlock();
        }
    }

    public void setDatagramPacket(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }

    public DatagramPacket getDatagramPacket() {
        return datagramPacket;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
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
