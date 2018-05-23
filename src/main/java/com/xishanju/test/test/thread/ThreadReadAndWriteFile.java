package com.xishanju.test.test.thread;

import com.xishanju.test.test.utils.ReadFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 17:38 2018/5/21
 */
public class ThreadReadAndWriteFile {

    private String targetPath;  //本地文件路径
    private String filePath;    //远程文件路径
    private long start,end;  //start起始位置，end结束位置

    public ThreadReadAndWriteFile(String targetPath,String filePath,long start,long end) {
        this.targetPath = targetPath;
        this.filePath = filePath;
        this.start = start;
        this.end = end;
    }

    public void load() {
        ReadFile.readFileMeth(targetPath,filePath,start,end);
    }

    public static void main(String[] args) {
        int threadNum = Runtime.getRuntime().availableProcessors();
        String filePath = "C:\\Users\\kingsoft\\Desktop\\error.txt";
        String targetPath = "C:\\Users\\kingsoft\\Desktop\\test.txt";
        File source = new File(filePath);
        long length = source.length();
        long oneNum = length / threadNum + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (long n = 0; n < threadNum ; n++) {
            long end = oneNum * (n+1);
            long start = oneNum * n;
            executorService.execute(() -> {
                new ThreadReadAndWriteFile(targetPath,filePath,start,end).load();
            });
        }
        if (executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
