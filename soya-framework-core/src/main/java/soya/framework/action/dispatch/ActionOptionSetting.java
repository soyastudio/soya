package soya.framework.action.dispatch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionOptionSetting {
    String option();

    String value() default "";

    String property() default "";

    int index() default -1;

}
