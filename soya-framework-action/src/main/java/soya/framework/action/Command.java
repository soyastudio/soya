package soya.framework.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String group();

    String name();

    String summary() default "";

    String description() default "";

    HttpMethod httpMethod() default HttpMethod.POST;

    MediaType[] httpRequestTypes() default {MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML};

    MediaType[] httpResponseTypes() default {MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML};

    String[] tags() default {};

    enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS;
    }

    enum MediaType {
        TEXT_PLAIN, APPLICATION_JSON, APPLICATION_XML, TEXT_HTML, APPLICATION_OCTET_STREAM;
    }

}
