package com.study.zookeeper.apiDemo.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author XiaoBai
 * @description
 * @date
 */
public class CuratorOperationDemo {
    public static void main(String[] args) throws InterruptedException {
        CuratorFramework curatorFramework = CuratorCLientUtils.getInstance();
        System.out.println("连接成功----------------------------");
        /**
         * 创建节点
         */
//        try {
//            String path = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
//                    forPath("/curator/curator1", "123".getBytes());
//            System.out.println(path);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /**
         * 删除节点(递归删除)
         */
//        try {
//            curatorFramework.delete().deletingChildrenIfNeeded().forPath("/curator");
//            System.out.println("删除成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        /**
         * 查询
         */
        Stat stat = new Stat();//节点状态
        try {
            byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath("/node");
            System.out.println(stat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Stat stat1 = curatorFramework.setData().forPath("/node", "1235".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 异步操作
         */
        ExecutorService service = Executors.newFixedThreadPool(1);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                    inBackground(new BackgroundCallback(){
                        /**
                         * 回调方法
                         * @param curatorFramework
                         * @param curatorEvent
                         * @throws Exception
                         */
                        @Override
                        public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                            countDownLatch.countDown();
                            System.out.println(Thread.currentThread().getName() + "-----resultCode:" + curatorEvent.getResultCode() + "---resultType:" + curatorEvent.getType());
                        }
                    }, service).forPath("/synPath");
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.await();
        service.shutdown();

        /**
         * 事务操作（curator独有）
         * 先创建一个节点，然后修改另一个节点的数据，两个操作在一个事务里
         */
        try {
            Collection<CuratorTransactionResult> transaction = curatorFramework.inTransaction().create().forPath("/transaction").and().
                    setData().forPath("/node", "12".getBytes()).and().commit();
            for (CuratorTransactionResult result : transaction) {
                System.out.println(result.getForPath() + "->" + result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
