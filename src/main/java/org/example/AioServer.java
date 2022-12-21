package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
/*
Java AIO
        异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的有效请求都是由OS先完成了
        再通知服务器应用去启动线程处理，一般适用于连接数较多且连接时间较长的应用
*/

public class AioServer {

    public static void main( String[] args ) throws IOException, InterruptedException {
        getThreadID();
        AioServer aio = new AioServer();
        aio.start();

        System.out.println( "Hello World!" );
    }

    public static void getThreadID() {
         Thread t = Thread.currentThread();
         System.out.println(t.getName());
    }

    public void start() throws IOException, InterruptedException {
// 创建 AIO 服务端
        // 工作线程，用来侦听回调的，事件响应的时候需要回调
        final AsynchronousServerSocketChannel serverChannel =
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(9999));
        System.out.println("Server Started...");

        // 准备接受数据
        // CompletionHandler 是用户处理器，由操作系统来触发
        // CompletionHandler  需要实现两个方法：completed()--成功，failed()--失败，
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

            @Override
            // 成功时触发回调
            public void completed(final AsynchronousSocketChannel socketChannel, Object attachment) {
                // 在此接收客户端连接，如果不写这行代码后面的客户端连接连不上服务端
                serverChannel.accept(attachment, this);
                try {
                    getThreadID();
                    System.out.println("有新连接：" + socketChannel.getRemoteAddress());
                    final ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // 读取 Client 发送的数据
                    // read() 方法入参是缓冲区 buffer，和 一个用户处理器 CompletionHandler
                    socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            getThreadID();
                            buffer.flip();
                            try {
                                System.out.println(socketChannel.getRemoteAddress() + "：" + new String(buffer.array(), 0, result));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // 向 Client 发送数据
                            //socketChannel.write(ByteBuffer.wrap("HelloClient".getBytes()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            getThreadID();
                            System.out.println("failed：");
                            exc.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            // 失败时触发的回调
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });
        Thread.sleep(Integer.MAX_VALUE);

    }
}
