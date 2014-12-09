package com.schwartech.curator.service.discovery;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import play.Application;
import play.Play;

import java.util.Collection;

/**
 * Created by jeff on 5/7/14.
 */
public class CuratorServiceDiscovery {

    private static CuratorServiceDiscoveryPlugin getPlugin() {
        Application app = Play.application();

        if (app == null) {
            throw new RuntimeException("No application running");
        }

        CuratorServiceDiscoveryPlugin plugin = app.plugin(CuratorServiceDiscoveryPlugin.class);
        if (plugin == null) {
            throw new RuntimeException("CuratorServiceDiscoveryPlugin not found");
        }

        return plugin;
    }

    public static ServiceProvider<InstanceDetails> getServiceProvider(String queryServiceName) {
        return getPlugin().getServiceProvider(queryServiceName);
    }

    public static ServiceInstance<InstanceDetails> getService(String queryServiceName) {
        return getPlugin().getService(queryServiceName);
    }

    public static String getPath() {
        return getPlugin().getServicePath();
    }

    public static String register(String serviceName, String description, int port, int sslPort) {
        return getPlugin().register(serviceName, description, port, sslPort);
    }

    public static void unregister(String serviceName, String id) {
        getPlugin().unregister();
    }

    public static Collection<ServiceInstance<InstanceDetails>> findServices(String queryServicePath, String queryServiceName) {
        return getPlugin().findServices(queryServicePath, queryServiceName);
    }

}