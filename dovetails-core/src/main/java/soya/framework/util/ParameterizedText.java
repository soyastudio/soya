package soya.framework.util;

import java.util.*;

public class ParameterizedText {
    private static final String DEFAULT_START_TOKEN = "${";
    private static final String DEFAULT_END_TOKEN = "}";

    private final String expression;
    private final String startToken;
    private final String endToken;

    private List<String> fragments;
    private Set<String> parameters;

    private ParameterizedText(String exp, String startToken, String endToken) {
        this.expression = exp;
        this.startToken = startToken;
        this.endToken = endToken;

        fragments = new ArrayList<>();
        parameters = new LinkedHashSet<>();

        String[] arr = exp.split(endToken);
        for (String s : arr) {
            if (s.contains(startToken)) {
                int index = s.indexOf(startToken);
                String a = s.substring(0, index);
                String b = s.substring(index);
                fragments.add(a);
                fragments.add(b + endToken);

                String param = b.substring(startToken.length());
                parameters.add(param);

            } else {
                fragments.add(s);

            }
        }
    }

    public static ParameterizedText create(String expression) {
        return new ParameterizedText(expression, DEFAULT_START_TOKEN, DEFAULT_END_TOKEN);
    }

    public static ParameterizedText create(String expression, String startToken, String endToken) {
        return new ParameterizedText(expression, startToken, endToken);
    }

    public boolean hasParameters() {
        return parameters.size() > 0;
    }

    public boolean hasParameter(String name) {
        return parameters.contains(name);
    }

    public Set<String> getParameters() {
        return parameters;
    }

    public ParameterizedText evaluate(String paramName, String paramValue) {
        if (parameters.contains(paramName)) {
            List<String> list = new ArrayList<>();
            Set<String> set = new LinkedHashSet<>();
            String token = startToken + paramName + endToken;
            fragments.forEach(e -> {
                if (e.equals(token)) {
                    list.add(paramValue);
                } else {
                    list.add(e);
                    if (e.startsWith(startToken) && e.endsWith(endToken)) {
                        set.add(e);
                    }
                }
            });

            this.fragments = list;
            this.parameters = set;
        }
        return this;
    }

    public <T> ParameterizedText evaluate(T context, ParameterResolver<T> resolver) {
        List<String> list = new ArrayList<>();
        Set<String> set = new LinkedHashSet<>();

        fragments.forEach(e -> {
            if (e.startsWith(startToken) && e.endsWith(endToken)) {
                String param = e.substring(startToken.length(), e.length() - endToken.length());
                String value = resolver.evaluate(param, context);
                if (value != null) {
                    list.add(value);
                } else {
                    list.add(e);
                    set.add(param);
                }

            } else {
                list.add(e);
            }
        });

        this.fragments = list;
        this.parameters = set;

        return this;
    }

    public String toString(Properties values) {
        if (values == null) {
            return toString();
        }

        StringBuilder builder = new StringBuilder();
        fragments.forEach(e -> {
            if (e.startsWith(startToken) && e.endsWith(endToken)) {
                String propName = e.substring(startToken.length(), e.length() - endToken.length());
                String propValue = values.getProperty(propName);
                if (propValue != null) {
                    builder.append(propValue);
                } else {
                    builder.append(e);
                }
            }
            builder.append(e);
        });
        return builder.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        fragments.forEach(e -> {
            builder.append(e);
        });
        return builder.toString();
    }
}
