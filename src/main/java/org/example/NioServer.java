package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


/*
Java NIO
同步非阻塞，服务器实现模式为一个线程处理多个请求，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询连接到有I/O请求就进行处理
*/

public class NioServer {
    public static void main( String[] args ) throws IOException {
        NioServer nio = new NioServer();
        nio.start();

        System.out.println( "Hello World!" );
    }

    public static void getThreadID() {
        Thread t = Thread.currentThread();
        System.out.println(t.getName());
    }

    public void start() throws IOException {
        System.out.println("nio服务器启动.....");
        //1.获取通道
        ServerSocketChannel ssChannel=ServerSocketChannel.open();
        //2.切换为非阻塞模式（默认为阻塞模式）
        ssChannel.configureBlocking(false);
        //3绑定连接的端口
        ssChannel.bind(new InetSocketAddress(9999));
        //4.获取选择器Selector
        Selector selector=Selector.open();
        //5.将通道注册到选择器上，并开始指定监听接收事件
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6.使用Selector选择器轮询已经就绪好的事件
        while(selector.select()>0){
            //7.获取选择器中的所有注册通道中已经就绪好的事件
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            //8.开始遍历这些准备好的事件
            while(it.hasNext()){
                //9.提取当前这个事件
                SelectionKey sk=it.next();
                //10.判断这个事件具体是什么
                if(sk.isAcceptable()){
                    //11.直接获取当前接入的客户端通道
                    SocketChannel schannel=ssChannel.accept();
                    System.out.println("bio收到客户端请求连接....."+schannel.getRemoteAddress());
                    //12.切换成非阻塞模式
                    schannel.configureBlocking(false);
                    //13.本客户端注册到选择器（进行读监听，因为是接收到来自客户端的通道）
                    schannel.register(selector, SelectionKey.OP_READ);
                }else if(sk.isReadable()){
                    //14.通过读取事件反向获取通道
                    SocketChannel schannel= (SocketChannel) sk.channel();
                    //15.读取数据
                    ByteBuffer buffer=ByteBuffer.allocate(1024);
                    int len=0;
                    while ((len=schannel.read(buffer))>0){
                        buffer.flip();
                        System.out.println("bio服务端接收到 "+schannel.getRemoteAddress()+" 发来的信息：" + new String(buffer.array(),0,len));
                        //清除buffer中的数据
                        buffer.clear();
                    }
                }
                //处理完毕后要移除当前事件,不然会重复处理
                it.remove();
            }
        }
        System.out.println("bio服务器停止.....");

    }
}
