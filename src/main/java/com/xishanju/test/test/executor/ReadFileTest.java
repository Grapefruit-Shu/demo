package com.xishanju.test.test.executor;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 10:27 2018/5/23
 */
public class ReadFileTest {

    private static Logger logger = Logger.getLogger(ReadFileTest.class);

    private int threadNum;  //线程数量
    private String filePath;   //资源路径
    private String nativePath;   //本地文件路径

    public ReadFileTest(int threadNum, String filePath, String nativePath) {
        this.threadNum = threadNum;
        this.filePath = filePath;
        this.nativePath = nativePath;
    }

    private void readFile() throws IOException {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath,"rw");
            long length = file.length();
            long page = length / threadNum + 1;

            ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
            for (int n = 0 ; n < threadNum ; n++) {
                long startPage = page * n;

                executorService.execute(() -> {
                    try {
                        readAndWrite(startPage);
                    } catch (Exception e) {
                        logger.error("读文件异常",e);
                    }
                });
            }
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAndWrite(long startPage) throws IOException {
        System.out.println("开始执行：" + Thread.currentThread().getName());
        RandomAccessFile file = new RandomAccessFile(filePath,"rw");
        RandomAccessFile rwf = new RandomAccessFile(nativePath,"rw");
        file.seek(startPage);
        rwf.seek(startPage);
        FileChannel rwfChannel = rwf.getChannel();
        FileChannel channel = file.getChannel();
        channel.transferTo(startPage,channel.size(),rwfChannel);
        file.close();
        rwf.close();
    }

    public static void main(String[] args) throws IOException {
        ReadFileTest test = new ReadFileTest(Runtime.getRuntime().availableProcessors(),
                "C:\\Users\\kingsoft\\Desktop\\error.txt",
                "C:\\Users\\kingsoft\\Desktop\\test.txt");
        test.readFile();
    }
}
