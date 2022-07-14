package soya.framework.action.actions.reflect;

import soya.framework.action.*;
import soya.framework.util.CodeBuilder;

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
            ActionName actionName = ActionName.fromURI(uri);
            if(actionName != null) {
                printCommandDefinition(ActionClass.get(actionName), builder);

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
                printCommandDefinition(ActionClass.get(cmd), builder);
            });
        }
    }

    private void printCommandDefinition(ActionClass cls, CodeBuilder builder) {
        builder.append("### Command: ").appendLine(cls.getActionName().getName());
        builder.append("- uri: ").append(cls.getActionName().toString());
        builder.appendLine();
    }
}
