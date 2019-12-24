package com.study.zookeeper.apiDemo.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * @author XiaoBai
 * @description 节点事件,提供了三种watcher来做节点的监听
 * pathCache  监视一个路径下子节点的创建，删除，节点数据更新
 * nodeCache  监视一个节点的创建、更新、删除
 * treeCache pathCache+nodeCache（监视路径下的创建、更新、删除事件），缓存路径下的所有子节点的数据
 * @date
 */
public class CuratorEventDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorCLientUtils.getInstance();
        //nodeCache
        NodeCache nodeCache = new NodeCache(curatorFramework, "/node", false);
        nodeCache.start(true);

        nodeCache.getListenable().addListener(()-> System.out.println("节点数据发生变化，变化后的结果："+
                new String(nodeCache.getCurrentData().getData())));
        curatorFramework.setData().forPath("/node", "啦啦啦".getBytes());
//        System.in.read();

        //pathChildrenCache
        PathChildrenCache cache = new PathChildrenCache(curatorFramework, "/childNode", true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent)->{
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.println("增加子节点");
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除子节点");
                    break;
                case CHILD_UPDATED:
                    System.out.println("更新子节点");
                    break;
                    default:
                        break;
            }
        });

        curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/childNode", "child".getBytes());
        TimeUnit.SECONDS.sleep(1);
        curatorFramework.setData().forPath("/childNode", "kkk".getBytes());
        TimeUnit.SECONDS.sleep(1);
    }
}
