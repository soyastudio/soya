package soya.framework.commons.reflect.descriptor;

import java.lang.reflect.Type;
import java.util.Map;

public class MethodReturnDescriptor extends AnnotatableDescriptor {
    private final TypeDescriptor type;

    private MethodReturnDescriptor(TypeDescriptor type, Map<String, Object> annotations) {
        this.type = type;
        this.setAnnotations(annotations);
    }

    public TypeDescriptor getType() {
        return type;
    }

    public static MethodReturnDescriptorBuilder builder(Type type) {
        return new MethodReturnDescriptorBuilder(type);
    }

    public static class MethodReturnDescriptorBuilder extends AnnotatableDescriptorBuilder<MethodReturnDescriptorBuilder> {
        private TypeDescriptor type;

        private MethodReturnDescriptorBuilder(Type type) {
            this.type = TypeDescriptor.fromType(type);
        }

        public MethodReturnDescriptor build() {
            return new MethodReturnDescriptor(type, annotations);
        }
    }
}
