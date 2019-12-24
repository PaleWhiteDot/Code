package com.study.zookeeper.apiDemo.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author XiaoBai
 * @description
 * @date
 */
public class CuratorCreateSessionDemo {
    private final static String CONNECTSTRING = "119.23.187.114:2181";

    public static void main(String[] args) {
        /**
         * 重试策略：ExponentialBackoffRetry（）衰减重试
         * RetryNTimes 指定最大重试次数
         * RetryOneTime 仅重试一次
         * RetryUnitElapsed 一直重试直到规定的时间
         */
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(CONNECTSTRING, 5000, 5000,
                new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();//启动连接
        //Fluent风格
        CuratorFramework framework = CuratorFrameworkFactory.builder().connectString(CONNECTSTRING).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).namespace("/curator").build();
        framework.start();
        System.out.println("SUCCESS");
    }
}
