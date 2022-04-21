package soya.framework.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class TaskResult {

    private static GsonRenderer gsonRenderer = new GsonRenderer();

    public abstract boolean successful();

    public abstract Object result();

    public String toString() {
        Object result = result();
        if (result == null) {
            return "";

        } else if (result instanceof String) {
            return (String) result;

        } else {
            // FIXME:
            return result.toString();

        }
    }

    public String toJson() {
        return gsonRenderer.render(result());
    }

    public <T> T render(Renderer<T> renderer) {
        return renderer.render(result());
    }

    public static TaskResult completed(Object object) {
        return new SuccessResult(object);
    }

    public static TaskResult failed(Throwable exception) {
        return new FailureResult(exception);
    }

    static class SuccessResult extends TaskResult {
        private final Object result;

        public SuccessResult(Object result) {
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

        public FailureResult(Throwable exception) {
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
        T render(Object object);
    }

    static class GsonRenderer implements Renderer<String> {
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
    }
}
