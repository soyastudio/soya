package soya.framework.erm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    String name();

    EntityType entityType();

    String description() default "";

    enum EntityType {
        ROOT, REFERENCE, DEPENDENT, MAPPING
    }

}
