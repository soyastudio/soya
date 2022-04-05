package soya.framework.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.commons.util.CodeBuilder;

@Command(group = "bod", name = "json-types", httpMethod = Command.HttpMethod.GET)
public class JsonTypeCommand extends XmlToJsonTypeCommand {

    protected void print(String path, String type, CodeBuilder codeBuilder) {
        codeBuilder.append(type).append("(").append(path).append(");");
    }
}