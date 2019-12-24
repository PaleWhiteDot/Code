package com.study.zookeeper.apiDemo.javaApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author XiaoBai
 * @description
 * @date
 */
public class ApiOperateDemo implements Watcher{
    private final static String CONNECTSTRING = "119.23.187.114:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
        zooKeeper = new ZooKeeper(CONNECTSTRING, 5000, new ApiOperateDemo());
        countDownLatch.await();
        System.out.println(zooKeeper.getState());
        //创建节点
        String result = zooKeeper.create("/mic1", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zooKeeper.getData("/mic1", new ApiOperateDemo(), stat);
        System.out.println("创建成功:" + result);
        //修改数据
        zooKeeper.setData("/mic1", "mic123".getBytes(), -1);
        TimeUnit.SECONDS.sleep(1);
//        zooKeeper.delete("/mic1", -1);
        //创建节点和子节点(临时节点下面不能挂子节点)
        String path = "/node0";
        zooKeeper.create(path, "1234".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zooKeeper.getData(path, new ApiOperateDemo(), stat);
        TimeUnit.SECONDS.sleep(1);
        Stat stat = zooKeeper.exists(path + "/node1", true);
        if (stat == null) {
            zooKeeper.create(path + "/node1", "1234".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            TimeUnit.SECONDS.sleep(1);
        }
        //获取指定节点下的子节点
        List<String> children = zooKeeper.getChildren("/node", true);

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (Event.EventType.None == watchedEvent.getType() && watchedEvent.getPath() == null) {
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState());
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                try {
                    System.out.println(("路径：" + watchedEvent.getPath() + "->改变后的值" +
                            zooKeeper.getData(watchedEvent.getPath(), true, stat)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                try {
                    System.out.println(("路径：" + watchedEvent.getPath() + "->改变后的值" +
                            zooKeeper.getData(watchedEvent.getPath(), true, stat)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (watchedEvent.getType() == Event.EventType.NodeCreated) {
                try {
                    System.out.println(("路径：" + watchedEvent.getPath() + "->改变后的值" +
                            zooKeeper.getData(watchedEvent.getPath(), true, stat)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
