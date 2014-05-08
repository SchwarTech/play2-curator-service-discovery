package com.schwartech.curator.service.discovery;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonRootName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * In a real application, the Service payload will most likely
 * be more detailed than this. But, this gives a good example.
 */
@JsonRootName("details")
public class InstanceDetails
{
    private static final String DESCRIPTION = "_description";

    private Map<String, String> details = new HashMap<String, String>();

    public InstanceDetails()
    {
        this("");
    }

    @Override
    public String toString() {
        return "InstanceDetails{" +
                "description='" + getDescription() + '\'' +
                '}';
    }

    public InstanceDetails(String description) {
        setDescription(description);
    }

    @JsonIgnore
    public void setValue(String key, String value) {
        details.put(key, value);
    }

    @JsonIgnore
    public String getValue(String key) {
        return details.get(key);
    }

    @JsonIgnore
    public boolean containsKey(String key) {
        return details.containsKey(key);
    }

    @JsonIgnore
    public int getSize() {
        return details.size();
    }

    @JsonIgnore
    public Set<String> keySet() {
        return details.keySet();
    }

    @JsonIgnore
    public Set<Map.Entry<String, String>> entrySet() {
        return details.entrySet();
    }

    public void setDescription(String description) {
        details.put(DESCRIPTION, description);
    }

    public String getDescription() {
        return details.get(DESCRIPTION);
    }
}