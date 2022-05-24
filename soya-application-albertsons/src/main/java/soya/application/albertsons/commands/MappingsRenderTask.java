package soya.application.albertsons.commands;

import soya.framework.util.CodeBuilder;
import soya.framework.core.Command;

import java.util.Locale;

@Command(group = "business-object-development", name = "mappings", httpMethod = Command.HttpMethod.GET)
public class MappingsRenderTask extends XPathMappingsTask {

    @Override
    protected String render() throws Exception {
        CodeBuilder builder = CodeBuilder.newInstance();
        mappings.entrySet().forEach(e -> {
            String value = e.getValue().toString().toUpperCase(Locale.ROOT);
            builder.append(e.getKey()).append("=").appendLine(e.getValue().toString());

        });
        return builder.toString();
    }
}
