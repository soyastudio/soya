package soya.framework.action.actions.reflect;

import soya.framework.action.Command;
import soya.framework.util.CodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Command(group = "reflect", name = "system-properties", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.TEXT_PLAIN})
public class SystemPropertiesAction extends ReflectionAction<String> {
    @Override
    protected String execute() throws Exception {
        List<String> propNames = new ArrayList<>();
        Enumeration enumeration = System.getProperties().propertyNames();
        while (enumeration.hasMoreElements()) {
            propNames.add((String) enumeration.nextElement());
        }
        Collections.sort(propNames);

        CodeBuilder builder = CodeBuilder.newInstance();
        propNames.forEach(e -> {
            builder.append(e).append("=").appendLine(System.getProperty(e));
        });

        return builder.toString();
    }
}
