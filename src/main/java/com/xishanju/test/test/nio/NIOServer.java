package com.xishanju.test.test.nio;

import com.xishanju.test.test.utils.CharsetHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 11:51 2018/5/22
 */
public class NIOServer {

    private static Logger logger = Logger.getLogger(NIOServer.class);

    private ByteBuffer readBuffer;
    private Selector selector;
    private ServerSocket serverSocket;

    /**
     * 创建链接时间
     */
    public void init() {
        //建立临时缓冲区
        readBuffer = ByteBuffer.allocate(2048);
        //创建服务端Socket非阻塞通道
        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            //指定内部Socket绑定的服务端地址，并支持重用接口，因为可能有多个客户端同时访问同一个端口
            serverSocket = serverSocketChannel.socket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress("localhost",2000));
            //创建轮询器，并绑定到管道上，开始监听客户端请求
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            logger.error("初始化Socket连接错误",e);
        }
    }

    /**
     * 读取数据
     */
    private void listener() {
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                while (selectionKeys.hasNext()) {
                    SelectionKey key = selectionKeys.next();
                    selectionKeys.remove();
                    handleKye(key);
                }
            } catch (Exception e) {
                logger.error("读取数据异常",e);
            }
        }
    }

    private void handleKye(SelectionKey key) throws IOException {
        SocketChannel channel = null;
        try {
            //如果客户端要连接，这里处理连接事件
            if (key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                channel = serverSocketChannel.accept();
                channel.configureBlocking(false);
                // 告诉轮询器 接下来关心的是读取客户端数据这件事
                channel.register(selector,SelectionKey.OP_READ);
            } else if (key.isReadable()) {  //7. 如果客户端发送数据，则这里读取数据。
                channel = (SocketChannel) key.channel();
                readBuffer.clear();
                // 当客户端关闭channel后，会不断收到read事件，此刻read方法返回-1 所以对应的服务器端也需要关闭channel
                int readCount = channel.read(readBuffer);
                if (readCount > 0) {
                    readBuffer.flip();
                    String question = CharsetHelper.decode(readBuffer).toString();
                    System.out.println("server get the question:" + question);
                    String answer = getAnswer(question);
                    System.out.println("接收信息：" + answer);
                    channel.write(CharsetHelper.encode(CharBuffer.wrap(answer)));
                } else {
                    channel.close();
                }
            }
        } catch (Exception e) {
            logger.error("读取数据异常",e);
        } finally {
            if (channel != null) {
                channel.close();
            }
        }
    }

    public static String getAnswer(String question) {
        String answer = null;
        switch (question) {
            case "who":
                answer = "我是小娜\n";
                break;
            case "what":
                answer = "我是来帮你解闷的\n";
                break;
            case "where":
                answer = "我来自外太空\n";
                break;
            case "hi":
                answer = "hello\n";
                break;
            case "bye":
                answer = "88\n";
                break;
            default:
                answer = "请输入 who， 或者what， 或者where";
        }
        return answer;
    }

    public static void main(String[] args) {
        NIOServer server = new NIOServer();
        server.init();
        server.listener();
    }
}
