package com.xishanju.test.test.utils;

import org.apache.log4j.Logger;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 14:58 2018/5/23
 */
public class ReadFile {

    private static Logger logger = Logger.getLogger(ReadFile.class);

    public static void readFileMeth(String targetPath,String filePath,long start,long end) {
        try {
            RandomAccessFile inFile = new RandomAccessFile(filePath,"rw");
            RandomAccessFile outFile = new RandomAccessFile(targetPath,"rw");
            inFile.seek(start);
            outFile.seek(start);

            FileChannel inChannel = inFile.getChannel();
            FileChannel outChannel = outFile.getChannel();

            FileLock lock = outChannel.lock(start,(end - start) ,false);
            inChannel.transferTo(start,(end - start), outChannel);

            lock.release();
            outFile.close();
            inFile.close();

        } catch (Exception e) {
            logger.error("读写文件异常：",e);
        }
    }
}
