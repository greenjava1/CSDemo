package org.example;

import java.io.IOException;

/**
 * Hello world!
 *
 */
/**
BIO，AIO，NIO适用场景分析
        BIO方式适用于链接数目较小且固定的架构，这种方式对服务器资源要求较高，并发局限于应用中，JDK1.4以前的唯一选择，但程序简单易理解
        NIO架构用于连接数目多且连接数时间较短的架构，比如的聊天器，弹幕系统，服务器间通讯等。编程比较复杂，JDK1.4开始支持
        AIO方式使用于连接数目较多且连接时间较长的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂，JDK1.7开始支持
*/
public class App 
{
    public static void main( String[] args ) throws IOException {
        //BioServer bio = new BioServer();
        //bio.start();

        System.out.println( "Hello World!" );
    }
}
