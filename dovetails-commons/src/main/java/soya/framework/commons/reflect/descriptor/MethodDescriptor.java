package soya.framework.commons.reflect.descriptor;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodDescriptor extends AnnotatableDescriptor {

    private final transient Method method;

    private final String id;
    private final ImmutableList<MethodParameterDescriptor> parameters;
    private final MethodReturnDescriptor returnType;

    private MethodDescriptor(Method method, String id, ImmutableList<MethodParameterDescriptor> parameters, MethodReturnDescriptor returnType, Map<String, Object> annotations) {
        this.method = method;
        this.id = id;
        this.parameters = parameters;
        this.returnType = returnType;
        setAnnotations(annotations);
    }

    public Method getMethod() {
        return method;
    }

    public List<MethodParameterDescriptor> getParameters() {
        return parameters;
    }

    public MethodReturnDescriptor getReturnType() {
        return returnType;
    }

    public String[] getParameterNames() {
        String[] names = new String[parameters.size()];
        for(int i = 0; i < names.length; i ++) {
            names[i] = parameters.get(i).getName();
        }
        return names;
    }

    public static MethodDescriptorBuilder builder(Method method) {
        return new MethodDescriptorBuilder(method);
    }

    public static class MethodDescriptorBuilder extends AnnotatableDescriptorBuilder<MethodDescriptorBuilder> {
        private transient Method method;

        private String id;
        private List<MethodParameterDescriptor.MethodParameterBuilder> parameterBuilders;
        private MethodReturnDescriptor.MethodReturnDescriptorBuilder returnDescriptorBuilder;

        private MethodDescriptorBuilder(Method method) {
            this.method = method;
            this.id = method.getDeclaringClass().getName() + "." + method.getName();
            parameterBuilders = new ArrayList<>();
            for(Parameter parameter : method.getParameters()) {
                MethodParameterDescriptor.MethodParameterBuilder pb = MethodParameterDescriptor.builder(parameter);
                parameterBuilders.add(pb);
            }

            returnDescriptorBuilder = MethodReturnDescriptor.builder(method.getGenericReturnType());
        }

        public MethodDescriptorBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MethodDescriptorBuilder setParameterName(int index, String name) {
            parameterBuilders.get(index).name(name);
            return this;
        }

        public MethodDescriptorBuilder setParameterRequired(int index, boolean required) {
            parameterBuilders.get(index).required(required);
            return this;
        }

        public MethodDescriptorBuilder annotateParameter(int index, String key, Object value) {
            parameterBuilders.get(index).annotate(key, value);
            return this;
        }

        public MethodDescriptorBuilder annotateReturn(String key, Object value) {
            returnDescriptorBuilder.annotate(key, value);
            return this;
        }

        public MethodDescriptor build() {
            ImmutableList.Builder<MethodParameterDescriptor> builder = ImmutableList.<MethodParameterDescriptor>builder();
            parameterBuilders.forEach(e -> {
                builder.add(e.build());
            });
            return new MethodDescriptor(method, id, builder.build(), returnDescriptorBuilder.build(), annotations);
        }
    }
}
