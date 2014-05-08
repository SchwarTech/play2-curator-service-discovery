package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.schwartech.curator.service.discovery.CuratorServiceDiscovery;
import com.schwartech.curator.service.discovery.InstanceDetails;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.mvc.*;

public class Application extends Controller {

    public static String queryServiceName = "DEV:V1:ApacheCuratorXDiscovery";

    public static Result echo(String msg) {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");
        result.put("message", msg);
        result.put("timestamp", System.currentTimeMillis());
        return ok(result);
    }

    public static Result index() {

        int status = 404;
        String wsResultsBody = "";

        ServiceProvider<InstanceDetails> provider = CuratorServiceDiscovery.getServiceProvider(queryServiceName);
        try {
            provider.start();

            ServiceInstance<InstanceDetails> instance = provider.getInstance();
            if (instance == null) {
                wsResultsBody = "Service not found";
            } else {
                InstanceDetails details = (InstanceDetails)instance.getPayload();
                Logger.info("ServiceInstance.details.size: " + details.getSize());
                Logger.info("ServiceInstance.address: " + instance.getAddress());
                Logger.info("ServiceInstance.serviceType: " + instance.getServiceType());
                Logger.info("Calling: " + instance.buildUriSpec() + "/api/v1/echo?msg=" + instance.getId());


//                WS.url
                F.Promise<WS.Response> results = WS.url(instance.buildUriSpec() + "/api/v1/echo").setQueryParameter("msg", instance.getId()).get();
                WS.Response theResponse = results.get(1500);
                status = theResponse.getStatus();
                wsResultsBody = theResponse.getBody();

                if (status != 200) {
                    provider.noteError(instance);
                }
            }
        } catch (Exception e) {
            Logger.error("Error calling service", e);
        } finally {
            CloseableUtils.closeQuietly(provider);
        }

        if (status == 200) {
            return ok(wsResultsBody);
        } else {
            return badRequest(wsResultsBody);
        }
    }
}
