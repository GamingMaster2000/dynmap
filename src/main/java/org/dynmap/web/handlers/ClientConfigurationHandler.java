package org.dynmap.web.handlers;

import java.io.BufferedOutputStream;
import java.util.Date;
import java.util.Map;

import org.dynmap.DynmapPlugin;
import org.dynmap.web.HttpHandler;
import org.dynmap.web.HttpRequest;
import org.dynmap.web.HttpResponse;
import org.dynmap.web.HttpStatus;
import org.json.simple.JSONObject;

public class ClientConfigurationHandler implements HttpHandler {
    private DynmapPlugin plugin;
    private Map<?, ?> configuration;
    private byte[] cachedConfiguration = null;
    public ClientConfigurationHandler(DynmapPlugin plugin, Map<?, ?> configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }
    @Override
    public void handle(String path, HttpRequest request, HttpResponse response) throws Exception {
        if (cachedConfiguration == null) {
            JSONObject configurationObject = new JSONObject();
            plugin.events.<JSONObject>trigger("buildclientconfiguration", configurationObject);
            
            String s = configurationObject.toJSONString();

            cachedConfiguration = s.getBytes();
        }
        String dateStr = new Date().toString();

        response.fields.put("Date", dateStr);
        response.fields.put("Content-Type", "text/plain");
        response.fields.put("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
        response.fields.put("Last-modified", dateStr);
        response.fields.put("Content-Length", Integer.toString(cachedConfiguration.length));
        response.status = HttpStatus.OK;

        BufferedOutputStream out = null;
        out = new BufferedOutputStream(response.getBody());
        out.write(cachedConfiguration);
        out.flush();
    }
}
