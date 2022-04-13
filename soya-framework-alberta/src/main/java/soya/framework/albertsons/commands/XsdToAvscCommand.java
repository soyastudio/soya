package soya.framework.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.commands.transform.converter.XsdToAvsc;

@Command(group = "bod", name = "avsc", httpMethod = Command.HttpMethod.GET)
public class XsdToAvscCommand extends SchemaCommand {

    @Override
    protected String render() {
        return XsdToAvsc.fromXmlSchema(tree.origin()).toString(true);
    }
}
