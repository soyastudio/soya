package soya.framework.commands.transform.xmlbeans;

import soya.framework.core.Command;
import soya.framework.commands.transform.converter.XsdToAvsc;

@Command(group = "transform", name = "xmlbeans-avsc-converter", httpRequestTypes = Command.MediaType.APPLICATION_JSON)
public class AvroSchemaConverter extends XmlBeansCommand {

    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        return XsdToAvsc.fromXmlSchema(tree.origin()).toString(true);
    }
}
