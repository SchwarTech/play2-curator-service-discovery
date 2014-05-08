package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.schwartech.curator.service.discovery.CuratorServiceDiscovery;
import com.schwartech.curator.service.discovery.InstanceDetails;
import org.apache.curator.x.discovery.ServiceInstance;
import play.Configuration;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.lang.StringBuilder;
import java.util.Collection;

public class Application extends Controller {

    public static Result echo(String msg) {
        ObjectNode result = Json.newObject();
        result.put("status", "OK");
        result.put("message", msg);
        result.put("timestamp", System.currentTimeMillis());
        return ok(result);
    }

    public static Result index() {

        Configuration curatorDiscoveryConf = Configuration.root().getConfig("curator.service.discovery");
        String serviceName = curatorDiscoveryConf.getString("name", "Play2CuratorService");
        String servicePath = curatorDiscoveryConf.getString("path", "/play2-curator-service-discovery-plugin");

        Collection<ServiceInstance<InstanceDetails>> instances = CuratorServiceDiscovery.findServices(servicePath, serviceName);
        return ok(instances.toString());
    }
}