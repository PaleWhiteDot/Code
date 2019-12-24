package com.study.zookeeper.DistributeLock.zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author XiaoBai
 * @description
 * @date
 */
public class MasterChooseTest {
    private final static String CONNECTSTRING = "119.23.187.114:2181";

    public static void main(String[] args) {
        List<ZkClient> clients = new ArrayList<>();
        try {
            for (int i = 0; i < 10; i++) {
                ZkClient client = new ZkClient(CONNECTSTRING, 50000, 50000,
                        new SerializableSerializer());
                clients.add(client);
                UserCenter userCenter = new UserCenter();
                userCenter.setMac_id(i);
                userCenter.setMac_name("客户端：" + i);
                MasterSelector selector = new MasterSelector(userCenter, client);
                selector.start();
                TimeUnit.SECONDS.sleep(4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
