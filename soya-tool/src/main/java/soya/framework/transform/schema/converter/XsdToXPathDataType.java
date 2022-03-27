package soya.framework.transform.schema.converter;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.transform.schema.KnowledgeTree;
import soya.framework.transform.schema.xs.XmlBeansUtils;
import soya.framework.transform.schema.xs.XsKnowledgeBase;
import soya.framework.transform.schema.xs.XsNode;

import java.io.File;
import java.util.Iterator;

public class XsdToXPathDataType {
    public static void main(String[] args) {
        File xsd = new File("C:\\Users\\qwen002\\IBM\\IIBT10\\workspace\\APPDEV_ESED1_SRC_TRUNK\\esed1_src\\CMM_dev\\BOD\\GetAirMilePoints.xsd");
        System.out.println(convert(xsd));
    }

    public static String convert(File xsd) {
        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(xsd)
                .create().knowledge();

        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = knowledgeTree.paths();
        while (iterator.hasNext()) {
            String path = iterator.next();
            XsNode node = knowledgeTree.get(path).origin();
            builder.append(path).append("=type(");
            if (XsNode.XsNodeType.Folder.equals(node.getNodeType())) {
                builder.append("complex").append(")");

            } else if (XsNode.XsNodeType.Attribute.equals(node.getNodeType())) {
                builder.append(getXsType(node.getSchemaType())).append(")");

            } else {
                builder.append(getXsType(node.getSchemaType())).append(")");

            }

            builder.append("::").append("cardinality(").append(node.getMinOccurs()).append("-");
            if (node.getMaxOccurs() != null) {
                builder.append(node.getMaxOccurs());
            } else {
                builder.append("n");
            }

            builder.append(")").append("\n");
        }

        return builder.toString();
    }

    private static String getXsType(SchemaType schemaType) {
        SchemaType base = schemaType;
        while (base != null && !base.isSimpleType()) {
            base = base.getBaseType();
        }

        if (base == null || XmlBeansUtils.getXMLBuildInType(base) == null) {
            return "string";

        } else {
            XmlBeansUtils.XMLBuildInType buildInType = XmlBeansUtils.getXMLBuildInType(base);
            String type = buildInType.getName();
            if (type.startsWith("xs:")) {
                type = type.substring(3);
            }

            return type;
        }
    }

    private static String getSimpleType(SchemaType schemaType) {
        SchemaType base = schemaType;
        while (base != null && !base.isSimpleType()) {
            base = base.getBaseType();
        }

        if (base == null || XmlBeansUtils.getXMLBuildInType(base) == null) {
            return "string";

        } else {
            XmlBeansUtils.XMLBuildInType buildInType = XmlBeansUtils.getXMLBuildInType(base);
            String type = buildInType.getName();
            if (type.startsWith("xs:")) {
                type = type.substring(3);
            }

            switch (type) {
                case "normalizedString":
                case "date":
                case "dateTime":
                case "time":
                    return "string";

                default:
                    return type;
            }
        }
    }
}
