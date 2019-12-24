package com.study.zookeeper.DistributeLock.javaApiLock;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author XiaoBai
 * @description 分布式锁的实现
 * @date 2019/12/17
 */
public class DistributeLock {
    //根节点
    private static final String ROOT_LOCKS = "/LOCKS";
    private ZooKeeper zooKeeper;
    //会话超时时间
    private int sessionTimeOut;
    //记录锁节点id
    private String lockId;
    //节点的数据
    private final static byte[] data = {1, 2};

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public DistributeLock() throws IOException, InterruptedException {
        this.zooKeeper = ZookeeperClient.getInstance();
        this.sessionTimeOut = ZookeeperClient.getSessionTimeOut();
    }

    /**
     * 获取锁
     * @return
     */
    public boolean lock() {
        try {
            lockId = zooKeeper.create(ROOT_LOCKS + "/", data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "->当前线程成功创建了lock节点[" + lockId + "],开始去竞争锁");

            //获取/lock目录下所有子节点
            List<String> children = zooKeeper.getChildren(ROOT_LOCKS, true);
            //从小到大排序
            TreeSet<String> sortedSet = new TreeSet<>();
            for (String chile : children) {
                sortedSet.add(ROOT_LOCKS + "/" +chile);
            }
            //拿到最小节点
            String first = sortedSet.first();
            //最小节点就是当前节点
            if (lockId.equals(first)) {
                System.out.println(Thread.currentThread().getName() + "成功获取锁，lock节点为[" + lockId + "]");
                return true;
            }
            //比lockId小的有序集合
            SortedSet<String> lessThanLockId = sortedSet.headSet(lockId);
            //获取有序集合的最小值
            if (!lessThanLockId.isEmpty()) {
                String last = lessThanLockId.last();
                zooKeeper.exists(last, new LockWatcher(countDownLatch));
                countDownLatch.await(sessionTimeOut, TimeUnit.MILLISECONDS);
                //上面这段代码表示，如果会话超时或者节点被删除（释放）
                System.out.println(Thread.currentThread().getName() + "成功获取锁["+lockId+"]");
            }
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放锁
     * @return
     */
    public boolean unLock() {
        System.out.println(Thread.currentThread().getName() + "->开始释放锁:" + lockId);
        try {
            zooKeeper.delete(lockId, -1);
            System.out.println("节点["+lockId+"]成功被删除");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        Logger.getLogger("org.apache").setLevel(Level.INFO);
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        CountDownLatch latch = new CountDownLatch(10);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                DistributeLock lock = null;
                try {
                    lock = new DistributeLock();
                    latch.countDown();
                    latch.await();
                    lock.lock();
                    Thread.sleep(random.nextInt(500));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unLock();
                }
            }).start();
        }
    }
}
