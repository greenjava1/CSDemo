package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class AioClient {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AioClient client = new AioClient();
        client.start();

        System.out.println("Hello World!");
    }

    public void start() throws IOException, InterruptedException, ExecutionException {

        // 创建 Client
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        // 与 Server 连接
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999)).get();

        // 向 Server 写数据
        socketChannel.write(ByteBuffer.wrap("HelloServer".getBytes()));

        // 读取 Server 的数据
        ByteBuffer buffer = ByteBuffer.allocate(512);
        while (true) {
            Integer len = socketChannel.read(buffer).get();
            if (len != -1) {
                System.out.println("客户端收到消息：" + new String(buffer.array(), 0, len));
            }
        }

    }
}
