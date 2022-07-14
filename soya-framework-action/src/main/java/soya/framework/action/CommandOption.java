package soya.framework.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandOption {

    String option();

    boolean hasArg() default true;

    boolean required() default false;

    String desc() default "";

    char valueSeparator() default ',';

    boolean dataForProcessing() default false;

    String defaultValue() default "";

    String referenceKey() default "";

    ParamType paramType() default ParamType.HeaderParam;

    enum ParamType {
        ReferenceParam, PathParam, HeaderParam, QueryParam, BodyParam;

        public static int indexOf(ParamType value) {
            ParamType[] values = values();
            for(int i = 0; i < values.length; i ++) {
                if(values[i].equals(value)) {
                    return i;
                }
            }

            return -1;
        }
    }

}
