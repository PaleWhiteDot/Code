package com.study.zookeeper.apiDemo.javaApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author XiaoBai
 * @description 权限控制
 * @date
 */
public class AuthControllerDemo implements Watcher {

    private final static String CONNECTSTRING = "119.23.187.114:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
        zooKeeper = new ZooKeeper(CONNECTSTRING, 5000, new AuthControllerDemo());
//        countDownLatch.await();

        ACL ac1 = new ACL(ZooDefs.Perms.CREATE, new Id("digest", "root:root"));
        ACL ac2 = new ACL(ZooDefs.Perms.CREATE, new Id("ip", "119.23.187.114"));
        List<ACL> acls = new ArrayList<>();
        acls.add(ac1);
        acls.add(ac2);

//        zooKeeper.addAuthInfo("digest", "root:root".getBytes());
        zooKeeper.create("/auth", "123".getBytes(), acls, CreateMode.PERSISTENT);
        ZooKeeper zooKeeper1 = new ZooKeeper(CONNECTSTRING, 5000, new AuthControllerDemo());
        zooKeeper1.delete("/auth", -1);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (Event.EventType.None == watchedEvent.getType() && watchedEvent.getPath() == null) {
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState());
            }
        }
    }
}
