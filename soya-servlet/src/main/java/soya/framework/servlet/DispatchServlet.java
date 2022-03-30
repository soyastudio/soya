package soya.framework.servlet;

import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandExecutionContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.StringTokenizer;

public class DispatchServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        System.out.println("=============== session id: " + session.getId());

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

            System.out.println(CommandExecutionContext.getInstance().getCommandType(uri).getName());

        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
