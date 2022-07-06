package soya.framework.action.actions.apache.xmlbeans;

import soya.framework.action.Command;
import soya.framework.action.actions.transform.converter.XsdToAvsc;

@Command(group = "transform", name = "xmlbeans-avsc-converter", httpRequestTypes = Command.MediaType.APPLICATION_JSON)
public class AvroSchemaConverter extends XmlBeansAction {

    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        return XsdToAvsc.fromXmlSchema(tree.origin()).toString(true);
    }
}
