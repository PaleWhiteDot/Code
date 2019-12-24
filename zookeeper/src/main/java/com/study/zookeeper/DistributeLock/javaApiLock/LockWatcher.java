package com.study.zookeeper.DistributeLock.javaApiLock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author XiaoBai
 * @description 监控
 * @date 2019/12/17
 */
public class LockWatcher implements Watcher {
    private CountDownLatch latch;

    public LockWatcher(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {
            latch.countDown();
        }
    }
}
