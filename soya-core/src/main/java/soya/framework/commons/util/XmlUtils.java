package soya.framework.commons.util;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {

    public static String toXmlString(Node node) {
        StringBuilder builder = new StringBuilder();
        print(node, builder);
        return builder.toString();
    }

    private static void print(Node node, StringBuilder builder) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String name = node.getNodeName();
            if (name.contains(":")) {
                name = name.substring(name.indexOf(":") + 1);
            }
            builder.append("<").append(name).append(">");

            NodeList children = node.getChildNodes();
            if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
                builder.append(node.getTextContent());
            } else {
                for (int i = 0; i < children.getLength(); i++) {
                    print(children.item(i), builder);
                }

            }

            builder.append("</").append(name).append(">");
        }
    }
}
