package soya.framework.dispatch.swagger;

import com.google.common.base.CaseFormat;
import soya.framework.commons.cli.*;
import soya.framework.dispatch.swagger.parameters.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SwaggerBuilder {

    public static Swagger create(CommandExecutionContext context, String basePath) {
        Swagger swagger = new Swagger();

        swagger.info(new Info()
                .title("SOYA Dispatch Services")
                .description("SOYA Dispatch Services")
                .contact(new Contact().name("Qun Wen").email("wenqun.soya@gmail.com")));

        swagger.basePath(basePath);

        swagger.scheme(Scheme.HTTP)
                .scheme(Scheme.HTTPS);

        context.groups().forEach(g -> {
            load(g, context, swagger);
        });

        return swagger;

    }

    private static void load(String group, CommandExecutionContext context, Swagger swagger) {
        swagger.tag(new Tag().name(group));

        String groupPath = "/" + group;
        List<String> uris = context.getCommands(group);
        uris.forEach(e -> {
            CommandOperationMapping mapping = new CommandOperationMapping(context.getCommandType(e));
            String key = groupPath + mapping.getPath();
            Path path = swagger.getPath(key);
            if (path == null) {
                path = new Path();
                swagger.path(key, path);
            }

            Operation operation = new Operation();
            operation.tag(group);
            operation.operationId(mapping.getCommand());

            mapping.consumeTypes.forEach(ct -> {
                operation.addConsumes(ct);
            });

            mapping.produceTypes.forEach(pt -> {
                operation.addProduces(pt);
            });

            mapping.getArguments().forEach(arg -> {
                CommandOption option = arg.getAnnotation(CommandOption.class);
                if (option.dataForProcessing()) {
                    BodyParameter bodyParameter = new BodyParameter();
                    bodyParameter.name("body");
                    operation.addParameter(bodyParameter);

                } else if (!option.paramType().equals(CommandOption.ParamType.ReferenceParam)) {
                    operation.parameter(new GenericParameter(arg));
                }

            });

            operation.defaultResponse(new Response().description("successful operation"));

            path.set(mapping.httpMethod.toLowerCase(), operation);
        });

    }

    static class GenericParameter implements Parameter {
        private String in;
        private String access;
        private String name;
        private String type = "string";
        private String description;
        private boolean required;
        private String pattern;
        private Boolean readOnly;

        public GenericParameter(Field field) {
            this.name = field.getName();


            CommandOption option = field.getAnnotation(CommandOption.class);
            CommandOption.ParamType paramType = option.paramType();

            if (CommandOption.ParamType.PathParam.equals(paramType)) {
                setIn("path");
                setRequired(true);

                PathParameter pathParameter = new PathParameter();
                pathParameter.setRequired(true);
                pathParameter.setName(field.getName());
                pathParameter.setType("string");

            } else if (CommandOption.ParamType.HeaderParam.equals(paramType)) {

                setIn("header");

                HeaderParameter headerParameter = new HeaderParameter();
                headerParameter.setName(field.getName());
                headerParameter.setType("string");

            } else if (CommandOption.ParamType.QueryParam.equals(paramType)) {
                this.in = "query";

                QueryParameter queryParameter;
            }
        }


        @Override
        public String getIn() {
            return in;
        }

        @Override
        public void setIn(String in) {
            this.in = in;
        }

        @Override
        public String getAccess() {
            return access;
        }

        @Override
        public void setAccess(String access) {
            this.access = access;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public boolean getRequired() {
            return required;
        }

        @Override
        public void setRequired(boolean required) {
            this.required = required;
        }

        @Override
        public String getPattern() {
            return pattern;
        }

        @Override
        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public Map<String, Object> getVendorExtensions() {
            return null;
        }

        @Override
        public Boolean isReadOnly() {
            return null;
        }

        @Override
        public void setReadOnly(Boolean readOnly) {
            this.readOnly = readOnly;
        }
    }

    static class CommandOperationMapping {
        private Class<?> cls;

        private String uri;
        private String command;
        private List<Field> fields;

        private String httpMethod;
        private String path;

        private List<String> consumeTypes = new ArrayList<>();
        private List<String> produceTypes = new ArrayList<>();

        private String template;
        private String methodName;

        private List<Field> arguments;

        CommandOperationMapping(Class<? extends CommandCallable> cls) {
            this.cls = cls;
            Command command = cls.getAnnotation(Command.class);

            this.uri = command.group() + "://" + command.name();
            this.command = command.name();
            this.fields = Arrays.asList(CommandParser.getOptionFields(cls));

            this.httpMethod = command.httpMethod().name();

            StringBuilder pathBuilder = new StringBuilder("/").append(cls.getAnnotation(Command.class).name());
            fields.forEach(e -> {
                CommandOption commandOption = e.getAnnotation(CommandOption.class);
                if (commandOption.paramType().equals(CommandOption.ParamType.PathParam)) {
                    pathBuilder.append("/{").append(e.getName()).append("}");
                }
            });
            this.path = pathBuilder.toString();

            for (Command.MediaType ct : command.httpRequestTypes()) {
                consumeTypes.add(toString(ct));
            }

            for (Command.MediaType pt : command.httpResponseTypes()) {
                produceTypes.add(toString(pt));
            }

            StringBuilder templateBuilder = new StringBuilder();
            int index = 0;
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (i > 0) {
                    templateBuilder.append(" ");
                }

                CommandOption mapping = field.getAnnotation(CommandOption.class);
                if (mapping.paramType().equals(CommandOption.ParamType.ReferenceParam) && !mapping.referenceKey().isEmpty()) {
                    templateBuilder.append("-" + mapping.option()).append(" {").append(mapping.referenceKey()).append("}");

                } else {
                    templateBuilder.append("-" + mapping.option()).append(" {").append(index).append("}");
                    index++;
                }
            }
            this.template = templateBuilder.toString();

            methodName = command.name().replaceAll("-", "_");
            methodName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, methodName);

            this.arguments = new ArrayList<>();
            fields.forEach(e -> {
                CommandOption option = e.getAnnotation(CommandOption.class);
                if (option.referenceKey().isEmpty()) {
                    arguments.add(e);
                }
            });

        }

        public String getUri() {
            return uri;
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

        public List<String> getConsumeTypes() {
            return consumeTypes;
        }

        public List<String> getProduceTypes() {
            return produceTypes;
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

        private String toString(Command.MediaType mediaType) {
            switch (mediaType) {
                case HTML:
                    return "text/html";

                case APPLICATION_JSON:
                    return "application/json";

                case APPLICATION_XML:
                    return "application/xml";

                default:
                    return "text/plain";
            }
        }
    }
}
