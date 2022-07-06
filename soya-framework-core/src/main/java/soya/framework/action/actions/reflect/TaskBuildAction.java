package soya.framework.action.actions.reflect;

import soya.framework.action.*;
import soya.framework.util.CodeBuilder;

import java.lang.reflect.Field;
import java.net.URI;

@Command(group = "reflect", name = "task-builder", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class TaskBuildAction extends ReflectionAction<String> {

    @CommandOption(option = "t", required = true)
    private String uri;

    @Override
    protected String execute() throws Exception {
        Class<? extends ActionCallable> cls = ActionContext.getInstance().getActionType(ActionName.fromURI(new URI(uri)));
        Field[] fields = ActionParser.getOptionFields(cls);

        CodeBuilder builder = CodeBuilder.newInstance();
        builder.appendLine("task:");
        builder.append("name: ", 1).appendLine(cls.getSimpleName());
        builder.append("uri: ", 1).appendLine(uri);

        if(fields.length > 0) {
            builder.appendLine("options: ", 1);
            for(Field field: fields) {
                CommandOption option = field.getAnnotation(CommandOption.class);
                builder.append(field.getName(), 2)
                        .append(": ")
                        .append("<")
                        .append(field.getType().getSimpleName())
                        .append(">")
                        .append("\t\t# ")
                        .append(option.desc());

                if(option.required()) {
                    builder.appendLine("(required)");
                } else {
                    builder.appendLine("(optional)");
                }
            }
        }

        builder.appendLine("onSuccess: ", 1);
        builder.appendLine("onFailure: ", 1);

        return builder.toString();
    }
}
