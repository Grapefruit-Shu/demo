package com.xishanju.test.test.thread;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;

/**
 * @Author:shuyong
 * @Description:
 * @Date: Create in 15:22 2018/5/21
 */
public class ThreadReadFileTest {

    private volatile int size = 0;

    private final String filePath = "C:\\Users\\kingsoft\\Desktop";

    private final String openWay = "rw";

    @Resource
    private Executor executor;

    public void readAndWrite() {
        Path path = Paths.get(filePath,openWay);


    }
}
