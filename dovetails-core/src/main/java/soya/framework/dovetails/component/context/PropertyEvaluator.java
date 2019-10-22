package soya.framework.dovetails.component.context;

import java.util.*;

public class PropertyEvaluator {

    private Set<PropertyListener> listeners = new HashSet<>();

    public PropertyEvaluator(List<PropertyDescriptor> propertyDescriptors) {


    }

    class PropertyEvent extends EventObject {
        private String token;
        private String value;

        public PropertyEvent(String key, String value) {
            super(key);
            this.token = "${" + key + "}";
            this.value = value;
        }

        public String getPropertyName() {
            return (String) this.getSource();
        }

        public String getPropertyValue() {
            return value;
        }

        public String getToken() {
            return token;
        }
    }

    class PropertyListener implements EventListener {
        private PropertyDescriptor descriptor;

        public void onEvent(PropertyEvent event) {
            if (descriptor.getParameters().contains(event.getPropertyName())) {
                List<String> fragments = new ArrayList<>();
                for(String frag: descriptor.getFragments()) {
                    if(frag.equals(event.getToken())) {
                        fragments.add(event.getPropertyValue());
                    } else {
                        fragments.add(frag);
                    }
                }

                //this.descriptor = new PropertyDescriptor(descriptor.getName(), fragments);
            }
        }

    }

}
