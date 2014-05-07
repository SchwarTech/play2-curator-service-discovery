package com.schwartech.curator.discovery;

import models.InstanceDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import play.Application;
import play.Configuration;
import play.Logger;
import play.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by jeff on 5/7/14.
 */

/**
 * A Play plugin that facilitates Curator Discovery
 */
public class CuratorDiscoveryPlugin extends Plugin {

    private final Application application;
    private String zooServers;
    private CuratorFramework client = null;
    private ServiceDiscovery serviceDiscovery;
    private boolean autoRegister = true;

    private String serviceId;
    private String serviceName;
    private String serviceDescription;
    private String servicePath;
    private ServiceInstance<InstanceDetails> thisInstance;

    private TestingServer mockZooKeeper;

    public CuratorDiscoveryPlugin(Application application) {
        this.application = application;
    }

    public String getServicePath() {
        return this.servicePath;
    }

    /**
     * Reads the configuration file and initializes com settings.
     */
    public void onStart() {
        Configuration curatorDiscoveryConf = Configuration.root().getConfig("curator.discovery");

        if (curatorDiscoveryConf == null) {
            Logger.info("Curator Discovery settings not found.");
        } else {
            serviceName = curatorDiscoveryConf.getString("service.name", "");
            serviceDescription = curatorDiscoveryConf.getString("service.description", "");
            servicePath = curatorDiscoveryConf.getString("service.path", "");
            autoRegister = curatorDiscoveryConf.getBoolean("autoregister", Boolean.TRUE);

            zooServers = curatorDiscoveryConf.getString("zooServers", "localhost:2181");
            if (zooServers.toLowerCase().contains("mock")) {
                try {
                    mockZooKeeper = new TestingServer(2181);
                    zooServers = mockZooKeeper.getConnectString();
                    Logger.info("Mock ZooKeeper started at: " + zooServers);
                } catch (Exception e) {
                    Logger.error("Could not start mock ZooKeeper server on port 2181: " + e.getMessage());
                    return;
                }
            }

            Logger.info("Curator Discovery settings found.  ZooKeeper servers: " + zooServers);
            if (autoRegister) {
                register(serviceName, serviceDescription, 9000);
            }
        }
    }

    public void onStop() {
        unregister();

        if (mockZooKeeper != null) {
            try {
                mockZooKeeper.close();
            } catch (IOException e) {
                Logger.error("Error shutting down mock ZooKeeper server: " + e.getMessage());
            }
        }

        CloseableUtils.closeQuietly(client);
        CloseableUtils.closeQuietly(serviceDiscovery);
    }

    private ServiceDiscovery<InstanceDetails> getServiceDiscovery(String basePath, ServiceInstance<InstanceDetails> thisInstance) {
        if (serviceDiscovery == null) {
            JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);

            serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                    .client(getClient())
                    .basePath(basePath)
                    .thisInstance(thisInstance)
                    .serializer(serializer)
                    .build();
        }

        try {
            serviceDiscovery.start();
        } catch (Exception e) {
            Logger.error("Error getting service discovery", e);
        }

        return serviceDiscovery;
    }

    private ServiceDiscovery<InstanceDetails> getServiceDiscovery(String basePath) {
        return getServiceDiscovery(basePath, null);
    }

    private CuratorFramework getClient() {
        if (client == null) {
            client = CuratorFrameworkFactory.newClient(zooServers, new ExponentialBackoffRetry(1000, 3));
        }

        if (!client.getState().equals(CuratorFrameworkState.STARTED)) {
            client.start();
        }

        return client;
    }

    public String register(String serviceName, String description, int port) {

        try
        {
            // in a real application, you'd have a convention of some kind for the URI layout
            UriSpec uriSpec = new UriSpec("{scheme}://{address}:{port}");

            thisInstance = ServiceInstance.<InstanceDetails>builder()
                    .name(serviceName)
                    .payload(new InstanceDetails(description))
                    .port(port)
                    .uriSpec(uriSpec)
                    .build();

            getServiceDiscovery(servicePath,thisInstance);
        } catch (Exception e) {
            Logger.error("Error registering service.", e);
        }

        if (thisInstance == null) {
            Logger.error("Error registering service: " + serviceName);
        } else {
            serviceId = thisInstance.getId();
            Logger.info("Service registered: " + serviceName + "/" + serviceId);
        }

        return serviceId;
    }

    public void unregister() {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(client);
    }

    public ServiceInstance getService(String queryServiceName) {
        ServiceInstance instance = null;

        try {
            ServiceProvider provider = getServiceDiscovery(servicePath)
                    .serviceProviderBuilder()
                        .serviceName(queryServiceName)
                        .providerStrategy(new RoundRobinStrategy())
                        .build();

            provider.start();

            if (provider == null) {
                return null;
            }
            instance = provider.getInstance();
            Logger.info("Found instance at: " + instance.buildUriSpec());

            CloseableUtils.closeQuietly(provider);
        } catch (Exception e) {
            Logger.error("Error getting service provider", e);
        }

        return instance;
    }

    public Collection<ServiceInstance<InstanceDetails>> findService(String queryServicePath, String queryServiceName) {
        Logger.debug("findService: " + queryServiceName);

        Collection<ServiceInstance<InstanceDetails>> instances = new ArrayList<>();
//        try
//        {
//            instances = getServiceDiscovery(queryServicePath).queryForInstances(queryServiceName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return instances;
    }
}