package soya.framework.commons.reflect.descriptor;

import java.lang.reflect.Parameter;
import java.util.Map;

public final class MethodParameterDescriptor extends AnnotatableDescriptor {

    private final TypeDescriptor type;
    private final String name;
    private final Boolean required;

    private MethodParameterDescriptor(TypeDescriptor type, String name, boolean required, Map<String, Object> annotations) {
        this.type = type;
        this.name = name;
        this.required = required ? Boolean.TRUE : null;

        setAnnotations(annotations);
    }

    public TypeDescriptor getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Boolean getRequired() {
        return required;
    }

    public static MethodParameterBuilder builder(Parameter parameter) {
        return new MethodParameterBuilder(parameter);
    }

    public static class MethodParameterBuilder extends AnnotatableDescriptorBuilder<MethodParameterBuilder> {
        private TypeDescriptor type;

        private String name;
        private boolean required;

        private MethodParameterBuilder(Parameter parameter) {
            if (parameter.getParameterizedType() != null) {
                type = TypeDescriptor.fromType(parameter.getParameterizedType());
            } else {
                type = TypeDescriptor.fromType(parameter.getType());
            }

            this.name = parameter.getName();
        }

        public MethodParameterBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MethodParameterBuilder required(boolean required) {
            this.required = required;
            return this;
        }

        public MethodParameterDescriptor build() {
            return new MethodParameterDescriptor(type, name, required, annotations);
        }
    }

}
