package soya.framework.util;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class ClasspathUtils {
    private ClasspathUtils() {
    }

    public static Set<Class<?>> findByAnnotation(Class<? extends Annotation> annotation, String... packageName) {

        Set<Class<?>> set = new HashSet<>();
        try {
            ClassPath classpath = ClassPath.from(getClassLoader());
            for (String pkg : packageName) {
                Set<ClassPath.ClassInfo> results = classpath.getTopLevelClassesRecursive(pkg);
                results.forEach(e -> {
                    Class<?> cls = e.load();
                    if (cls.getAnnotation(annotation) != null) {
                        set.add(cls);
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return set;
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClasspathUtils.class.getClassLoader();
        }

        return classLoader;
    }
}
