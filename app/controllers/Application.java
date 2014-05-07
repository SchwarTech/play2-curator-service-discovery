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

    public static Result index() {
        ServiceInstance instance = CuratorDiscovery.getService(queryServiceName);
        if (instance == null) {
            return ok("NONE FOUND");
        } else {
            return ok(callService(instance.buildUriSpec()));
        }
    }

    public static String callService(String uri) {
        F.Promise<WS.Response> results = WS.url(uri + "/api/v1/echo").get();
        return results.get(1500).getBody();
    }
}
