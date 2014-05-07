import helpers.ServiceDiscoveryHelper;
import models.InstanceDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import play.Application;
import play.GlobalSettings;
import play.Logger;

public class Global extends GlobalSettings {

//    private ServiceInstance<InstanceDetails> thisInstance;
    public void onStart(Application app) {

//        CuratorFramework client = null;
//        try
//        {
//            ServiceDiscoveryHelper.server = new TestingServer();
//
//            Logger.info("TestingServer.connectString: " + ServiceDiscoveryHelper.server.getConnectString());
//            client = CuratorFrameworkFactory.newClient(ServiceDiscoveryHelper.server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
//            client.start();
//
//            JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
//
//            String serviceName = "DEV:V1:ApacheCuratorXDiscovery";
//            String description = "ApacheCuratorXDiscovery service";
//
//            // in a real application, you'd have a convention of some kind for the URI layout
//            UriSpec uriSpec = new UriSpec("{scheme}://{address}:{port}");
//
//            thisInstance = ServiceInstance.<InstanceDetails>builder()
//                    .name(serviceName)
//                    .payload(new InstanceDetails(description))
//                    .port(9000) // in a real application, you'd use a common port
//                    .uriSpec(uriSpec)
//                    .build();
//
//            ServiceDiscoveryHelper.serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
//                    .client(client)
//                    .basePath(ServiceDiscoveryHelper.PATH)
//                    .serializer(serializer)
//                    .thisInstance(thisInstance)
//                    .build();
//
//            ServiceDiscoveryHelper.serviceDiscovery.start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

//    public void onStop(Application app) {
//        CloseableUtils.closeQuietly(ServiceDiscoveryHelper.serviceDiscovery);
//    }

}