package soya.framework.action.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.beanutils.ConvertUtils;
import soya.framework.action.*;
import soya.framework.action.oas.swagger.Swagger;
import soya.framework.action.oas.swagger.SwaggerBuilder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ActionServlet extends HttpServlet {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

    private ActionContext context;
    private Swagger swagger;

    private Set<ActionRequestEventListener> actionRequestEventListeners = new HashSet<>();
    private boolean debug;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void addListener(ActionRequestEventListener listener) {
        this.actionRequestEventListeners.add(listener);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String path = this.getServletInfo();

        ServletRegistration registration = config.getServletContext().getServletRegistration(getServletName());
        for (String e : registration.getMappings()) {
            if (e.endsWith("/*")) {
                path = e.substring(0, e.lastIndexOf("/*"));
            }
        }

        this.context = ActionContext.getInstance();

        if (debug) {
            actionRequestEventListeners.add(new DispatcherRequestLogger());
        }

        swagger = SwaggerBuilder.create(context);
        swagger.setBasePath(path);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/swagger.json".equals(req.getPathInfo())) {
            PrintWriter writer = resp.getWriter();
            writer.print(swagger.toJson());

            writer.flush();
            writer.close();

        } else {
            dispatch(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String accept = req.getHeader("accept");
        int status = HttpServletResponse.SC_OK;

        ActionResult result = null;
        try {
            result = dispatch(req);
            notify(ActionRequestEvent.postDispatch(req, result));

        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.setContentType(accept);
        resp.setStatus(status);

        OutputStream out = resp.getOutputStream();
        if (result == null) {

        } else {
            out.write(toByteArray(result));

        }
        out.flush();
        out.close();

    }

    private byte[] toByteArray(ActionResult actionResult) {
        Object result = actionResult.result();
        if (result == null) {
            return new byte[0];

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

            return GSON.toJson(jsonObject).getBytes(StandardCharsets.UTF_8);

        } else if (result instanceof String) {
            return ((String) result).getBytes(StandardCharsets.UTF_8);

        } else {
            return (GSON.toJson(result)).getBytes(StandardCharsets.UTF_8);

        }

    }

    private ActionResult dispatch(HttpServletRequest req) throws Exception {

        String group = null;
        String command = null;

        List<String> pathParams = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(req.getPathInfo(), "/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (group == null) {
                group = token;
            } else if (command == null) {
                command = token;

            } else {
                pathParams.add(token);
            }
        }

        if (group != null && command != null) {
            String uri = group + "://" + command;
            Class<? extends ActionCallable> cls = context.getActionType(ActionName.fromURI(uri));
            ActionCallable task = cls.newInstance();
            Field[] fields = ActionParser.getOptionFields(cls);
            int pathIndex = 0;
            for (Field field : fields) {
                CommandOption option = field.getAnnotation(CommandOption.class);
                String value = null;
                if (CommandOption.ParamType.ReferenceParam.equals(option.paramType())) {
                    value = context.getProperty(option.referenceKey());

                } else if (option.dataForProcessing()) {
                    value = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

                } else if (CommandOption.ParamType.PathParam.equals(option.paramType())) {
                    value = pathParams.get(pathIndex);
                    pathIndex++;

                } else if (CommandOption.ParamType.HeaderParam.equals(option.paramType())) {
                    value = req.getHeader(field.getName());

                } else if (CommandOption.ParamType.QueryParam.equals(option.paramType()) || option.dataForProcessing()) {
                    value = req.getParameter(option.option());
                    if (value == null) {
                        value = req.getParameter(field.getName());
                    }

                }

                if (value != null && !value.isEmpty()) {
                    field.setAccessible(true);
                    field.set(task, convert(value, field.getType()));
                }
            }

            notify(ActionRequestEvent.preDispatch(req, task));

            Future<ActionResult> future = context.getExecutorService().submit(task);
            while (!future.isDone()) {
                Thread.sleep(100l);
            }

            return future.get();

        }

        return null;
    }

    private Object convert(String value, Class<?> type) {
        return ConvertUtils.convert(value, type);
    }

    private void notify(ActionRequestEvent event) {
        actionRequestEventListeners.forEach(e -> {
            e.onEvent(event);
        });
    }

    static class DispatcherRequestLogger implements ActionRequestEventListener {
        private static Logger logger = Logger.getLogger("DispatchLogger");

        @Override
        public void onEvent(ActionRequestEvent event) {
            if (event instanceof ActionRequestEvent.PreActionEvent) {
                onPreDispatchEvent((ActionRequestEvent.PreActionEvent) event);

            } else if (event instanceof ActionRequestEvent.PostActionEvent) {
                onPostDispatchEvent((ActionRequestEvent.PostActionEvent) event);

            }
        }

        private void onPreDispatchEvent(ActionRequestEvent.PreActionEvent event) {
            ActionCallable task = event.getTask();

            Command command = task.getClass().getAnnotation(Command.class);
            String uri = command.group() + "://" + command.name();

            logger.info("Dispatch to " + uri);
        }

        private void onPostDispatchEvent(ActionRequestEvent.PostActionEvent event) {
            ActionResult result = event.getResult();
            if (result.successful()) {
                logger.info("Task succeeded with result: \n" + result.toString());

            } else {
                Exception exception = (Exception) result.result();
                exception.printStackTrace();
            }
        }
    }

}
