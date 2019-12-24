package com.study.zookeeper.DistributeLock.zkClient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author XiaoBai
 * @description 选举的服务
 * @date
 */
public class MasterSelector {
    private ZkClient zkClient;
    //需要竞争的节点
    private final static String MASTER_PATH = "/master";
    //注册节点内容变化
    private IZkDataListener dataListener;
    //其他服务器
    private UserCenter server;
    //master节点
    private UserCenter master;
    private static Boolean isRunning = false;
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public MasterSelector(UserCenter server, ZkClient zkClient) {
        this.server = server;
        this.zkClient = zkClient;
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }
            @Override
            public void handleDataDeleted(String s) throws Exception {
                //master被删除，重新进行选举
                checkMaster();
            }
        };
    }

    /**
     * 对外提供开始选举的方法
     */
    public void start() {
        if (!isRunning) {
            isRunning = true;
            //注册节点事件
            zkClient.subscribeDataChanges(MASTER_PATH, dataListener);
            chooseMaster();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            scheduledExecutorService.shutdown();
            zkClient.unsubscribeDataChanges(MASTER_PATH, dataListener);
            releaseMaster();
        }
    }

    /**
     * 选举(都去创建临时节点master)
     */
    private void chooseMaster() {
        if (!isRunning) {
            System.out.println("当前服务没有启动");
            return;
        }
        try {
            zkClient.createEphemeral(MASTER_PATH, server);
            master = server;
            System.out.println(master.getMac_name() + "->我已经被选举为master");
            //定时器    释放master（网络故障）
            scheduledExecutorService.schedule(() ->{
                releaseMaster();
            }, 5, TimeUnit.SECONDS);

        } catch (ZkNodeExistsException e) {
            //master节点已经存在
            UserCenter userCneter = zkClient.readData(MASTER_PATH, true);
            if (userCneter == null) {
                chooseMaster();
            } else {
                master = userCneter;
            }
        }
    }

    /**
     * 释放锁(模拟故障)
     */
    private void releaseMaster() {
        //判断当前是不是master，只有master能释放
        if (checkMaster()) {
            zkClient.deleteRecursive(MASTER_PATH);
        }
    }

    /**
     * 判断当前服务器是不是master
     * @return
     */
    private Boolean checkMaster() {
        UserCenter userCenter = zkClient.readData(MASTER_PATH);
        if (userCenter.getMac_name().equals(server.getMac_name())) {
            master = userCenter;
            return true;
        }
        return false;

    }
}
