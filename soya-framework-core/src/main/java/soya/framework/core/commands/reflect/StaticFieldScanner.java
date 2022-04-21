package soya.framework.core.commands.reflect;

import org.reflections.Reflections;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Command(group = "reflect", name = "static-field-scanner", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class StaticFieldScanner extends ReflectCommand<String> {

    @CommandOption(option = "p", required = true)
    private String packageName;

    @Override
    public String call() throws Exception {
        Reflections reflections = new Reflections(packageName);

        Set<String> set = new HashSet<>();
        Set<String> results = reflections.getAllTypes();

        for (String e : results) {
            if (e.startsWith(packageName + ".")) {
                try {
                    Class<?> cls = Class.forName(e);
                    if(cls.getDeclaringClass() == null) {
                        Field[] fields = cls.getDeclaredFields();
                        for (Field field : fields) {
                            if(Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                                field.setAccessible(true);
                                System.out.println(cls.getName() + "." + field.getName() + "=" + field.get(null));
                            }
                        }
                    }
                } catch (Throwable ex) {
                    //ex.printStackTrace();
                }
            }

        }

        List<String> list = new ArrayList<>(set);
        Collections.sort(list);

        return GSON.toJson(list);
    }
}
