package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class BioClient {


    public static void main( String[] args ) throws IOException {
        BioClient client = new BioClient();
        client.start();

        System.out.println( "Hello World!" );
    }

    public int start() throws IOException {

        // 1.创建socket将对象请求服务端的链接
        Socket socket = new Socket("127.0.0.1", 9999);
        // 2.从socket对象中获取一个字符输出流
        OutputStream os = socket.getOutputStream();
        PrintStream ps = new PrintStream(os);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请说：");
            String msg = scanner.nextLine();
            ps.println(msg);
            // 推送发送内容给服务端
            ps.flush();
        }
    }
}
