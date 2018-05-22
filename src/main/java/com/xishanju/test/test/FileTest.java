package com.xishanju.test.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 15:52 2018/5/21
 */
public class FileTest {

    private final String filePath = "C:\\Users\\kingsoft\\Desktop\\error.txt";

    private final String openWay = "rw";

    public static void main(String[] args) {
        FileTest fileTest = new FileTest();
        fileTest.readFile(10);
    }

    private void readFile(int size) {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath,openWay);
            FileChannel inChannel = file.getChannel();
            file.seek(size);
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            int byteRead = inChannel.read(buffer);
            while (byteRead != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    writeFile(buffer);
                }
                buffer.clear();
                byteRead = inChannel.read(buffer);
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(ByteBuffer buffer) {
        try {
            FileOutputStream fos = new FileOutputStream("C:\\Users\\kingsoft\\Desktop\\test.txt",true);
            FileChannel channel = fos.getChannel();
            channel.write(buffer);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
