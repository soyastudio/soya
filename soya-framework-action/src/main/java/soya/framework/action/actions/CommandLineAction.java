package soya.framework.action.actions;

import soya.framework.action.*;

import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

public abstract class CommandLineAction implements ActionCallable {

    @CommandOption(option = "c", required = true)
    protected String commandLine;

    @CommandOption(option = "p", dataForProcessing = true)
    protected String payload;

    protected Map<String, ActionName> aliasMap = new Hashtable<>();

    public CommandLineAction() {
        loadCommand();
    }

    @Override
    public ActionResult call() throws Exception {
        ActionCallable callable = null;

        Object[] args = payload == null ? new Object[0] : new Object[]{payload};

        try {
            callable = ActionSignature.builder(URI.create(commandLine)).create().create(args);

        } catch (IllegalArgumentException e) {
            int index = commandLine.indexOf(32);
            if (index > 0) {
                String cmd = commandLine.substring(0, index);
                if (aliasMap.containsKey(cmd)) {
                    // TODO:
                    //callable = ActionParser.fromCommandLine(aliasMap.get(cmd).toString() + " " + commandLine.substring(index).trim());

                } else {
                    throw new IllegalArgumentException("Command not found: " + cmd);
                }

            } else {
                callable = ActionSignature.builder(commandLine).create().create(args);

            }
        }

        return callable.call();

    }

    protected abstract void loadCommand();
}
