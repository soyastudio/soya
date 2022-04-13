package soya.framework.commands.transform.xmlbeans.xs;

import org.apache.avro.Schema;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.kt.KnowledgeTree;
import soya.framework.kt.T123W;
import soya.framework.commands.transform.converter.XsdToAvsc;

public class XsToAvroSchemaRenderer implements XsKnowledgeRenderer {

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        Schema schema = XsdToAvsc.fromXmlSchema(knowledgeBase.origin());
        return schema.toString(true);
    }
}
