package helpers;

import models.InstanceDetails;
import org.apache.curator.test.TestingServer;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * Created by jeff on 5/6/14.
 */
public class ServiceDiscoveryHelper {
    public static final String     PATH = "/discovery/example";
    public static TestingServer server;

    public static org.apache.curator.x.discovery.ServiceDiscovery<InstanceDetails> serviceDiscovery;

}
