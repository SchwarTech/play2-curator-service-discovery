package com.schwartech.curator.service.discovery;

import com.schwartech.curator.service.discovery.util.LocalIpV4Filter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
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
public class CuratorServiceDiscoveryPlugin extends Plugin {

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

    public CuratorServiceDiscoveryPlugin(Application application) {
        this.application = application;
    }

    public String getServicePath() {
        return this.servicePath;
    }

    /**
     * Reads the configuration file and initializes com settings.
     */
    public void onStart() {
        Configuration curatorDiscoveryConf = Configuration.root().getConfig("curator.service.discovery");

        if (curatorDiscoveryConf == null) {
            Logger.info("Curator Discovery settings not found.");
        } else {
            serviceName = curatorDiscoveryConf.getString("name", "Play2CuratorService");
            serviceDescription = curatorDiscoveryConf.getString("description", "Play2 Curator Service");
            servicePath = curatorDiscoveryConf.getString("path", "/play2-curator-service-discovery-plugin");
            autoRegister = curatorDiscoveryConf.getBoolean("autoregister", Boolean.TRUE);

            int port = Configuration.root().getInt("http.port", 9000);

            Logger.info("CuratorServiceDiscoveryPlugin Settings:");
            Logger.info(" * serviceName: " + serviceName);
            Logger.info(" * serviceDescription: " + serviceDescription);
            Logger.info(" * servicePath: " + servicePath);
            Logger.info(" * autoRegister: " + autoRegister);
            Logger.info(" * http.port: " + port);

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
                register(serviceName, serviceDescription, port);
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
            Logger.error("Error getting discovery", e);
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

            ServiceInstanceBuilder builder = ServiceInstance.<InstanceDetails>builder()
                    .name(serviceName)
                    .payload(new InstanceDetails(description))
                    .port(port)
//                    .address("127.0.0.1")
//                    .sslPort(8443)
                    .uriSpec(uriSpec);

//            Logger.info("Installing IPV4 Filter");
//            builder.setLocalIpFilter(new LocalIpV4Filter());

            thisInstance = builder.build();

            getServiceDiscovery(servicePath,thisInstance);
        } catch (Exception e) {
            Logger.error("Error registering discovery.", e);
        }

        if (thisInstance == null) {
            Logger.error("Error registering discovery: " + serviceName);
        } else {
            serviceId = thisInstance.getId();
            Logger.info("Service registered: " + serviceName + "/" + serviceId);
        }

        findService(servicePath, serviceName);

        return serviceId;
    }

    public void unregister() {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(client);
    }

    public ServiceProvider<InstanceDetails> getServiceProvider(String queryServiceName) {
        findService(servicePath, serviceName);

        return getServiceDiscovery(servicePath).serviceProviderBuilder()
                    .serviceName(queryServiceName)
                    .providerStrategy(new RandomStrategy<InstanceDetails>())
                    .build();
    }

    public ServiceInstance getService(String queryServiceName) {
        ServiceInstance instance = null;

        try {
            ServiceProvider provider = getServiceProvider(queryServiceName);

            provider.start();

            if (provider == null) {
                return null;
            }
            instance = provider.getInstance();
//            Logger.info("Found instance at: " + instance.buildUriSpec());

            CloseableUtils.closeQuietly(provider);
        } catch (Exception e) {
            Logger.error("Error getting discovery provider", e);
        }

        return instance;
    }

    public Collection<ServiceInstance<InstanceDetails>> findService(String queryServicePath, String queryServiceName) {
        Collection<ServiceInstance<InstanceDetails>> instances = new ArrayList<>();
        try {
            instances = getServiceDiscovery(queryServicePath).queryForInstances(queryServiceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }
}