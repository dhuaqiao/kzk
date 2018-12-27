package netty.hardware.szlmled;

import com.led.netty.utils.PackDataUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class SzlmLedApplicationTests {

    @Test
    public void contextLoads() {

        //hello_1
        //  A5 31 38 31 32 32 36 68 32 01 7B 01 10 00 00 00 85 00 00 00 01 01 01 00 68 65 6C 6C 6F 5F 31 00 53 04 AE
        // A5 31 38 31 32 32 36 68 32 01 7B 01 10 00 00 00 85 00 00 00 01 01 01 00 68 65 6C 6C 6F 5F 31 00 53 04 AE

       //byte[] data =  PackDataUtils.pack0X85();
      // String msg = PackDataUtils.binaryToHexString(data);
        //System.out.println(msg);
    }

    //@Test
    public void testUdpClient() throws Exception{
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>(){
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                        System.out.println("channelRead0 "+msg.content().toString(Charset.defaultCharset()));
                    }
                });
      String host = "127.0.0.1";
      int port = 28686;
       Channel channel = bootstrap.bind(port).sync().channel();
       ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        System.out.println(channel.localAddress());

        //封装数据
        host = "123.56.0.62";
        port = 8686;
        InetSocketAddress recipient = new InetSocketAddress(host,port);
       DatagramPacket datagramPacket = new DatagramPacket(buf,recipient);
        ChannelFuture channelFuture = channel.writeAndFlush(datagramPacket);
        System.out.println(channelFuture.isSuccess()+ " >> "+channelFuture.isDone());
        Thread.sleep(10000);
        workerGroup.shutdownGracefully();
        channel.close();

    }

}
