package soya.framework.oas.commands;

import soya.framework.commons.util.CodeBuilder;
import soya.framework.core.Command;
import soya.framework.core.CommandCallable;
import soya.framework.core.CommandOption;
import soya.framework.oas.swagger.Swagger;
import soya.framework.oas.swagger.SwaggerApiDefinition;

@Command(group = "openapi", name = "command-generate", httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class CommandGenerator implements CommandCallable<String> {

    @CommandOption(option = "p", required = true)
    private String packageName;

    @CommandOption(option = "b", required = true)
    private String baseCommand;

    @CommandOption(option = "d", dataForProcessing = true, required = true)
    private String document;

    @Override
    public String call() throws Exception {
        CodeBuilder builder = CodeBuilder.newInstance();
        SwaggerApiDefinition api = new SwaggerApiDefinition(Swagger.fromJson(document));

        builder.append("package ").append(packageName).appendLine(";");
        builder.appendLine();

        builder.append("public interface ").append(baseCommand).appendLine("<T> extends CommandCallable<T> {");
        builder.appendLine();

        api.getOperations().forEach(e -> {
            printOperation(e, builder);
        });

        builder.appendLine("}");


        return builder.toString();
    }

    private void printOperation(SwaggerApiDefinition.OperationDefinition operation, CodeBuilder builder) {
        String className = operation.getOperation().getOperationId();
        String groupName = baseCommand;
        builder.append("@Command(", 1)
                .append("group = \"").append(groupName).append("\"")
                .append(", name = \"").append(className).append("\"")
                .appendLine(")");

        builder.append("class ", 1)
                .append(className)
                .append(" extends ")
                .append(baseCommand)
                .appendLine("<String> {");
        builder.appendLine();

        builder.appendLine("}", 1);
        builder.appendLine();
    }
}
