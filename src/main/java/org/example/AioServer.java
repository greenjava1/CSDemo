package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

/*
Java AIO
        异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的有效请求都是由OS先完成了
        再通知服务器应用去启动线程处理，一般适用于连接数较多且连接时间较长的应用
*/

public class AioServer {


    AsynchronousServerSocketChannel serverChannel;

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

        try {
            // 绑定监听端口
            // serverChannel底层默认绑定了一个AsynchronousChannelGroup
            // AsynchronousChannelGroup所包含的线程池中的线程用于各种异步回调函数的操作
            serverChannel = AsynchronousServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(9999));
            System.out.println("启动服务器，监听端口：9999");

            // accept()是异步的调用
            while (true) {
                serverChannel.accept(null, new AcceptHandler());// 参数attachment类似于邮件的附件，也可以不放
                // 小技巧：阻塞住，保证服务器的主线程不过早的返回，同时避免过于频繁的调用accept函数
                System.in.read();  // read()是阻塞式调用
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close(serverChannel);
        }
    }

    // 用于处理serverChannel异步调用accept()函数的结果
    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

        @Override
        public void completed(AsynchronousSocketChannel result, Object attachment) {
            if (serverChannel.isOpen()) {
                // 确保服务端还在运行，让服务器继续等待下一个客户端的连接请求
                // 底层已经进行了保护，不用担心出现栈溢出的问题
                serverChannel.accept(null, this);
            }

            // 处理已连接客户端的读写操作, 读写仍然是异步的
            AsynchronousSocketChannel clientChannel = result;
            if (clientChannel != null && clientChannel.isOpen()) {
                // 处理客户端通道上异步调用的读/写操作
                ClientHandler handler = new ClientHandler(clientChannel);

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                // attachmentInfo包含了clientChannel的回调函数在处理read()结果时需要用到的信息
                Map<String, Object> attachmentInfo = new HashMap<>();
                attachmentInfo.put("type", "read");
                attachmentInfo.put("buffer", buffer);
                //System.out.println("bio服务端接受到信息 read：" + new String(buffer.array(), 0, result);
                clientChannel.read(buffer, attachmentInfo, handler); // 接收客户端发来的消息，写入到buffer
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // 处理错误
        }
    }

    // 处理异步调用clientChannel的read和write操作结束后返回的结果
    private class ClientHandler implements CompletionHandler<Integer, Object>{
        private AsynchronousSocketChannel clientChannel;

        public ClientHandler(AsynchronousSocketChannel channel) {
            this.clientChannel = channel;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            Map<String, Object> info = (Map<String, Object>) attachment;
            // 判断完结的操作是读操作 还是 写操作呢？
            String type = (String) info.get("type");
            System.out.println("bio服务端接受到信息 ：" +type);
            // 如果是读操作完成了，拿到读进buffer的数据，把它写回ClientChannel(即让ClientChannel再读取buffer)
            if ("read".equals(type)) {
                ByteBuffer buffer = (ByteBuffer) info.get("buffer");
                buffer.flip(); // 写模式变为读模式
                // 更改attachment的type为写
                info.put("type", "write");
                clientChannel.write(buffer, info, this); // 由buffer读出数据，写入clientChannel
                System.out.println("bio服务端接受到信息 read：" + new String(buffer.array(), 0, result));

            } else if ("write".equals(type)) {
                // 把信息原封不动发回客户后，继续监听客户发来的消息
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                info.put("type", "read"); // 更新为read
                info.put("buffer", buffer);
                clientChannel.read(buffer, info, this);
                System.out.println("bio服务端接受到信息：write" + new String(buffer.array(), 0, result));
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // 处理错误
        }
    }

}
