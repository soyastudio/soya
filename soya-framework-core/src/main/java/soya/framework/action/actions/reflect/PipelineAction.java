package soya.framework.action.actions.reflect;

import com.google.gson.*;
import soya.framework.action.ActionResult;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.action.Pipeline;

@Command(group = "reflect", name = "pipeline", httpMethod = Command.HttpMethod.POST)
public class PipelineAction extends ReflectionAction<String> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @CommandOption(option = "m", dataForProcessing = true)
    private String message;

    @Override
    protected String execute() throws Exception {

        JsonObject msg = JsonParser.parseString(message).getAsJsonObject();

        JsonObject header = msg.get("pipeline").getAsJsonObject();
        JsonObject payload = msg.get("payload").getAsJsonObject();

        Pipeline pipeline = Pipeline.fromJson(GSON.toJson(header));
        String[] paramNames = pipeline.parameterNames();
        Object[] args = new Object[paramNames.length];

        for (int i = 0; i < paramNames.length; i++) {
            Class<?> type = pipeline.parameterType(paramNames[i]);
            JsonElement jsonElement = payload.get(paramNames[i]);
            if (jsonElement != null) {
                args[i] = GSON.fromJson(jsonElement, type);
            }
        }

        ActionResult result = pipeline.execute(args);
        if(result.result() instanceof String) {
            return (String) result.result();

        } else {
            return GSON.toJson(result.result());

        }
    }
}
