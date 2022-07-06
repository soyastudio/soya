package soya.framework.action.dispatch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionPipeline {

    String[] parameters();

    ActionTask[] tasks();

    String result() default "";
}
