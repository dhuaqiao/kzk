package org.tio.showcase.udp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Node;
import org.tio.core.udp.UdpPacket;
import org.tio.core.udp.intf.UdpHandler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class ShowcaseUdpHandler implements UdpHandler {

    private static Logger log = LoggerFactory.getLogger(ShowcaseUdpHandler.class);

    public ShowcaseUdpHandler() {
    }

    @Override
    public void handler(UdpPacket udpPacket, DatagramSocket datagramSocket) {
        byte[] data = udpPacket.getData();
        String msg = new String(data);
        Node remote = udpPacket.getRemote();

        log.info("收到来自{}的消息:【{}】", remote, msg);
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, new InetSocketAddress(remote.getIp(), remote.getPort()));
        try {
            datagramSocket.send(datagramPacket);
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }
    }
}
