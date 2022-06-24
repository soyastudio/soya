package soya.framework.commandline.tasks.reflect;

import org.reflections.Reflections;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "reflect", name = "type-discover", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class TypeDiscoveryTask extends ReflectionTask<String[]> {

    @CommandOption(option = "t", required = true)
    protected String type;

    @CommandOption(option = "p")
    protected String packageName;

    @CommandOption(option = "a")
    protected boolean includeAbstract;


    @Override
    protected String[] execute() throws Exception {
        List<String> list = new ArrayList<>();
        Class<?> cls = Class.forName(type);

        Reflections reflections = packageName == null? new Reflections() : new Reflections(packageName);
        if(cls.isAnnotation()) {
            reflections.getTypesAnnotatedWith((Class<? extends Annotation>) cls).forEach(e -> {
                if(!Modifier.isAbstract(e.getModifiers()) || includeAbstract) {
                    list.add(e.getName());
                }
            });

        } else {
            reflections.getSubTypesOf(cls).forEach(e -> {
                if(!Modifier.isAbstract(e.getModifiers()) || includeAbstract) {
                    list.add(e.getName());
                }
            });
        }
        Collections.sort(list);

        return list.toArray(new String[list.size()]);
    }
}
