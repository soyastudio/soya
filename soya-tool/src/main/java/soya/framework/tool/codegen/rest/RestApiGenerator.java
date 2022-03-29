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
                .appendLine("import org.springframework.stereotype.Controller;")
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
        builder.appendLine("@Controller");
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
        String httpMethod = command.httpMethod().name();

        Field[] fields = CommandParser.getOptionFields(cls);
        String methodName = command.name().replaceAll("-", "_");
        methodName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, methodName);

        builder.append("@", indent).appendLine(httpMethod);
        builder.append("@Path(\"", indent).append(path(cls)).appendLine("\")");

        if(command.httpRequestTypes().length > 0) {
            builder.append("@Consumes({", indent).append(mediaTypes(command.httpRequestTypes())).appendLine("})");
        }

        if(command.httpResponseTypes().length > 0) {
            builder.append("@Produces({", indent).append(mediaTypes(command.httpResponseTypes())).appendLine("})");
        }

        builder.append("@CommandMapping(", indent)
                .append("command = \"").append(command.name()).append("\"");
        if(fields.length > 0) {
            builder.append(", template = \"").append(template(fields)).append("\"");
        }

        builder.append(")").appendLine();

        builder.append("public Response ", indent).append(methodName).append("(");

        List<Field> params = new ArrayList<>();
        for(Field field: fields) {
            if(field.getAnnotation(CommandOption.class).referenceKey().isEmpty()) {
                params.add(field);
            }
        }
        Collections.sort(params, new FieldComparator());

        for (int i = 0; i < params.size(); i++) {
            Field field = params.get(i);
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
                .append(methodName)
                .appendLine("\", ")
                .append(arguments(fields), indent + 4)
                .append(")")
                .appendLine(")")
                .appendLine(".build();", indent + 2);
        indent--;
        builder.appendLine("}", indent).appendLine();
    }

    private String path(Class<? extends CommandCallable> cls) {
        StringBuilder builder = new StringBuilder("/").append(cls.getAnnotation(Command.class).name());

        Field[] fields = CommandParser.getOptionFields(cls);
        List<Field> list = Arrays.asList(fields);
        Collections.sort(list, new FieldComparator());

        list.forEach(e -> {
            CommandOption commandOption = e.getAnnotation(CommandOption.class);
            if(commandOption.paramType().equals(CommandOption.ParamType.PathParam)) {
                builder.append("/{").append(e.getName()).append("}");
            }
        });

        return builder.toString();
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
        Collections.sort(list, new FieldComparator());

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

    private String arguments(Field[] fields) {
        List<Field> params = new ArrayList<>();
        for(Field field: fields) {
            if(field.getAnnotation(CommandOption.class).referenceKey().isEmpty()) {
                params.add(field);
            }
        }
        Collections.sort(params, new FieldComparator());

        StringBuilder builder = new StringBuilder("new Object[]{");
        for(int i = 0; i < params.size(); i ++) {
            Field field = params.get(i);
            if(i > 0) {
                builder.append(", ");
            }

            CommandOption mapping = field.getAnnotation(CommandOption.class);
            if(mapping.dataForProcessing()) {
                builder.append("encodeMessage(").append(fields[i].getName()).append(")");
            } else {
                builder.append(field.getName());

            }
        }

        builder.append("}");
        return builder.toString();
    }

    class FieldComparator implements Comparator<Field> {

        @Override
        public int compare(Field o1, Field o2) {
            CommandOption commandOption1 = o1.getAnnotation(CommandOption.class);
            CommandOption commandOption2 = o2.getAnnotation(CommandOption.class);

            Class<?> cls1 = o1.getDeclaringClass();
            Class<?> cls2 = o2.getDeclaringClass();

            if(commandOption1.dataForProcessing() && !commandOption2.dataForProcessing()) {
                return 1;

            } else if(!commandOption1.dataForProcessing() && commandOption2.dataForProcessing()) {
                return -1;

            } else {
                int paramDiff = CommandOption.ParamType.indexOf(commandOption1.paramType()) - CommandOption.ParamType.indexOf(commandOption2.paramType());
                if(paramDiff != 0) {
                    return paramDiff;

                } else if (cls1.equals(cls2)) {
                    return o1.getName().compareTo(o2.getName());

                } else if (cls2.isAssignableFrom(cls1)) {
                    return 1;

                } else {
                    return -1;
                }

            }
        }
    }


}
