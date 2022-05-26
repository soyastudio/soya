package soya.framework.dispatch.servlet;

import soya.framework.commandline.*;
import soya.framework.commandline.oas.swagger.Swagger;
import soya.framework.commandline.oas.swagger.SwaggerBuilder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DispatchServlet extends HttpServlet {

    private TaskExecutionContext context;
    private Swagger swagger;

    private Set<DispatchRequestEventListener> dispatchRequestEventListeners = new HashSet<>();
    private boolean debug;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void addListener(DispatchRequestEventListener listener) {
        this.dispatchRequestEventListeners.add(listener);
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

        this.context = TaskExecutionContext.getInstance();

        if(debug) {
            dispatchRequestEventListeners.add(new DispatcherRequestLogger());
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

        TaskResult result = null;
        try {
            result = dispatch(req);
            notify(DispatchRequestEvent.postDispatch(req, result));

        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.setContentType(accept);
        resp.setStatus(status);

        OutputStream out = resp.getOutputStream();
        if (result == null) {

        } else {
            out.write(result.toByteArray());

        }
        out.flush();
        out.close();

    }

    private TaskResult dispatch(HttpServletRequest req) throws Exception {

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
            Class<? extends TaskCallable> cls = context.getTaskType(TaskName.fromURI(uri));
            TaskCallable task = cls.newInstance();
            Field[] fields = TaskParser.getOptionFields(cls);
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
                    field.set(task, value);
                }
            }

            notify(DispatchRequestEvent.preDispatch(req, task));

            Future<TaskResult> future = context.getExecutorService().submit(task);
            while (!future.isDone()) {
                Thread.sleep(100l);
            }

            return future.get();

        }

        return null;
    }

    private void notify(DispatchRequestEvent event) {
        dispatchRequestEventListeners.forEach(e -> {
            e.onEvent(event);
        });
    }

    static class DispatcherRequestLogger implements DispatchRequestEventListener {
        private static Logger logger = Logger.getLogger("DispatchLogger");

        @Override
        public void onEvent(DispatchRequestEvent event) {
            if(event instanceof DispatchRequestEvent.PreDispatchEvent) {
                onPreDispatchEvent((DispatchRequestEvent.PreDispatchEvent) event);

            } else if(event instanceof DispatchRequestEvent.PostDispatchEvent) {
                onPostDispatchEvent((DispatchRequestEvent.PostDispatchEvent) event);

            }
        }

        private void onPreDispatchEvent(DispatchRequestEvent.PreDispatchEvent event) {
            TaskCallable task = event.getTask();

            Command command = task.getClass().getAnnotation(Command.class);
            String uri = command.group() + "://" + command.name();

            logger.info("Dispatch to " + uri);
        }

        private void onPostDispatchEvent(DispatchRequestEvent.PostDispatchEvent event) {
            TaskResult result = event.getResult();
            if(result.successful()) {
                logger.info("Task succeeded with result: \n" + result.toString());

            } else {
                Exception exception = (Exception) result.result();
                exception.printStackTrace();
            }
        }
    }

}
