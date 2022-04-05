package soya.framework.albertsons.commands;

import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandMapping;
import soya.framework.commons.cli.CommandParser;
import soya.framework.commons.util.CodeBuilder;

import java.util.Locale;

@Command(group = "bod", name = "xpath-mappings", httpMethod = Command.HttpMethod.GET)
public class XPathMappingsRenderer extends XPathMappingsCommand {

    @Override
    protected void annotate() throws Exception {
    }

    @Override
    protected String render() {

        CodeBuilder builder = CodeBuilder.newInstance();
        mappings.entrySet().forEach(e -> {
            String value = e.getValue().toString().toUpperCase(Locale.ROOT);
            builder.append(e.getKey()).append("=").appendLine(e.getValue().toString());

        });
        return builder.toString();
    }
}
