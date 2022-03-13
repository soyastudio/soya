package soya.framework.commons.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandOption {
    String option();

    String longOption() default "";

    boolean hasArg() default true;

    boolean required() default false;

    String defaultValue() default "";

    String desc() default "";

}
