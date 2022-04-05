package soya.framework.transform.schema.xs;

import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;

import javax.xml.namespace.QName;
import java.math.BigInteger;

public class XsNode {
    private transient SchemaType schemaType;

    private QName name;
    private XsNodeType nodeType;

    private BigInteger minOccurs;
    private BigInteger maxOccurs;

    XsNode(SchemaField schemaField) {
        this.schemaType = schemaField.getType();

        this.name = schemaField.getName();
        this.minOccurs = schemaField.getMinOccurs();
        this.maxOccurs = schemaField.getMaxOccurs();

        if (schemaField.getType().isSimpleType()) {
            nodeType = XsNodeType.Field;

        } else if (schemaType.getBaseType() != null && schemaType.getBaseType().isSimpleType()) {
            nodeType = XsNodeType.Field;

        } else {
            nodeType = XsNodeType.Folder;
            if (schemaType.getContentModel() != null
                    && SchemaParticle.SEQUENCE == schemaType.getContentModel().getParticleType()
                    && schemaType.getContentModel().getMaxOccurs() == null) {

                this.maxOccurs = null;
            }

        }
    }

    XsNode(SchemaProperty schemaProperty) {
        this.schemaType = schemaProperty.getType();
        this.nodeType = XsNodeType.Attribute;

        this.name = schemaProperty.getName();
        this.minOccurs = schemaProperty.getMinOccurs();
        this.maxOccurs = schemaProperty.getMaxOccurs();

    }

    public SchemaType getSchemaType() {
        return schemaType;
    }

    public QName getName() {
        return name;
    }

    public XsNodeType getNodeType() {
        return nodeType;
    }

    public BigInteger getMinOccurs() {
        return minOccurs;
    }

    public BigInteger getMaxOccurs() {
        return maxOccurs;
    }

    public enum XsNodeType {
        Folder, Field, Attribute
    }
}
