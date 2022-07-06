package soya.framework.action.actions.reflect;

import soya.framework.util.CodeBuilder;
import soya.framework.action.*;

import java.net.URI;

@Command(group = "reflect", name = "help", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class HelpAction extends ReflectionAction<String> {

    @CommandOption(option = "t")
    protected String topic;

    @Override
    public String execute() throws Exception {
        ActionContext context = ActionContext.getInstance();
        CodeBuilder builder = CodeBuilder.newInstance();

        if (topic == null) {
            printAll(context, builder);

        } else if (topic.contains("://")) {
            URI uri = URI.create(topic);
            if(context.getActionType(ActionName.fromURI(uri)) != null) {
                printCommandDefinition(context.getActionType(ActionName.fromURI(uri)), builder);

            } else {
                throw new IllegalArgumentException("Task is not defined with uri: " + uri);
            }


        } else if (context.containsGroup(topic)) {
            printGroup(topic, context, builder);

        } else {
            printAll(context, builder);
        }

        return builder.toString();
    }

    private void printAll(ActionContext context, CodeBuilder builder) {
        context.groups().forEach(group -> {
            printGroup(group, context, builder);
        });
    }

    private void printGroup(String group, ActionContext context, CodeBuilder builder) {
        ActionContext.GroupDescription groupDescription = context.groupDescription(group);
        if (groupDescription != null) {
            builder.append("## ").append(groupDescription.getTitle()).append(" (").append(group).appendLine(")");
            builder.appendLine(groupDescription.getDescription()).appendLine();

            context.getCommands(group).forEach(cmd -> {
                Class<? extends ActionCallable> cls = context.getActionType(cmd);
                printCommandDefinition(cls, builder);
            });
        }
    }

    private void printCommandDefinition(Class<? extends ActionCallable> cls, CodeBuilder builder) {
        Command command = cls.getAnnotation(Command.class);
        if (command != null) {
            builder.append("### Command: ").appendLine(command.name());
            builder.append("- uri: ").append(command.group()).append("://").appendLine(command.name());
            builder.appendLine();
        }
    }
}
