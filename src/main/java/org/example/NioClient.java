package org.example;

import java.io.IOException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioClient {
    public static void main( String[] args ) throws IOException {
        NioClient client = new NioClient();
        client.start();

        System.out.println( "Hello World!" );
    }

    public void start() throws IOException {

        SocketChannel sschannel=SocketChannel.open(new InetSocketAddress("localhost",9999));
        sschannel.configureBlocking(false);
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        Scanner scanner=new Scanner(System.in);
        while (true){
            System.out.println("请说：");
            String msg= scanner.nextLine();
            buffer.put(("boy："+msg).getBytes());
            buffer.flip();
            sschannel.write(buffer);
            buffer.clear();
        }
    }
}
