package soya.framework.transform.schema.xs;

import org.apache.avro.Schema;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.transform.schema.KnowledgeTree;
import soya.framework.transform.schema.T123W;
import soya.framework.transform.schema.converter.XsdToAvsc;

public class XsToAvroSchemaRenderer implements XsKnowledgeRenderer {

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        Schema schema = XsdToAvsc.fromXmlSchema(knowledgeBase.origin());
        return schema.toString(true);
    }
}
