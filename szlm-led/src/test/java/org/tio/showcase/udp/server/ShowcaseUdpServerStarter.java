package org.tio.showcase.udp.server;

import org.tio.core.udp.UdpServer;
import org.tio.core.udp.UdpServerConf;

import java.net.SocketException;

public class ShowcaseUdpServerStarter {

    /**
     * @param args
     * @throws SocketException
     */
    public static void main(String[] args) throws SocketException {
        ShowcaseUdpHandler fpmsUdpHandler = new ShowcaseUdpHandler();
        UdpServerConf udpServerConf = new UdpServerConf(3000, fpmsUdpHandler, 5000);
        UdpServer udpServer = new UdpServer(udpServerConf);
        udpServer.start();
    }
}
