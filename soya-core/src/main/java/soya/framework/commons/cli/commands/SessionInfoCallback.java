package soya.framework.commons.cli.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import soya.framework.commons.cli.Flow;

import java.util.*;

public class SessionInfoCallback implements Flow.Callback {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private SessionInfoCallback() {
    }

    private Appender appender = log -> System.out.println(log);
    private List<Renderer> renderers = new ArrayList<>();

    public static SessionInfoCallback instance() {
        return new SessionInfoCallback();
    }

    public SessionInfoCallback printProperties() {
        renderers.add(new PropertiesRenderer());
        return this;
    }

    public SessionInfoCallback printTaskResult(String task) {
        renderers.add(new TaskResultRenderer(task));
        return this;
    }

    @Override
    public void onSuccess(Flow.Session session) throws Exception {
        renderers.forEach(e -> {
            appender.append(e.render(session));
        });
    }

    interface Appender {
        void append(String log);
    }

    interface Renderer {
        String render(Flow.Session session);
    }

    static class PropertiesRenderer implements Renderer {
        private PropertiesRenderer() {
        }

        @Override
        public String render(Flow.Session session) {
            StringBuilder builder = new StringBuilder("################ Print Session Properties ################\n");

            Properties properties = session.properties();
            List<String> list = new ArrayList<>();
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                list.add((String) enumeration.nextElement());
            }

            Collections.sort(list);
            list.forEach(e -> {
                builder.append(e).append("=").append(properties.getProperty(e)).append("\n");
            });

            return builder.toString();
        }
    }

    static class TaskResultRenderer implements Renderer {
        private String task;

        private TaskResultRenderer(String task) {
            this.task = task;
        }

        @Override
        public String render(Flow.Session session) {
            Object result = session.getResult(task);
            if (result instanceof String) {
                return (String) result;

            } else {
                return GSON.toJson(result);
            }

        }
    }

}
