package soya.framework.dovetails.component.context;

import java.util.*;

class PropertyDescriptor {
    private final String name;
    private List<String> fragments;
    private Set<String> parameters;

    public PropertyDescriptor(String name, String value) {
        this.name = name;

        fragments = new ArrayList<>();
        parameters = new HashSet<>();

        String[] arr = value.split("}");
        for (String s : arr) {
            if (s.contains("${")) {
                int index = s.indexOf("${");
                String a = s.substring(0, index);
                String b = s.substring(index);

                fragments.add(a);
                fragments.add(b + "}");

                parameters.add(b.substring(2));
            } else {
                fragments.add(s);
            }
        }
    }

    public PropertyDescriptor(String name, String value, Properties values) {
        this.name = name;

        fragments = new ArrayList<>();
        parameters = new HashSet<>();

        String[] arr = value.split("}");
        for (String s : arr) {
            if (s.contains("${")) {
                int index = s.indexOf("${");
                String a = s.substring(0, index);
                String b = s.substring(index);


                fragments.add(a);
                String param = b.substring(2);
                String v = values.getProperty(param);
                if(v != null) {
                    fragments.add(v);

                } else {
                    fragments.add(b + "}");
                    parameters.add(param);

                }
            } else {
                fragments.add(s);
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getFragments() {
        return fragments;
    }

    public Set<String> getParameters() {
        return parameters;
    }
}
