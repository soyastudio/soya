package soya.framework.core.tasks.reflect;

import soya.framework.util.CodeBuilder;
import soya.framework.core.*;

import java.net.URI;

@Command(group = "reflect", name = "help", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class HelpTask extends ReflectionTask<String> {

    @CommandOption(option = "t")
    protected String topic;

    @Override
    public String execute() throws Exception {
        TaskExecutionContext context = TaskExecutionContext.getInstance();
        CodeBuilder builder = CodeBuilder.newInstance();

        if (topic == null) {
            printAll(context, builder);

        } else if (topic.contains("://")) {
            URI uri = URI.create(topic);
            if(context.getTaskType(TaskName.fromURI(uri)) != null) {
                printCommandDefinition(context.getTaskType(TaskName.fromURI(uri)), builder);

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

    private void printAll(TaskExecutionContext context, CodeBuilder builder) {
        context.groups().forEach(group -> {
            printGroup(group, context, builder);
        });
    }

    private void printGroup(String group, TaskExecutionContext context, CodeBuilder builder) {
        TaskExecutionContext.GroupDescription groupDescription = context.groupDescription(group);
        if (groupDescription != null) {
            builder.append("## ").append(groupDescription.getTitle()).append(" (").append(group).appendLine(")");
            builder.appendLine(groupDescription.getDescription()).appendLine();

            context.getCommands(group).forEach(cmd -> {
                Class<? extends TaskCallable> cls = context.getTaskType(cmd);
                printCommandDefinition(cls, builder);
            });
        }
    }

    private void printCommandDefinition(Class<? extends TaskCallable> cls, CodeBuilder builder) {
        Command command = cls.getAnnotation(Command.class);
        if (command != null) {
            builder.append("### Command: ").appendLine(command.name());
            builder.append("- uri: ").append(command.group()).append("://").appendLine(command.name());
            builder.appendLine();
        }
    }
}
