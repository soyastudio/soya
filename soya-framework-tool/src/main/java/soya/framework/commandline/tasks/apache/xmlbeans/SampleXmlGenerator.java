package soya.framework.commandline.tasks.apache.xmlbeans;

import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import soya.framework.commandline.Command;

@Command(group = "transform", name = "xmlbeans-sample-xml", httpRequestTypes = Command.MediaType.APPLICATION_XML)
public class SampleXmlGenerator extends XmlBeansTask {

    @Override
    protected void process() throws Exception {

    }

    @Override
    protected String render() {
        return SampleXmlUtil.createSampleForType(tree.origin().documentTypes()[0]);
    }
}
