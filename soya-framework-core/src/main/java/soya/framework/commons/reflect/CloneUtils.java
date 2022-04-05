package soya.framework.commons.reflect;

import soya.framework.commons.reflect.cloning.Cloner;
import soya.framework.commons.reflect.cloning.Immutable;

import java.util.HashSet;
import java.util.Set;

public class CloneUtils {
    private static Set<Class<?>> basicImmutableTypes;
    private static Cloner cloner;

    static {
        basicImmutableTypes = new HashSet<>();
        basicImmutableTypes.add(String.class);
        basicImmutableTypes.add(Boolean.class);
        basicImmutableTypes.add(Byte.class);
        basicImmutableTypes.add(Character.class);
        basicImmutableTypes.add(Double.class);
        basicImmutableTypes.add(Float.class);
        basicImmutableTypes.add(Integer.class);
        basicImmutableTypes.add(Long.class);
        basicImmutableTypes.add(Short.class);

        cloner = new Cloner();
    }

    public static boolean isImmutable(Class<?> c) {
        if(c.isPrimitive() || basicImmutableTypes.contains(c) || c.getAnnotation(Immutable.class) != null) {
            return true;
        }

        // TODO: others

        return false;
    }

    public static <T> T deepClone(T t) {
        return cloner.deepClone(t);
    }
}
