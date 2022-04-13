package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.core.Resources;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Set;

@Command(group = "business-object-development", name = "esql-validation", httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class EsqlValidateCommand extends XPathMappingsCommand {

    @CommandOption(option = "c", required = true, dataForProcessing = true)
    protected String code;

    private Set<String> lines = new LinkedHashSet<>();

    @Override
    protected void annotate() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(Resources.getResourceAsString(code)));
        String line = reader.readLine();
        while (line != null) {
            String token = line.trim();
            if (token.startsWith("-- ")) {
                lines.add(token);
            }
            line = reader.readLine();
        }
    }

    @Override
    protected String render() {
        StringBuilder builder = new StringBuilder();
        mappings.entrySet().forEach(e -> {
            String path = "-- " + e.getKey();
            Mapping mapping = e.getValue();
            if (mapping.rule != null && !lines.contains(path)) {
                builder.append(e.getKey()).append("=").append(mapping.source).append("\n");
            }
        });

        return builder.toString();
    }

    protected String render2() {
        StringBuilder builder = new StringBuilder();
        lines.forEach(e -> {
            builder.append(e).append("\n");
        });

        return builder.toString();
    }
}
