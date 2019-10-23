package soya.framework.dovetails.component.context;

import java.util.*;

public class PropertiesEvaluator {
    private Properties sources;
    private Properties values;

    private Map<String, PropertyDescriptor> descriptors = new HashMap<>();
    private Set<PropertyListener> listeners = new HashSet<>();

    public PropertiesEvaluator(Properties sources, Properties values) {
        this.sources = sources == null ? new Properties() : new Properties(sources);
        this.values = values == null ? new Properties() : new Properties(values);

        Enumeration<?> enumeration = sources.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = sources.getProperty(key);

            PropertyDescriptor desc = new PropertyDescriptor(key, value, values);
            descriptors.put(desc.getName(), desc);
            if(desc.getParameters().size() > 0) {
                listeners.add(new PropertyListener(desc));
            }
        }

    }

    public PropertiesEvaluator(List<PropertyDescriptor> propertyDescriptors) {


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

        public PropertyListener(PropertyDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        public void onEvent(PropertyEvent event) {
            if (descriptor.getParameters().contains(event.getPropertyName())) {
                List<String> fragments = new ArrayList<>();
                for (String frag : descriptor.getFragments()) {
                    if (frag.equals(event.getToken())) {
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
