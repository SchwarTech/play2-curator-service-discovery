package com.schwartech.curator.discovery;

import org.apache.curator.test.TestingServer;
import play.Application;
import play.Configuration;
import play.Logger;
import play.Plugin;


/**
 * Created by jeff on 5/7/14.
 */

/**
 * A Play plugin that facilitates Curator Discovery
 */
public class CuratorDiscoveryPlugin extends Plugin {

    private final Application application;
    private String zooServers;

    private TestingServer mockZooKeeper;

    public CuratorDiscoveryPlugin(Application application) {
        this.application = application;
    }

    /**
     * Reads the configuration file and initializes com settings.
     */
    public void onStart() {

        Configuration curatorDiscoveryConf = Configuration.root().getConfig("curator.discovery");

        if (curatorDiscoveryConf == null) {
            Logger.info("Curator Discovery settings not found.");
        } else {
//            username = curatorDiscoveryConf.getString("username", "root");
//            password = curatorDiscoveryConf.getString("password", "secret");
//
//            memBuf = curatorDiscoveryConf.getLong("memBuf", 1000000L);
//            timeout =curatorDiscoveryConf.getLong("timeout", 1000L);
//            numThreads = curatorDiscoveryConf.getInt("numThreads", 10);
//
//            instanceName = curatorDiscoveryConf.getString("instanceName", "mock-instance");
//            zooServers = curatorDiscoveryConf.getString("zooServers", "localhost:2181");
//
//            batchWriterConfig = new BatchWriterConfig()
//                    .setMaxMemory(memBuf)
//                    .setMaxWriteThreads(numThreads)
//                    .setTimeout(timeout, TimeUnit.MILLISECONDS);
            Logger.info("Curator Discovery settings found.  ZooKeeper servers: " + zooServers);
        }
    }

    public void onStop() {

    }

    public String register(String serviceName, String description) {
        String id = null;
        return id;
    }

    public boolean unregister(String serviceName, String id) {
        return false;
    }

//    public Connector getConnector() throws AccumuloSecurityException, AccumuloException {
//        return getConnector(username, password);
//    }
//
//    public Connector getConnector(String username, String password) throws AccumuloSecurityException, AccumuloException {
//        Instance inst = getZooKeeper();
//        long t1 = System.currentTimeMillis();
//
//        PasswordToken token = new PasswordToken(password.getBytes());
//        return inst.getConnector(username, token);
//    }
//
//    public BatchWriterConfig getDefaultWriterConfig() {
//        return batchWriterConfig;
//    }
//
//    private Instance getZooKeeper() {
//        Instance instance;
//        if (instanceName.contains("mock")) {
//            if (mockInstance == null) {
//                mockInstance = new MockInstance();
//            }
//            instance = mockInstance;
//        } else {
//            instance = new ZooKeeperInstance(instanceName, zooServers);
//        }
//
//        return instance;
//    }
}
