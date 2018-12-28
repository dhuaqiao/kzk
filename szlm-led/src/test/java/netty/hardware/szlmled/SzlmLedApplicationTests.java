package netty.hardware.szlmled;

import com.led.netty.pojo.CommonCommand;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

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

        LocalDateTime localDateTimeNow = LocalDateTime.now();

        LocalDateTime after1m = localDateTimeNow.plusMinutes(1);

        long millis = localDateTimeNow.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        long after1mMillis = after1m.toInstant(ZoneOffset.of("+8")).toEpochMilli();

        System.out.println(TimeUnit.MILLISECONDS.toSeconds(after1mMillis - millis));

        /**
         * 全删
         * A5 68 32 01 7B 01 02 00 00 00 84 00 9D 01 AE
         *
         * 删 2
         * A5 68 32 01 7B 01 04 00 00 00 84 01 01 02 A3 01 AE
         *
         * 删 1、3
         * A5 68 32 01 7B 01 05 00 00 00 84 01 02 01 03 A7 01 AE
         *
         * 这个协议没有设备ID
         */
       byte[] data = PackDataUtils.packDeleteItem(null,null);
        System.out.println(PackDataUtils.binaryToHexString(data));

        data = PackDataUtils.packDeleteItem(new byte[]{31,32},null);
        System.out.println(PackDataUtils.binaryToHexString(data));

        data = PackDataUtils.packDeleteItem(null,2);
        System.out.println(PackDataUtils.binaryToHexString(data));

        data = PackDataUtils.packDeleteItem(null,new int[]{1,3});
        System.out.println(PackDataUtils.binaryToHexString(data));

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
