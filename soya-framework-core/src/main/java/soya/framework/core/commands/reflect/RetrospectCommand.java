package soya.framework.core.commands.reflect;

import soya.framework.commons.util.CodeBuilder;
import soya.framework.core.Command;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandExecutionContext;

@Command(group = "reflect", name = "retrospect", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class RetrospectCommand extends ReflectCommand<String> {

    @Override
    public String call() throws Exception {
        CommandExecutionContext context = CommandExecutionContext.getInstance();
        CodeBuilder builder = CodeBuilder.newInstance();

        context.groups().forEach(group -> {
            CommandExecutionContext.GroupDescription groupDescription = context.groupDescription(group);
            if (groupDescription != null) {
                builder.append("## ").append(groupDescription.getTitle()).append(" (").append(group).appendLine(")");
                builder.appendLine(groupDescription.getDescription()).appendLine();

                context.getCommands(group).forEach(cmd -> {
                    Class<? extends CommandCallable> cls = context.getCommandType(cmd);
                    printCommandDefinition(cls, builder);
                });
            }
        });

        return builder.toString();
    }

    private void printCommandDefinition(Class<? extends CommandCallable> cls, CodeBuilder builder) {
        Command command = cls.getAnnotation(Command.class);
        if(command != null) {
            builder.append("### Command: ").appendLine(command.name());
            builder.append("- uri: ").append(command.group()).append("://").appendLine(command.name());
            builder.appendLine();
        }
    }
}
