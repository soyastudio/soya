package soya.framework.dovetails.component.log;

import soya.framework.DataObject;

import java.util.ArrayList;
import java.util.List;

public class LoggingMessages implements DataObject {
    public static String ATTR_NAME = "LOGGINGS";

    private List<String> messages = new ArrayList<>();

    @Override
    public String getAsString() {
        StringBuilder builder = new StringBuilder();
        messages.forEach(m -> {
            builder.append(m).append("\n");
        });
        return builder.toString();
    }
}
