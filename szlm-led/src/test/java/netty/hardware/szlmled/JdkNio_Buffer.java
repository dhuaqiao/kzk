package netty.hardware.szlmled;

import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class JdkNio_Buffer {

    @Test
    public void testBuffer01(){
        System.out.println("----------Test allocate--------");
        System.out.println("before alocate:"
                + Runtime.getRuntime().freeMemory()/1024/1024);

        // 如果分配的内存过小，调用Runtime.getRuntime().freeMemory()大小不会变化？
        // 要超过多少内存大小JVM才能感觉到？
        ByteBuffer buffer = ByteBuffer.allocate(102400);
        System.out.println("buffer = " + buffer);

        System.out.println("after alocate:"
                + Runtime.getRuntime().freeMemory());

        // 这部分直接用的系统内存，所以对JVM的内存没有影响
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(102400);
        System.out.println("directBuffer = " + directBuffer);
        System.out.println("after direct alocate:"
                + Runtime.getRuntime().freeMemory());

        System.out.println("----------Test wrap--------");
        byte[] bytes = new byte[32];
        buffer = ByteBuffer.wrap(bytes);
        System.out.println(buffer);

        buffer = ByteBuffer.wrap(bytes, 10, 10);
        System.out.println(buffer);
    }
    @Test
    public void testBuffer02(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        printBuffer(buffer);
        System.out.println(" put data ");
        buffer.put((byte)0x01);
        buffer.put((byte)0x02);
        printBuffer(buffer);



    }


    public void printBuffer(Buffer buffer){
        // Invariants: mark <= position <= limit <= capacity
        System.out.println("position: "+buffer.position());
        System.out.println("limit: "+buffer.limit());
        System.out.println("capacity: "+buffer.capacity());
        //System.out.println("compact: "+buffer.mark());
    }
}
