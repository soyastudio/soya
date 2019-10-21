package soya.framework.dovetails.component.context;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PropertyDescriptor {
    private static final String PARAM_TOKEN = "@@";
    private static final int PARAM_TOKEN_LEN = PARAM_TOKEN.length();

    private final String name;
    private final ImmutableList<String> fragments;
    private final ImmutableSet<String> parameters;

    public PropertyDescriptor(String name, String value) {
        this.name = name;

        List<String> list = new ArrayList<>();
        Set<String> set = new HashSet<>();
        String[] arr = value.split("}");
        for(String s: arr) {
            if(s.contains("${")) {
                int index = s.indexOf("${");
                String a = s.substring(0, index);
                String b = s.substring(index + 2);
                list.add(a);
                list.add(PARAM_TOKEN + b);
                set.add(b);
            }
        }

        fragments = ImmutableList.copyOf(list);
        parameters = ImmutableSet.copyOf(set);
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
