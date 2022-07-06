package soya.framework.action.actions.reflect;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import soya.framework.action.Command;
import soya.framework.action.ActionContext;

@Command(group = "reflect", name = "execution-context",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class ExecutionContextReflectionAction extends ReflectionAction<String> {

    @Override
    public String execute() throws Exception {
        ActionContext context = ActionContext.getInstance();
        ActionContext.ServiceLocator serviceLocator = (ActionContext.ServiceLocator) context;

        JsonObject jsonObject = new JsonObject();

        JsonArray properties = new JsonArray();
        context.getProperties().entrySet().forEach(e -> {
            JsonObject o = new JsonObject();
            o.addProperty("name", (String) e.getKey());
            o.addProperty("value", (String) e.getValue());
            properties.add(o);
        });

        jsonObject.add("properties", properties);

        JsonArray services = new JsonArray();
        serviceLocator.serviceTypes().forEach(e -> {
            services.add(e);
        });
        jsonObject.add("service-types", services);

        return new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
    }
}
