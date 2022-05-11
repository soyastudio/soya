package soya.framework.core.oas.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {

    String specification() default "2.0";

    String title();

    String description() default "";

    String termsOfService() default "";

    String contactName() default "";

    String contactEmail() default "";

    String contactUrl() default "";

    String licenseName() default "";

    String licenseUrl() default "";

    String version() default "";

    // for version 3.0
    Server[] servers() default {};




}
