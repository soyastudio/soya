package soya.framework.document.xmlbeans;

import soya.framework.core.Command;
import soya.framework.transform.schema.converter.XsdToAvsc;

@Command(group = "document", name = "xmlbeans-avsc-converter", httpRequestTypes = Command.MediaType.APPLICATION_JSON)
public class AvroSchemaConverter extends XmlBeansCommand {

    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        return XsdToAvsc.fromXmlSchema(tree.origin()).toString(true);
    }
}
