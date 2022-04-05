package soya.framework.tool.codegen.rest;

import com.google.common.base.CaseFormat;
import org.apache.commons.cli.Options;
import org.reflections.Reflections;
import soya.framework.commons.cli.*;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.tool.codegen.JavaCodeBuilderCommand;

import java.lang.reflect.Field;
import java.util.*;

@Command(group = "soya-rest", name = "api")
public class RestApiGenerator extends JavaCodeBuilderCommand {

    @CommandOption(option = "g", longOption = "group", required = true)
    protected String group;

    @Override
    protected void printImports(CodeBuilder builder) {
        builder.appendLine("import io.swagger.annotations.Api;")
                .appendLine("import soya.framework.commons.cli.CommandDispatcher;")
                .appendLine("import soya.framework.commons.cli.CommandMapping;")
                .appendLine("import soya.framework.commons.cli.GroupMapping;")
                .appendLine()
                .appendLine("import javax.ws.rs.*;")
                .appendLine("import javax.ws.rs.core.MediaType;")
                .appendLine("import javax.ws.rs.core.Response;")
                .appendLine();


    }

    @Override
    protected void printClass(CodeBuilder builder) {
        String className = group.replaceAll("-", "_");
        className = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, className) + "Controller";
        builder.append("@Path(\"/").append(group).appendLine("\")");
        builder.append("@Api(value = \"").append(group).appendLine("\")");
        builder.append("@GroupMapping(value = \"").append(group).appendLine("\")");
        builder.append("public class ").append(className).appendLine(" extends CommandDispatcher {");
        builder.appendLine();

        indent++;
        builder.append("public", indent).append(" ").append(className).appendLine("() {");
        builder.appendLine("super();", indent + 1);
        builder.appendLine("}", indent).appendLine();
        indent--;

    }

    @Override
    protected void printBody(CodeBuilder builder) {
        Reflections reflections = new Reflections("soya.framework");
        Set<Class<?>> subTypes =
                reflections.getTypesAnnotatedWith(Command.class);
        subTypes.forEach(c -> {
            Command command = c.getAnnotation(Command.class);
            if (command.group().equals(group)) {
                printCommandMethod((Class<? extends CommandCallable>) c, builder);
            }
        });
    }

    private void printCommandMethod(Class<? extends CommandCallable> cls, CodeBuilder builder) {
        Command command = cls.getAnnotation(Command.class);
        CommandMethodMapping mapping = new CommandMethodMapping(cls);
        List<Field> arguments = mapping.getArguments();

        builder.append("@", indent).appendLine(mapping.getHttpMethod());
        builder.append("@Path(\"", indent).append(mapping.getPath()).appendLine("\")");

        if(command.httpRequestTypes().length > 0) {
            builder.append("@Consumes({", indent).append(mediaTypes(command.httpRequestTypes())).appendLine("})");
        }

        if(command.httpResponseTypes().length > 0) {
            builder.append("@Produces({", indent).append(mediaTypes(command.httpResponseTypes())).appendLine("})");
        }

        builder.append("@CommandMapping(", indent)
                .append("command = \"").append(mapping.getCommand()).append("\"")
                .append(", template = \"").append(mapping.getTemplate()).append("\"");;


        builder.append(")").appendLine();

        builder.append("public Response ", indent).append(mapping.getMethodName()).append("(");

        for (int i = 0; i < arguments.size(); i++) {
            Field field = arguments.get(i);
            CommandOption option = field.getAnnotation(CommandOption.class);
            if (i > 0) {
                builder.append(", ");
            }

            if(!option.dataForProcessing()) {
                builder.append("@").append(option.paramType().name()).append("(\"").append(field.getName()).append("\") ");
            }
            builder.append("String ").append(field.getName());
        }

        builder.appendLine(") throws Exception {");
        indent++;

        builder.appendLine("return Response", indent)
                .append(".ok(", indent + 2)
                .append("_dispatch(\"")
                .append(mapping.getMethodName())
                .appendLine("\", ")
                .append(arguments(arguments), indent + 4)
                .append(")")
                .appendLine(")")
                .appendLine(".build();", indent + 2);
        indent--;
        builder.appendLine("}", indent).appendLine();
    }

    private String mediaTypes(Command.MediaType[] mediaTypes) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < mediaTypes.length; i ++) {
            if(i > 0) {
                builder.append(", ");
            }
            builder.append("MediaType.").append(mediaTypes[i].name());
        }
        return builder.toString();
    }

    private String template(Field[] fields) {
        StringBuilder builder = new StringBuilder();

        List<Field> list = Arrays.asList(fields);

        int index = 0;
        for(int i = 0; i < list.size(); i ++) {
            Field field = list.get(i);
            if(i > 0) {
                builder.append(" ");
            }

            CommandOption mapping = field.getAnnotation(CommandOption.class);
            if(mapping.paramType().equals(CommandOption.ParamType.ReferenceParam) && !mapping.referenceKey().isEmpty()) {
                builder.append("-" + mapping.option()).append(" {").append(mapping.referenceKey()).append("}");

            } else {
                builder.append("-" + mapping.option()).append(" {").append(index).append("}");
                index ++;
            }
        }
        return builder.toString();
    }

    private String arguments(List<Field> fields) {
        StringBuilder builder = new StringBuilder("new Object[]{");
        for(int i = 0; i < fields.size(); i ++) {
            Field field = fields.get(i);
            if(i > 0) {
                builder.append(", ");
            }

            CommandOption mapping = field.getAnnotation(CommandOption.class);
            if(mapping.dataForProcessing()) {
                builder.append("encodeMessage(").append(field.getName()).append(")");
            } else {
                builder.append(field.getName());

            }
        }

        builder.append("}");
        return builder.toString();
    }

    class CommandMethodMapping {
        private Class<?> cls;

        private String command;
        private List<Field> fields;

        private String httpMethod;
        private String path;

        private String template;
        private String methodName;

        private List<Field> arguments;

        CommandMethodMapping(Class<? extends CommandCallable> cls) {
            this.cls = cls;
            Command command = cls.getAnnotation(Command.class);

            this.command = command.name();
            this.fields = Arrays.asList(CommandParser.getOptionFields(cls));

            this.httpMethod = command.httpMethod().name();

            StringBuilder pathBuilder = new StringBuilder("/").append(cls.getAnnotation(Command.class).name());
            fields.forEach(e -> {
                CommandOption commandOption = e.getAnnotation(CommandOption.class);
                if(commandOption.paramType().equals(CommandOption.ParamType.PathParam)) {
                    pathBuilder.append("/{").append(e.getName()).append("}");
                }
            });
            this.path = pathBuilder.toString();

            StringBuilder templateBuilder = new StringBuilder();
            int index = 0;
            for(int i = 0; i < fields.size(); i ++) {
                Field field = fields.get(i);
                if(i > 0) {
                    templateBuilder.append(" ");
                }

                CommandOption mapping = field.getAnnotation(CommandOption.class);
                if(mapping.paramType().equals(CommandOption.ParamType.ReferenceParam) && !mapping.referenceKey().isEmpty()) {
                    templateBuilder.append("-" + mapping.option()).append(" {").append(mapping.referenceKey()).append("}");

                } else {
                    templateBuilder.append("-" + mapping.option()).append(" {").append(index).append("}");
                    index ++;
                }
            }
            this.template = templateBuilder.toString();

            methodName = command.name().replaceAll("-", "_");
            methodName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, methodName);

            this.arguments = new ArrayList<>();
            fields.forEach(e -> {
                CommandOption option = e.getAnnotation(CommandOption.class);
                if(option.referenceKey().isEmpty()) {
                    arguments.add(e);
                }
            });

        }

        public String getCommand() {
            return command;
        }

        public List<Field> getFields() {
            return fields;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public String getPath() {
            return path;
        }

        public String getTemplate() {
            return template;
        }

        public String getMethodName() {
            return methodName;
        }

        public List<Field> getArguments() {
            return arguments;
        }
    }

}
