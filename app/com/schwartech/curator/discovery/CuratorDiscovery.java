package com.schwartech.curator.discovery;

import org.apache.curator.x.discovery.ServiceInstance;
import play.Application;
import play.Configuration;
import play.Play;

/**
 * Created by jeff on 5/7/14.
 */
public class CuratorDiscovery {
    private static String serviceName = "";
    private static String serviceDescription = "";
    private static String zooServers = "";

    private static CuratorDiscoveryPlugin getPlugin() {
        Application app = Play.application();
        if(app == null) {
            throw new RuntimeException("No application running");
        }

        CuratorDiscoveryPlugin plugin = app.plugin(CuratorDiscoveryPlugin.class);
        if(plugin == null) {
            throw new RuntimeException("CuratorDiscoveryPlugin not found");
        }

        serviceName = Configuration.root().getConfig("curator.discovery").getString("service.name", "");
        serviceDescription = Configuration.root().getConfig("curator.discovery").getString("service.description", "");
        zooServers = Configuration.root().getConfig("curator.discovery").getString("zooServers", "");

        return plugin;
    }

    public static ServiceInstance getService(String queryServiceName) {
        return getPlugin().getService(queryServiceName);
    }

    public static String getPath() {
        return getPlugin().getServicePath();
    }
//    public static String register(String serviceName, String description) {
//        return getPlugin().register(serviceName, description);
//    }
//
//    public static boolean unregister(String serviceName, String id) {
//        return getPlugin().unregister(serviceName, id);
//    }

}