package soya.framework.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class TaskRunner {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

    public static ActionResult execute(URI uri) throws Exception {
        return execute(ActionParser.fromURI(uri));
    }

    public static ActionResult execute(ActionCallable task) throws Exception {
        Future<ActionResult> future = ActionContext.getInstance().getExecutorService().submit(task);
        while (!future.isDone()) {
            Thread.sleep(100l);
        }
        return future.get();
    }

    public static void main(String[] args) {
        try {
            URI uri = new URI("reflect://jmx-mbeans");
            ActionResult actionResult = execute(uri);

            Object result = actionResult.result();
            String output = null;
            if (result == null) {
                output = "";

            } else if (result instanceof Throwable) {
                Throwable throwable = (Throwable) result;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("exceptionType", throwable.getClass().getName());
                jsonObject.addProperty("message", throwable.getMessage());

                JsonArray array = new JsonArray();
                Throwable cause = throwable.getCause();
                while (cause != null) {
                    JsonObject o = new JsonObject();
                    o.addProperty("exceptionType", throwable.getClass().getName());
                    o.addProperty("message", throwable.getMessage());
                    array.add(o);

                    cause = cause.getCause();
                }

                jsonObject.add("causes", array);

                output =  GSON.toJson(jsonObject);

            } else if (result instanceof String) {
                output = (String) result;

            } else {
                output = GSON.toJson(result);

            }
            System.out.println(output);
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
