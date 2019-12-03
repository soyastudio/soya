package soya.framework.util;

import com.google.common.base.Strings;

public class EmptyUtils {
    public static String validate(String s) {
        if(Strings.isNullOrEmpty(s)) {
            throw new EmptyException();
        }
        return s;
    }

    public static String validate(String s, String name) {
        if(Strings.isNullOrEmpty(s)) {
            throw new EmptyException(name);
        }
        return s;
    }
}
