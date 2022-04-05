package soya.framework.albertsons.commands;

import soya.framework.commons.cli.Command;
import soya.framework.commons.util.CodeBuilder;

@Command(group = "bod", name = "xpath-construct", httpMethod = Command.HttpMethod.GET)
public class XPathConstructCommand extends ConstructCommand {

    @Override
    protected String render() {
        CodeBuilder builder = CodeBuilder.newInstance();
        mappings.entrySet().forEach(e -> {
            String construction = e.getValue().construct();
            if(!construction.isEmpty()) {
                builder.append(e.getKey()).append("=").append(construction).appendLine();
            }
        });

        return builder.toString();
    }

}
