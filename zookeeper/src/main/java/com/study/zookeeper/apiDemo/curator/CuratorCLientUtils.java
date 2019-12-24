package com.study.zookeeper.apiDemo.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author XiaoBai
 * @description
 * @date
 */
public class CuratorCLientUtils {
    private final static String CONNECTSTRING = "119.23.187.114:2181";
    private static CuratorFramework curatorFramework;

    public static CuratorFramework getInstance() {
        curatorFramework = CuratorFrameworkFactory.newClient(CONNECTSTRING, 5000, 5000,
                new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        return curatorFramework;
    }
}
