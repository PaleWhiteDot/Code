package com.study.zookeeper.apiDemo.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

/**
 * @author XiaoBai
 * @description
 * @date
 */
public class ZkClientApiOptDemo {
    private final static String CONNECTSTRING = "119.23.187.114:2181";

    private static ZkClient getInstance() {
        return new ZkClient(CONNECTSTRING, 5000);
    }

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = getInstance();
        //zkClient提供了递归创建节点的功能
        zkClient.createPersistent("/zkCli", true);
        System.out.println("SUCCESS---------------------------------------");
        zkClient.deleteRecursive("/zkCli");

        //watcher
        zkClient.subscribeDataChanges("/zkCli", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println(dataPath+"-----" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {

            }
        });

        zkClient.writeData("/zkCli", "node");
        TimeUnit.SECONDS.sleep(1);
    }
}
