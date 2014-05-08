package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.schwartech.curator.service.discovery.CuratorServiceDiscovery;
import com.schwartech.curator.service.discovery.InstanceDetails;
import org.apache.curator.utils.CloseableExecutorService;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.mvc.*;

import java.net.ConnectException;

public class Application extends Controller {

    public static String queryServiceName = "DEV:V1:ApacheCuratorXDiscovery";

    private static WS.Response callEchoService(String url, String msg) throws Exception {
        F.Promise<WS.Response> results = WS.url(url).setQueryParameter("msg", msg).get();
        return results.get(1500);
    }

    private static WS.Response getProviderAndExecute(String queryServiceName) throws Exception {
        WS.Response theResponse = null;

        ServiceProvider<InstanceDetails> provider = CuratorServiceDiscovery.getServiceProvider(queryServiceName);
        try {
            provider.start();

            ServiceInstance<InstanceDetails> instance = provider.getInstance();
            if (instance == null) {
                return theResponse;
            } else {
                Logger.info("Exec:" + instance.buildUriSpec() + "/api/v1/echo?msg=" + instance.getId());

                try {
                    theResponse = callEchoService(instance.buildUriSpec() + "/api/v1/echo", instance.buildUriSpec() + "/" + instance.getId());
                    if (theResponse != null && theResponse.getStatus() >= 300) {
                        provider.noteError(instance);
                    }
                } catch (Exception ce) {
                    provider.noteError(instance);
                }
            }
        } catch (Exception e) {
            Logger.error("Error calling service", e);
        } finally {
            CloseableUtils.closeQuietly(provider);
        }

        return theResponse;
    }

    public static Result index() {

        int status = 404;
        String wsResultsBody = "";

        int count = 0;
        boolean valid = false;
        while (!valid && count < 3) {
            count++;
            Logger.info("Looking for '" + queryServiceName + "', count="+count);
            try {
                WS.Response theResponse = getProviderAndExecute(queryServiceName);
                if (theResponse == null) {
                    wsResultsBody = "Not Found";
                } else {
                    status = theResponse.getStatus();
                    wsResultsBody = theResponse.getBody();
                    valid = true;
                }
            } catch (Exception ce) {
                Logger.error("Error calling webservice.  Trying again - careful this could be an infinite loop.  message: " + ce.getMessage());
            }
        }

        if (status == 200) {
            return ok(wsResultsBody);
        } else {
            return badRequest(wsResultsBody);
        }
    }
}