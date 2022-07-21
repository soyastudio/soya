package soya.framework.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.UUID;

public abstract class ServiceEvent {
    private final String id;
    private final long createdTime;

    public ServiceEvent() {
        this.id = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public static ServiceEvent fromJson(String json) {
        if (json == null)
            return null;

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (jsonObject.get("type") != null) {
            try {
                Class<?> type = Class.forName(jsonObject.get("type").getAsString());
                Gson gson = new Gson();
                return (ServiceEvent) gson.fromJson(json, type);

            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Can not find the event type.");

        }
    }
}
