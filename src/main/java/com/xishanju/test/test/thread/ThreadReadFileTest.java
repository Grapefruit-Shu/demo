package com.xishanju.test.test.thread;

import com.xishanju.test.test.utils.ReadFile;

import java.io.File;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 15:22 2018/5/21
 */
public class ThreadReadFileTest extends Thread {

    private String targetPath;  //本地文件路径
    private String filePath;    //远程文件路径
    private long start,end;  //start起始位置，end结束位置

    public ThreadReadFileTest(String targetPath,String filePath,long start,long end) {
        this.targetPath = targetPath;
        this.filePath = filePath;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        ReadFile.readFileMeth(targetPath,filePath,start,end);
    }

    public static void main(String[] args) {
        int threadNum = Runtime.getRuntime().availableProcessors();
        String filePath = "C:\\Users\\kingsoft\\Desktop\\error.txt";
        String targetPath = "C:\\Users\\kingsoft\\Desktop\\test.txt";
        File source = new File(filePath);
        long length = source.length();
        long oneNum = length / threadNum + 1;
        for (long n = 0; n < threadNum ; n++) {
            long end = oneNum * (n+1);
            long start = oneNum * n;
            new ThreadReadFileTest(targetPath,filePath,start,end).start();
        }

    }

}
