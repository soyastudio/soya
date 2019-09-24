package soya.framework.commons.reflect.descriptor;

import java.util.HashMap;
import java.util.Map;

public abstract class AnnotatableDescriptorBuilder<T extends AnnotatableDescriptorBuilder> {

    protected Map<String, Object> annotations = new HashMap<>();

    public T annotate(String key, Object value) {
        if(value == null) {
            annotations.remove(key);
        } else {
            annotations.put(key, value);
        }

        return (T)this;
    }

}
