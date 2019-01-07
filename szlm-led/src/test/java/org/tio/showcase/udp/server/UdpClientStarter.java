package org.tio.showcase.udp.server;

import org.junit.Test;
import org.tio.core.udp.UdpClient;
import org.tio.core.udp.UdpClientConf;

public class UdpClientStarter {

    @Test
    public void testUdpClient() {
        UdpClientConf udpClientConf = new UdpClientConf("192.168.1.115", 8081, 5000);
        UdpClient udpClient = new UdpClient(udpClientConf);
        udpClient.start();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            String str = i + "、" + "用tio开发udp，有点意思";
            udpClient.send(str.getBytes());
        }
        long end = System.currentTimeMillis();
        long iv = end - start;
        System.out.println("耗时:" + iv + "ms");
    }

}
