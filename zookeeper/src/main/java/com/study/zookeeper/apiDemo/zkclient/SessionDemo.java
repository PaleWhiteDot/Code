package com.study.zookeeper.apiDemo.zkclient;

import org.I0Itec.zkclient.ZkClient;

/**
 * @author XiaoBai
 * @description
 * @date
 */
public class SessionDemo {
    private final static String CONNECTSTRING = "119.23.187.114:2181";

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient(CONNECTSTRING, 4000);
        System.out.println(zkClient+"------------------------------------------");
    }
}
