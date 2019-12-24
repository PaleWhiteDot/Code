package com.study.zookeeper.DistributeLock.javaApiLock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author XiaoBai
 * @description
 * @date 2019/12/17
 */
public class ZookeeperClient {
    private final static String CONNECTSTRING = "119.23.187.114:2181";
    private static int sessionTimeOut = 5000;

    /**
     *获取连接
     */
    public static ZooKeeper getInstance() throws IOException, InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTSTRING, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }

    public static int getSessionTimeOut() {
        return sessionTimeOut;
    }
}
