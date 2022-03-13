package soya.framework.commons.cli;

public @interface OptionMapping {
    String option();

    int parameterIndex() default -1;

    String property() default "";


}
