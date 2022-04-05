package soya.framework.albertsons.commands;

import soya.framework.commons.cli.Command;
import soya.framework.transform.schema.converter.XsdToAvsc;

@Command(group = "bod", name = "avsc", httpMethod = Command.HttpMethod.GET)
public class XsdToAvscCommand extends SchemaCommand {

    @Override
    protected String render() {
        return XsdToAvsc.fromXmlSchema(tree.origin()).toString(true);
    }
}
