package com.study.zookeeper.DistributeLock.zkClient;

import java.io.Serializable;

/**
 * @author XiaoBai
 * @description 模拟竞争Master的机器
 * @date
 */
public class UserCenter implements Serializable {
    private static final long serialVersionUID = 4422973397777606336L;
    //    private static final long serialVersionUID = 1L;
    //机器信息
    private int mac_id;
    //机器名称
    private String mac_name;

    public int getMac_id() {
        return mac_id;
    }

    public void setMac_id(int mac_id) {
        this.mac_id = mac_id;
    }

    public String getMac_name() {
        return mac_name;
    }

    public void setMac_name(String mac_name) {
        this.mac_name = mac_name;
    }
}

