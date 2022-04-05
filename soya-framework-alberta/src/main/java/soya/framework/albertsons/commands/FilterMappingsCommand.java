package soya.framework.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.commons.util.CodeBuilder;

import java.util.Locale;

@Command(group = "bod", name = "filter-mapping")
public class FilterMappingsCommand extends XPathMappingsCommand {

    @CommandOption(option = "q", longOption = "query")
    protected String expression;

    @Override
    protected String render() {
        String token = expression == null? "::" : expression.toUpperCase(Locale.ROOT);
        CodeBuilder builder = CodeBuilder.newInstance();
        mappings.entrySet().forEach(e -> {
            String value = e.getValue().toString().toUpperCase(Locale.ROOT);
            if(value.contains(token)) {
                builder.append(e.getKey()).append("=").appendLine(e.getValue().toString());
            }

        });
        return builder.toString();
    }

    @Override
    protected void annotate() throws Exception {

    }
}
