package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
/*
Java BIO
同步阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器就需要启动一个线程处理
如果这个连接不做任何事情就会造成不必要的浪费
*/

public class BioServer {

    public static void main( String[] args ) throws IOException {
        BioServer bio = new BioServer();
        bio.start();

        System.out.println( "Hello World!" );
    }

    public void start() throws IOException {
        System.out.println("bio服务器启动.....");
        // 1.定义一个serverSocket，并定义端口是 9999
        ServerSocket ss = new ServerSocket(9999);
        // 2.监听k客户端的socket请求
        Socket socket = ss.accept();
        System.out.println("bio收到客户端请求连接.....");
        // 3.从客户端传来的socket里面获取到流
        InputStream is = socket.getInputStream();
        // 4.将字节输入流包装成一个缓冲字符输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String msg;
        while((msg = br.readLine())!=null){
            System.out.println("bio服务端接受到信息：" + msg);
        }
        System.out.println("bio服务器停止.....");
    }
}
