package soya.framework.albertsons.commands;

import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import soya.framework.commons.cli.Command;

@Command(group = "bod", name = "sample-xml", httpMethod = Command.HttpMethod.GET)
public class SampleXmlCommand extends SchemaCommand {

    @Override
    protected String render() {
        return SampleXmlUtil.createSampleForType(tree.origin().documentTypes()[0]);
    }
}
