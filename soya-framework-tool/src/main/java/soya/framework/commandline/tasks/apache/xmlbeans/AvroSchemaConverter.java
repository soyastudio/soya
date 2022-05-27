package soya.framework.commandline.tasks.apache.xmlbeans;

import soya.framework.commandline.Command;
import soya.framework.commandline.tasks.transform.converter.XsdToAvsc;

@Command(group = "transform", name = "xmlbeans-avsc-converter", httpRequestTypes = Command.MediaType.APPLICATION_JSON)
public class AvroSchemaConverter extends XmlBeansTask {

    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        return XsdToAvsc.fromXmlSchema(tree.origin()).toString(true);
    }
}
