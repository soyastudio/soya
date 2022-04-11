package soya.framework.document.xmlbeans;

import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import soya.framework.core.Command;

@Command(group = "document", name = "xmlbeans-sample-xml", httpRequestTypes = Command.MediaType.APPLICATION_XML)
public class SampleXmlGenerator extends XmlBeansCommand {

    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        return SampleXmlUtil.createSampleForType(tree.origin().documentTypes()[0]);
    }
}
