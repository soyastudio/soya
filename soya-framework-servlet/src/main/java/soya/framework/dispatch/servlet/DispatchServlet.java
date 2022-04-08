package soya.framework.dispatch.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandExecutionContext;
import soya.framework.core.CommandOption;
import soya.framework.core.CommandParser;
import soya.framework.dispatch.swagger.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DispatchServlet extends HttpServlet {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private CommandExecutionContext context;

    private Swagger swagger;

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

        this.context = CommandExecutionContext.getInstance();

        swagger = SwaggerBuilder.create(context, path);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/swagger.json".equals(req.getPathInfo())) {
            PrintWriter writer = resp.getWriter();
            writer.print(GSON.toJson(swagger));

            //writer.print(IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("swagger.json")));

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

    private void dispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String accept = req.getHeader("accept");
        int status = HttpServletResponse.SC_OK;
        Object result = null;
        try {
            result = dispatch(req);

        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.setContentType(accept);
        resp.setStatus(status);
        PrintWriter writer = resp.getWriter();
        if (result == null) {

        } else if (result instanceof String) {
            writer.print(result);

        } else {
            writer.print(result);

        }
        writer.flush();

        writer.close();

    }

    private Object dispatch(HttpServletRequest req) throws Exception {
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

            Class<? extends CommandCallable> cls = context.getCommandType(uri);
            CommandCallable<?> cmd = cls.newInstance();
            Field[] fields = CommandParser.getOptionFields(cls);
            for (Field field : fields) {
                CommandOption option = field.getAnnotation(CommandOption.class);
                String value = getValue(field, req);
                if (value != null && !value.isEmpty()) {
                    field.setAccessible(true);
                    field.set(cmd, value);
                }
            }

            Future<?> future = context.getExecutorService().submit(cmd);
            while (!future.isDone()) {
                Thread.sleep(100l);
            }

            return future.get();

        }

        return null;
    }

    private String getValue(Field field, HttpServletRequest request) throws IOException {
        String value = null;

        CommandOption option = field.getAnnotation(CommandOption.class);


        if(option.dataForProcessing()) {
            value = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        } else {
            value = request.getParameter(option.option());

            if (value == null) {
                value = request.getParameter(field.getName());
            }

            if (value == null) {
                value = request.getHeader(option.option());
            }

            if (value == null) {
                value = request.getHeader(field.getName());
            }

            if (value == null && !option.referenceKey().isEmpty()) {
                value = context.getProperty(option.referenceKey());
            }

        }

        return value;
    }

}
