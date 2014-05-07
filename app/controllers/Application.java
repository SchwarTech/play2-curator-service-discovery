package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.schwartech.curator.discovery.CuratorDiscovery;
import com.schwartech.curator.discovery.CuratorDiscoveryPlugin;
import models.InstanceDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import play.*;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.mvc.*;

import java.util.Collection;

public class Application extends Controller {

    public static String queryServiceName = "DEV:V1:ApacheCuratorXDiscovery";

    public static Result echo() {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");
        result.put("message", "Hello World");
        result.put("timestamp", System.currentTimeMillis());
        return ok(result);
    }

    public static Result index2() {
        ServiceInstance instance = CuratorDiscovery.getService(queryServiceName);
        Logger.info("index2.instance: " + instance);
        if (instance == null) {
            return ok("NONE FOUND");
        } else {
            return ok(instance.buildUriSpec());
        }
    }

    public static Result index() {

//        String zooServers = Configuration.root()
//                .getConfig("curator.discovery")
//                .getString("zooServers", "localhost:2181");

        // This shows how to query all the instances in service discovery
        StringBuilder output = new StringBuilder("Searching for: " + queryServiceName + "\n");

        ServiceDiscovery<InstanceDetails> serviceDiscovery = null;
        CuratorFramework client = null;
        try
        {
            String zooServers = "localhost:2181";

            client = CuratorFrameworkFactory.newClient(zooServers, new ExponentialBackoffRetry(1000, 3));
            client.start();

            JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
            serviceDiscovery = ServiceDiscoveryBuilder
                    .builder(InstanceDetails.class)
                    .client(client)
                    .basePath(CuratorDiscovery.getPath())
                    .serializer(serializer)
                    .build();
            serviceDiscovery.start();

            Collection<ServiceInstance<InstanceDetails>> instances = serviceDiscovery.queryForInstances(queryServiceName);

            for ( ServiceInstance<InstanceDetails> instance : instances )
            {
                Logger.info("Payload: " + instance.getPayload());
                Logger.info("uri: " + instance.buildUriSpec());

                String responseData = callService(instance.buildUriSpec());
                Logger.info("response: " + responseData);

                output.append("Found instance: " + instance.buildUriSpec() + ", payload: " + instance.getPayload());
                output.append("\n************** OUTPUT ************");
                output.append("\n" + responseData);
                output.append("\n**********************************");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serviceDiscovery != null) {
                CloseableUtils.closeQuietly(serviceDiscovery);
            }

            if (client != null) {
                CloseableUtils.closeQuietly(client);
            }
        }

        return ok(output.toString());
    }

    public static String callService(String uri) {
//        F.Promise<WS.Response> results = WS.url(uri + "/api/v1/echo").get();
//        return results.get(1500).getBody();
        return "*** NA ** JSS ***";
    }
}
