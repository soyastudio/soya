package soya.application.albertsons.commands;

import soya.framework.util.CodeBuilder;
import soya.framework.commandline.Command;

@Command(group = "business-object-development", name = "deprecation",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class DeprecatedMappingsTask extends XPathMappingsTask {

    @Override
    protected String render() {
        CodeBuilder builder = CodeBuilder.newInstance();
        mappings.entrySet().forEach(e -> {
            String key = e.getKey();
            if (key.startsWith("#")) {
                builder.appendLine(key.substring(1).trim());
            }
        });

        return builder.toString();
    }

    @Override
    protected void annotate() throws Exception {

    }
}
