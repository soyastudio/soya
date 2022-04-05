package soya.framework.transform.schema.xs;

import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import soya.framework.transform.schema.KnowledgeTree;
import soya.framework.transform.schema.T123W;

public class SampleXmlRenderer implements XsKnowledgeRenderer {
    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        return SampleXmlUtil.createSampleForType(knowledgeBase.origin().documentTypes()[0]);
    }
}
