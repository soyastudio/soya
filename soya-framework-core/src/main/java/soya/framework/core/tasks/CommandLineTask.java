package soya.framework.core.tasks;

import soya.framework.core.*;

import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

public abstract class CommandLineTask implements TaskCallable {

    @CommandOption(option = "c", required = true)
    protected String commandLine;

    @CommandOption(option = "p", dataForProcessing = true)
    protected String payload;

    protected Map<String, TaskName> aliasMap = new Hashtable<>();

    public CommandLineTask() {
        loadCommand();
    }

    @Override
    public TaskResult call() throws Exception {
        TaskCallable callable = null;

        try {
            callable = TaskParser.fromURI(URI.create(commandLine), payload);

        } catch (IllegalArgumentException e) {
            int index = commandLine.indexOf(32);
            if(index > 0) {
                String cmd = commandLine.substring(0, index);
                if(aliasMap.containsKey(cmd)) {
                    callable = TaskParser.fromCommandLine(aliasMap.get(cmd).toString() + " " + commandLine.substring(index).trim());

                } else {
                    throw new IllegalArgumentException("Command not found: " + cmd);
                }

            } else {
                callable = TaskParser.fromCommandLine(commandLine, payload);

            }
        }

        return callable.call();

    }

    protected abstract void loadCommand();
}
