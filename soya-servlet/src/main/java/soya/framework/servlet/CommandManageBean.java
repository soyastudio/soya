package soya.framework.servlet;

import soya.framework.commons.cli.CommandExecutionContext;

public class CommandManageBean {
    public static final String COMMAND_MANAGE_BEAN = "COMMAND_MANAGE_BEAN";

    private CommandExecutionContext context;

    public CommandManageBean() {
    }

    public CommandManageBean(CommandExecutionContext context) {
        this.context = context;
    }

    public void init() {
        context = CommandExecutionContext.getInstance();

    }

    private void init(CommandExecutionContext context) {

    }


}
