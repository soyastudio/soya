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
        APPLICATION_XML("application/xml"),
        APPLICATION_ATOM_XML("application/atom+xml"),
        APPLICATION_XHTML_XML("application/xhtml+xml"),
        APPLICATION_SVG_XML("application/svg+xml"),
        APPLICATION_JSON("application/json"),
        APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
        MULTIPART_FORM_DATA("multipart/form-data"),
        APPLICATION_OCTET_STREAM("application/octet-stream"),
        TEXT_PLAIN("text/plain"),
        TEXT_XML("text/xml"),
        TEXT_HTML("text/html"),
        SERVER_SENT_EVENTS("text/event-stream"),
        APPLICATION_JSON_PATCH_JSON("application/json-patch+json");

        private final String contentType;

        MediaType(java.lang.String contentType) {
            this.contentType = contentType;
        }

    }

}
