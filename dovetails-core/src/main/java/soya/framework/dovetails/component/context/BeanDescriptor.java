package soya.framework.dovetails.component.context;

import com.google.gson.JsonObject;

class BeanDescriptor {
    private final String name;
    private final String type;
    private final JsonObject configuration;

    public BeanDescriptor(String name, String type, JsonObject configuration) {
        this.name = name;
        this.type = type;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public JsonObject getConfiguration() {
        return configuration;
    }
}
