package soya.framework.servlet;

import soya.framework.commons.cli.CommandCallable;
import soya.framework.commons.cli.CommandExecutionContext;
import soya.framework.commons.cli.CommandOption;
import soya.framework.commons.cli.CommandParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.StringTokenizer;
import java.util.concurrent.Future;

public class DispatchServlet extends HttpServlet {
    private CommandExecutionContext context;
    private CommandManageBean manageBean;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.context = CommandExecutionContext.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        try {
            Object result = dispatch(req);
            if(result != null) {
                PrintWriter writer = resp.getWriter();
                writer.println(result.toString());
                writer.flush();

                writer.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    private Object dispatch(HttpServletRequest req) throws Exception {
        String group = null;
        String command = null;
        StringTokenizer tokenizer = new StringTokenizer(req.getPathInfo(), "/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (group == null) {
                group = token;
            } else if (command == null) {
                command = token;
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
                if(value != null || !value.isEmpty()) {
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

    private String getValue(Field field, HttpServletRequest request) {
        String value = null;
        CommandOption option = field.getAnnotation(CommandOption.class);
        value = request.getParameter(option.option());

        if (value == null) {
            value = request.getParameter(option.longOption());
        }

        if (value == null) {
            value = request.getHeader(option.option());
        }

        if (value == null) {
            value = request.getHeader(option.longOption());
        }

        if (value == null && !option.referenceKey().isEmpty()) {
            value = context.getProperty(option.referenceKey());
        }

        return value;
    }


}
