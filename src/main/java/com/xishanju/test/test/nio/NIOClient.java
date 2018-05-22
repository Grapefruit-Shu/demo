package com.xishanju.test.test.nio;

import com.xishanju.test.test.utils.CharsetHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 15:14 2018/5/22
 */
public class NIOClient implements Runnable{

    private BlockingQueue<String> words;
    private Random random = null;
    SocketChannel channel = null;
    // 信道选择器
    private Selector selector;

    // 要连接的服务器Ip地址
    private String hostIp;

    // 要连接的远程服务器在监听的端口
    private int hostListenningPort;

    public NIOClient(String hostIp,int hostListenningPort) throws IOException {
        this.hostIp = hostIp;
        this.hostListenningPort = hostListenningPort;
        initChannel();
    }

    public static void main(String[] args) {
        NIOClient client = null;
        try {
            client = new NIOClient("localhost",2000);
            client.init();
            new Thread(client).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //1. 初始化要发送的数据
    private void init() {
        random = new Random();
        words = new ArrayBlockingQueue<String>(5);
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入信息:");
            String string = scanner.next();
            words.add(string);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 初始化
     *
     * @throws IOException
     */
    private void initChannel() throws IOException {
        // 打开监听信道并设置为非阻塞模式
        channel = SocketChannel.open(new InetSocketAddress(hostIp,hostListenningPort));
        channel.configureBlocking(false);

        // 打开并注册选择器到信道
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
    }

    //2. 启动子线程代码
    public void run() {
        try {
            //3. 创建连接服务端的通道 并设置为阻塞方法，这里需要指定服务端的ip和端口号
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("localhost", 2000));
            selector = Selector.open();
            //4. 请求关心连接事件
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean isOver = false;
            while (!isOver) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isConnectable()) { //5. 当通道连接准备完毕，发送请求并指定接收允许获取服务端返回信息
                        if (channel.isConnectionPending()) {
                            if (channel.finishConnect()) {
                                key.interestOps(SelectionKey.OP_READ);
                                channel.write(CharsetHelper.encode(CharBuffer.wrap(getWord())));
                                sleep();
                            } else {
                                key.cancel();
                            }
                        }
                    } else if (key.isReadable()) {//6. 开始读取服务端返回数据
                        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                        channel.read(byteBuffer);
                        byteBuffer.flip();
                        String answer = CharsetHelper.decode(byteBuffer).toString();
                        System.out.println("client get the answer:" + answer);
                        String word = getWord();
                        if (word != null) {
                            channel.write(CharsetHelper.encode(CharBuffer.wrap(getWord())));
                        } else {
                            isOver = true;
                        }
                        sleep();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //7. 关闭通道
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getWord() {
        return words.poll();
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(random.nextInt(3));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送字符串到服务器
     *
     * @param message
     * @throws IOException
     */
    public void sendMsg(String message) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("UTF-8"));
        channel.write(writeBuffer);
    }
}
