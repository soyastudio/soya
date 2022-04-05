package soya.framework.admin.configuration;

import com.google.common.base.CaseFormat;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.core.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommandExecutionConfiguration implements ApplicationContextAware {

    @Value("${workspace.home}")
    private String workspaceHome;

    @Value("${ant.work.home}")
    private String antWorkHome;

    @Bean
    ExecutorService commandExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CommandExecutionContext.builder()
                .setExecutorService(applicationContext.getBean(ExecutorService.class))
                .serviceLocator(applicationContext)
                .setProperty("workspace.home", workspaceHome)
                .setProperty("ant.work.home", antWorkHome)
                .create();
    }

    public static class DispatcherGenerator {
        private CommandExecutionContext context;
        private Set<Class<?>> classes;

        public DispatcherGenerator(CommandExecutionContext context) {
            this.context = context;
            this.classes = generate(context);
        }

        public Set<Class<?>> generated() {
            return classes;
        }

        private Set<Class<?>> generate(CommandExecutionContext context) {
            Set<Class<?>> set = new HashSet<>();
            context.groups().forEach(e -> {
                List<Class<? extends CommandCallable>> commands = new ArrayList<>();
                context.getCommands(e).forEach(c -> {
                    commands.add(context.getCommandType(c));
                });

                try {
                    set.add(create(e, commands));

                } catch (CannotCompileException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (NotFoundException ex) {
                    ex.printStackTrace();
                }
            });

            return set;
        }

        private Class<?> create(String group, List<Class<? extends CommandCallable>> commands) throws CannotCompileException, IOException, NotFoundException {
            ClassPool cp = ClassPool.getDefault();
            CtClass superClass = cp.get(CommandDispatcher.class.getName());

            CtClass cc = cp.makeClass(getDispatchClassName(group));
            cc.setSuperclass(superClass);

            ClassFile classFile = cc.getClassFile();

            AnnotationsAttribute cAttr = new AnnotationsAttribute(classFile.getConstPool(), AnnotationsAttribute.visibleTag);

            Annotation pathAnnotation = new Annotation("javax.ws.rs.Path", classFile.getConstPool());
            pathAnnotation.addMemberValue("value", new StringMemberValue("/" + group, classFile.getConstPool()));
            cAttr.addAnnotation(pathAnnotation);

            Annotation apiAnnotation = new Annotation("io.swagger.annotations.Api", classFile.getConstPool());
            apiAnnotation.addMemberValue("value", new StringMemberValue("/" + group, classFile.getConstPool()));
            cAttr.addAnnotation(apiAnnotation);

            Annotation mappingAnnotation = new Annotation(GroupMapping.class.getName(), classFile.getConstPool());
            mappingAnnotation.addMemberValue("value", new StringMemberValue("/" + group, classFile.getConstPool()));
            cAttr.addAnnotation(mappingAnnotation);

            classFile.addAttribute(cAttr);

            commands.forEach(c -> {
                CommandMethodMapping mapping = new CommandMethodMapping(c);

                try {
                    CtMethod m = CtNewMethod.make(method(mapping), cc);
                    // create the annotation
                    AnnotationsAttribute methodAttr = new AnnotationsAttribute(classFile.getConstPool(), AnnotationsAttribute.visibleTag);

                    // HttpMethod:
                    Annotation httpMethodAnnotation = new Annotation("javax.ws.rs." + mapping.getHttpMethod(), classFile.getConstPool());
                    methodAttr.addAnnotation(httpMethodAnnotation);

                    // Path
                    Annotation methodPathAnnotation = new Annotation("javax.ws.rs.Path", classFile.getConstPool());
                    methodPathAnnotation.addMemberValue("value", new StringMemberValue(mapping.getPath(), classFile.getConstPool()));
                    methodAttr.addAnnotation(methodPathAnnotation);

                    // Consumes

                    // Produces


                    m.getMethodInfo().addAttribute(methodAttr);


                    cc.addMethod(m);

                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }


            });

            cc.writeFile();

            return cc.toClass();
        }

        private String method(CommandMethodMapping mapping) {
            List<Field> arguments = mapping.getArguments();
            CodeBuilder builder = CodeBuilder.newInstance();
            int indent = 2;
            builder.append("public ", indent)
                    .append("javax.ws.rs.core.Response")
                    .append(" ").append(mapping.getMethodName()).append("(");

            for (int i = 0; i < arguments.size(); i++) {
                Field field = arguments.get(i);
                CommandOption option = field.getAnnotation(CommandOption.class);
                if (i > 0) {
                    builder.append(", ");
                }

                if (!option.dataForProcessing()) {
                /*builder.append("@").append(GET.class.getPackage().getName()).append(".")
                        .append(option.paramType().name()).append("(\"").append(field.getName()).append("\") ");*/
                }
                builder.append("String ").append(field.getName());
            }

            builder.appendLine(") throws Exception {");
            indent++;

            builder.appendLine("return ", indent)
                    .append("javax.ws.rs.core.Response")
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


            return builder.toString();
        }


/*

    private void printCommandMethod(Class<? extends CommandCallable> cls, CodeBuilder builder) {
        Command command = cls.getAnnotation(Command.class);
        RestApiGenerator.CommandMethodMapping mapping = new RestApiGenerator.CommandMethodMapping(cls);
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
*/

        private String mediaTypes(Command.MediaType[] mediaTypes) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < mediaTypes.length; i++) {
                if (i > 0) {
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
            for (int i = 0; i < list.size(); i++) {
                Field field = list.get(i);
                if (i > 0) {
                    builder.append(" ");
                }

                CommandOption mapping = field.getAnnotation(CommandOption.class);
                if (mapping.paramType().equals(CommandOption.ParamType.ReferenceParam) && !mapping.referenceKey().isEmpty()) {
                    builder.append("-" + mapping.option()).append(" {").append(mapping.referenceKey()).append("}");

                } else {
                    builder.append("-" + mapping.option()).append(" {").append(index).append("}");
                    index++;
                }
            }
            return builder.toString();
        }

        private String arguments(List<Field> fields) {
            StringBuilder builder = new StringBuilder("new Object[]{");
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (i > 0) {
                    builder.append(", ");
                }

                CommandOption mapping = field.getAnnotation(CommandOption.class);
                if (mapping.dataForProcessing()) {
                    builder.append("encodeMessage(").append(field.getName()).append(")");
                } else {
                    builder.append(field.getName());

                }
            }

            builder.append("}");
            return builder.toString();
        }

        private String getDispatchClassName(String group) {
            String className = group.replaceAll("-", "_").toLowerCase();
            return "soya.framework.dispatch." + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, className) + "DispatchController";
        }

/*

    private Class<?> generate(String group, List<Class<? extends CommandCallable>> commands) {
        AnnotationDescription pathDescription = AnnotationDescription.Builder
                .ofType(Path.class)
                .define("value", "/" + group)
                .build();
        AnnotationDescription apiDescription = AnnotationDescription.Builder
                .ofType(Api.class)
                .define("value", "/" + group)
                .build();
        AnnotationDescription groupMappingDescription = AnnotationDescription.Builder
                .ofType(GroupMapping.class)
                .define("value", group)
                .build();

        DynamicType.Builder<CommandDispatcher> builder = new ByteBuddy()
                .subclass(CommandDispatcher.class)
                .annotateType(pathDescription)
                .annotateType(apiDescription)
                .annotateType(groupMappingDescription);

        commands.forEach(c -> {
            CommandMethodMapping mapping = new CommandMethodMapping(c);
            DynamicType.Builder.MethodDefinition methodDefinition;
            List<TypeDefinition> params = new ArrayList<>();

            builder.method(ElementMatchers.named(mapping.getMethodName()))
                    .intercept(FixedValue.value("XXX"));


        });

        Class<?> type = builder
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        System.out.println(type.getName() + ": " + type.getDeclaredMethods().length);

        return type;
    }
*/
    }


    static class CommandMethodMapping {
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
                if (commandOption.paramType().equals(CommandOption.ParamType.PathParam)) {
                    pathBuilder.append("/{").append(e.getName()).append("}");
                }
            });
            this.path = pathBuilder.toString();

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
