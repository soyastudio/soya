package soya.framework.commandline.tasks.reflect;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import soya.framework.commandline.Command;
import soya.framework.commandline.TaskExecutionContext;

@Command(group = "reflect", name = "context",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class ExecutionContextReflectionTask extends ReflectionTask<String> {

    @Override
    public String execute() throws Exception {
        TaskExecutionContext context = TaskExecutionContext.getInstance();
        TaskExecutionContext.ServiceLocator serviceLocator = (TaskExecutionContext.ServiceLocator) context;

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
