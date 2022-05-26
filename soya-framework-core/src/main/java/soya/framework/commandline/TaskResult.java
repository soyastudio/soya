package soya.framework.commandline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;

public abstract class TaskResult {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String uri;

    protected TaskResult(TaskCallable taskCallable) {
        Command command = taskCallable.getClass().getAnnotation(Command.class);
        this.uri = command.group() + "://" + command.name();
    }

    public abstract boolean successful();

    public abstract Object result();

    public String toString() {
        Object result = result();
        if (result == null) {
            return "";

        } else if (result instanceof String) {
            return (String) result;

        } else {
            return GSON.toJson(result);

        }
    }

    public byte[] toByteArray() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    public static TaskResult completed(TaskCallable task, Object object) {
        return new SuccessResult(task, object);
    }

    public static TaskResult failed(TaskCallable task, Throwable exception) {
        return new FailureResult(task, exception);
    }

    static class SuccessResult extends TaskResult {
        private final Object result;

        protected SuccessResult(TaskCallable task, Object result) {
            super(task);
            this.result = result;
        }

        @Override
        public Object result() {
            return result;
        }

        @Override
        public boolean successful() {
            return true;
        }
    }

    static class FailureResult extends TaskResult {

        private final Throwable exception;

        protected FailureResult(TaskCallable task, Throwable exception) {
            super(task);
            this.exception = exception;
        }

        @Override
        public Throwable result() {
            return exception;
        }

        @Override
        public boolean successful() {
            return false;
        }
    }

    interface Renderer<T> {
        String render(T object);

        byte[] renderAsByteArray(T object);
    }

    static class GsonRenderer implements Renderer {
        private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        GsonRenderer() {
        }

        @Override
        public String render(Object object) {
            if (object instanceof Throwable) {
                Throwable throwable = (Throwable) object;
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

                return GSON.toJson(jsonObject);

            } else {
                return GSON.toJson(object);

            }
        }

        @Override
        public byte[] renderAsByteArray(Object object) {
            return new byte[0];
        }
    }
}
