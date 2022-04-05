package soya.framework.transform.schema.converter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import soya.framework.transform.schema.avro.AvroUtils;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class XmlToAvro {
    public static final String ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

    public static void main(String[] args) throws Exception {

        File avsc = new File("C:\\github\\Workshop\\AppBuild\\CMM\\avsc\\GetAirMilePoints.avsc");
        File xml = new File("C:\\github\\Workshop\\AppBuild\\BusinessObjects\\AirMilePoints\\test\\GetAirMilePoints.xml");

        File avro = new File("C:\\github\\Workshop\\AppBuild\\BusinessObjects\\AirMilePoints\\GetAirMilePoints.avro");

        Schema schema = new Schema.Parser().parse(new FileInputStream(avsc));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // parse XML file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(xml);
        document.getDocumentElement().normalize();

        //Here comes the root node
        Element root = document.getDocumentElement();

        //System.out.println(XmlToAvroConverter.toXmlString(root));

        GenericData.Record record =  createRecord(schema, root);

        byte[] out = toAvroBinary(root, schema);

        GenericRecord result = AvroUtils.read(out, schema);
        System.out.println(result.toString());

    }

    public static GenericData.Record convert(Node node, Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), create(e.name(), e.schema(), node));
        });

        return record;
    }

    public static byte[] toAvroJson(Node xml, Schema schema) throws IOException, ParserConfigurationException, SAXException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        AvroUtils.writeAsJson(createRecord(schema, xml), schema, outputStream);

        return outputStream.toByteArray();
    }

    public static byte[] toAvroBinary(Node xml, Schema schema) throws IOException, ParserConfigurationException, SAXException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        AvroUtils.writeAsBinary(createRecord(schema, xml), schema, outputStream);

        return outputStream.toByteArray();
    }

    public static GenericData.Record createRecord(Schema schema, Node node) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), create(e.name(), e.schema(), node));
        });

        return record;
    }

    private static GenericData.Record createEmptyRecord(Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), getDefaultValue(e.schema()));
        });
        return record;
    }

    private static List<Node> getChildrenByName(Node node, String name) {
        List<Node> list = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            String nodeName = child.getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(":") + 1);
            }

            if (name.equals(nodeName)) {
                list.add(child);
            }
        }
        return list;
    }

    private static Object create(String name, Schema schema, Node node) {

        Schema.Type type = schema.getType();
        Element element = (Element) node;

        if (element.getAttribute(name) != null && !element.getAttribute(name).isEmpty()) {
            return element.getAttribute(name);

        } else if (element.getAttribute(name) != null && !element.getAttribute(name).isEmpty()) {
            return element.getAttribute(name);

        } else if (type.equals(Schema.Type.ARRAY)) {
            return createArray(name, schema, node);

        } else if (type.equals(Schema.Type.RECORD) || type.equals(Schema.Type.MAP)) {
            List<Node> children = getChildrenByName(node, name);
            if (children.size() == 1 && children.get(0).getNodeType() == Node.ELEMENT_NODE) {
                return createRecord(schema, children.get(0));

            } else {
                return new GenericData.Record(schema);
            }

        } else if (type.equals(Schema.Type.UNION)) {
            return generateUnion(name, schema, node);

        } else {
            String value = null;
            List<Node> children = getChildrenByName(node, name);
            if (children.size() == 1) {
                value = children.get(0).getTextContent();
            }

            return convert(value, schema);
        }
    }

    private static Object convert(String value, Schema schema) {
        if (value == null) {
            return getDefaultValue(schema);
        }

        switch (schema.getType()) {
            case BOOLEAN:
                return Boolean.parseBoolean(value);

            case BYTES:
                return value.getBytes();

            case DOUBLE:
                return Double.parseDouble(value);

            case FLOAT:
                return Float.parseFloat(value);

            case INT:
                return Integer.parseInt(value);

            case LONG:
                return Long.parseLong(value);

            case NULL:
                return null;

            case STRING:
                return value;

            case ENUM:
                return generateEnumSymbol(value, schema);

            case FIXED:
                return generateFixed(value, schema);

            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Object generateUnion(String name, Schema type, Node node) {
        List<Node> children = getChildrenByName(node, name);
        if (children.size() > 0) {
            for (Node n : children) {
                List<Schema> schemas = type.getTypes();
                for (Schema sc : schemas) {
                    if (!Schema.Type.NULL.equals(sc.getType())) {
                        return create(name, sc, node);
                    }
                }
            }
        }

        return null;
    }

    private static Object createArray(String name, Schema schema, Node node) {

        Collection<Object> result = new ArrayList<>();
        List<Node> children = getChildrenByName(node, name);
        children.forEach(e -> {
            Schema elementType = schema.getElementType();
            if (Schema.Type.RECORD.equals(elementType.getType())) {
                result.add(createRecord(elementType, e));

            } else {
                result.add(convert(e.getTextContent(), elementType));

            }
        });

        return result;
    }

    private static Object generateFixed(String value, Schema schema) {
        if (value == null) {
            return getDefaultFixedValue(schema);

        } else {
            return value;

        }
    }

    private static Object generateEnumSymbol(String value, Schema schema) {
        List<String> values = schema.getEnumSymbols();
        for (String e : values) {
            if (e.equals(value)) {
                return e;
            }
        }

        throw new IllegalArgumentException("No value for enum type: " + value);
    }

    private static Object getDefaultValue(Schema schema) {
        switch (schema.getType()) {
            case BOOLEAN:
                return Boolean.FALSE;

            case BYTES:
                return "".getBytes();

            case DOUBLE:
                return 0.0;

            case FLOAT:
                return 0.0f;

            case INT:
                return 0;

            case LONG:
                return 0L;

            case NULL:
                return null;

            case STRING:
                return "";

            case RECORD:
            case MAP:
                return createEmptyRecord(schema);

            case ARRAY:
                return new ArrayList<>();

            case UNION:
                return getDefaultUnionValue(schema);

            case ENUM:
                return getDefaultEnumSymbol(schema);

            case FIXED:
                return getDefaultFixedValue(schema);

            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Object getDefaultUnionValue(Schema schema) {
        Object value = null;
        List<Schema> list = schema.getTypes();
        for (Schema sc : list) {
            if (Schema.Type.NULL.equals(sc.getType())) {
                return null;

            } else if (value == null) {
                value = getDefaultValue(schema);
            }
        }

        return value;
    }

    private static Object getDefaultEnumSymbol(Schema schema) {
        List<String> values = schema.getEnumSymbols();
        if (values.size() > 0) {
            return values.get(0);

        } else {
            return null;

        }
    }

    private static Object getDefaultFixedValue(Schema schema) {
        StringBuilder builder = new StringBuilder();
        int len = schema.getFixedSize();
        char[] arr = ALPHABET.toCharArray();
        for (int i = 0; i < len; i++) {
            int random = ThreadLocalRandom.current().nextInt();
            if (random < 0) {
                random = -1 * random;
            }
            int index = random % 52;
            builder.append(arr[index]);
        }
        return builder.toString();
    }

    private static void printNode(Node node) {
        if (node.getChildNodes().getLength() == 1 && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
            System.out.println(node.getNodeName() + ": " + node.getTextContent());

        } else {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(node.getNodeName() );
                NodeList nodeList = node.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nd = nodeList.item(i);
                    if (nd.getTextContent() != null) {
                        printNode(nodeList.item(i));
                    }
                }

            }
        }

    }

    public static GenericData.Record sampleRecord(Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        return record;
    }
}
