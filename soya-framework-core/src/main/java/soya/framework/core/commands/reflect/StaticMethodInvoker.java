package soya.framework.core.commands.reflect;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;

@Command(group = "reflect", name = "static-method-invoker", httpMethod = Command.HttpMethod.POST, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class StaticMethodInvoker extends ReflectCommand<String> {

    @CommandOption(option = "c", required = true)
    private String className;

    @CommandOption(option = "m", required = true)
    private String methodName;

    @CommandOption(option = "s", required = true)
    private String signature;

    @CommandOption(option = "i", required = true, dataForProcessing = true)
    private String inputs;

    @Override
    public String call() throws Exception {
        return null;
    }
}
