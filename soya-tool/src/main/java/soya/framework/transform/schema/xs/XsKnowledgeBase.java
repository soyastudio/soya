package soya.framework.transform.schema.xs;

import org.apache.xmlbeans.*;
import soya.framework.transform.schema.*;
import soya.framework.transform.schema.support.MoneyTree;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class XsKnowledgeBase<T> implements KnowledgeTreeBase<T, SchemaTypeSystem, XsNode> {

    private T tao;
    private KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase;

    private XsKnowledgeBase() {

    }

    @Override
    public T tao() {
        return tao;
    }

    @Override
    public KnowledgeTree<SchemaTypeSystem, XsNode> knowledge() {
        return knowledgeBase;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> implements KnowledgeTreeBaseBuilder<T, SchemaTypeSystem, XsNode> {
        private XmlSchemaExtractor extractor = new XmlSchemaExtractor();
        private XsKnowledgeTreeDigester digester = new XsKnowledgeTreeDigester();

        public Builder<T> schemaTypeLoader(SchemaTypeLoader schemaTypeLoader) {
            extractor.schemaTypeLoader(schemaTypeLoader);
            return this;
        }

        public Builder<T> xmlOptions(XmlOptions xmlOptions) {
            extractor.xmlOptions(xmlOptions);
            return this;
        }

        public Builder<T> string(String xmlString) {
            extractor.string(xmlString);
            return this;
        }

        public Builder<T> file(File file) {
            extractor.file(file);
            return this;
        }

        public Builder<T> url(URL url) {
            extractor.url(url);
            return this;
        }

        public Builder<T> inputStream(InputStream inputStream) {
            extractor.inputStream(inputStream);
            return this;
        }

        public Builder<T> reader(Reader reader) {
            extractor.reader(reader);
            return this;
        }

        @Override
        public XsKnowledgeBase<T> create() throws T123W.FlowBuilderException {
            XsKnowledgeBase<T> baseline = new XsKnowledgeBase<>();
            baseline.knowledgeBase = digester.digester(extractor.extract());
            return baseline;
        }

        @Override
        public KnowledgeTreeBaseBuilder<T, SchemaTypeSystem, XsNode> knowledgeExtractor(KnowledgeExtractor<SchemaTypeSystem> knowledgeExtractor) {
            return null;
        }

        @Override
        public KnowledgeTreeBaseBuilder<T, SchemaTypeSystem, XsNode> knowledgeDigester(KnowledgeDigester<SchemaTypeSystem, XsNode> digester) {
            return null;
        }
    }

    public static class XmlSchemaExtractor implements KnowledgeExtractor<SchemaTypeSystem> {
        private Object source;
        private SchemaTypeLoader schemaTypeLoader;
        private XmlOptions xmlOptions = new XmlOptions()
                .setErrorListener(null).setCompileDownloadUrls()
                .setCompileNoPvrRule();

        public XmlSchemaExtractor schemaTypeLoader(SchemaTypeLoader schemaTypeLoader) {
            this.schemaTypeLoader = schemaTypeLoader;
            return this;
        }

        public XmlSchemaExtractor xmlOptions(XmlOptions xmlOptions) {
            if (xmlOptions != null) {
                this.xmlOptions = xmlOptions;
            }
            return this;
        }

        public XmlSchemaExtractor string(String xmlString) {
            this.source = xmlString;
            return this;
        }

        public XmlSchemaExtractor file(File file) {
            this.source = file;
            return this;
        }

        public XmlSchemaExtractor url(URL url) {
            this.source = url;
            return this;
        }

        public XmlSchemaExtractor inputStream(InputStream inputStream) {
            this.source = inputStream;
            return this;
        }

        public XmlSchemaExtractor reader(Reader reader) {
            this.source = reader;
            return this;
        }

        public XmlSchemaExtractor xmlStreamReader(XMLStreamReader xmlStreamReader) {
            this.source = xmlStreamReader;
            return this;
        }

        @Override
        public KnowledgeExtractor<SchemaTypeSystem> source(Object source) {
            this.source = source;
            return this;
        }

        @Override
        public SchemaTypeSystem extract() throws T123W.FlowBuilderException {
            try {
                // Parse the XML Schema object first to get XML object
                XmlObject parsedSchema = parse(source, xmlOptions);

                // We may have more than schemas to validate with
                XmlObject[] schemas = new XmlObject[]{parsedSchema};

                // Compile the XML Schema to create a schema type system
                return XmlBeans.compileXsd(schemas, schemaTypeLoader, xmlOptions);

            } catch (XmlException | IOException e) {
                throw new T123W.FlowBuilderException(e);

            }
        }

        private XmlObject parse(Object source, XmlOptions xmlOptions) throws XmlException, IOException {
            if (source == null) {
                throw new IllegalStateException("Source is not set.");

            } else if (source instanceof String) {
                return XmlObject.Factory.parse((String) source, xmlOptions);

            } else if (source instanceof File) {
                return XmlObject.Factory.parse((File) source, xmlOptions);

            } else if (source instanceof URL) {
                return XmlObject.Factory.parse((URL) source, xmlOptions);

            } else if (source instanceof XMLStreamReader) {
                return XmlObject.Factory.parse((XMLStreamReader) source, xmlOptions);

            } else if (source instanceof InputStream) {
                return XmlObject.Factory.parse((InputStream) source, xmlOptions);

            } else if (source instanceof Reader) {
                return XmlObject.Factory.parse((Reader) source, xmlOptions);

            } else {
                throw new IllegalArgumentException("Source type is not supported.");

            }
        }
    }

    public static class XsKnowledgeTreeDigester implements KnowledgeDigester<SchemaTypeSystem, XsNode> {

        @Override
        public KnowledgeTree<SchemaTypeSystem, XsNode> digester(SchemaTypeSystem schemaTypeSystem) {
            MoneyTree<SchemaTypeSystem, XsNode> tree = null;
            SchemaType sType = schemaTypeSystem.documentTypes()[0];
            if (SchemaType.ELEMENT_CONTENT == sType.getContentType()) {

                SchemaLocalElement element = (SchemaLocalElement) sType.getContentModel();
                QName qName = element.getName();

                tree = MoneyTree.newInstance(schemaTypeSystem, qName.getLocalPart(), new XsNode(element));
                processParticle(element.getType().getContentModel(), true, tree.root(), tree);
            }

            return tree;
        }

        private void processParticle(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            switch (sp.getParticleType()) {
                case (SchemaParticle.ELEMENT):
                    processElement(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.SEQUENCE):
                    processSequence(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.CHOICE):
                    processChoice(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.ALL):
                    processAll(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.WILDCARD):
                    processWildCard(sp, mixed, parent, tree);
                    break;

                default:
                    // throw new Exception("No Match on Schema Particle Type: " + String.valueOf(sp.getParticleType()));
            }
        }

        private void processElement(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            SchemaLocalElement element = (SchemaLocalElement) sp;
            TreeNode treeNode = tree.create(parent, element.getName().getLocalPart(), new XsNode(element));
            SchemaProperty[] attrProps = sp.getType().getAttributeProperties();
            if (attrProps != null) {
                for (SchemaProperty property : attrProps) {
                    tree.create(treeNode, "@" + property.getName().getLocalPart(), new XsNode(property));
                }
            }

            if (element.getType().isSimpleType()) {
                // end
                if (!element.getType().isBuiltinType()) {
                    // System.out.println("===== simple " + element.getName() + ": " + element.getType());
                }
            } else if (element.getType().getContentModel() != null) {
                // next
                processParticle(element.getType().getContentModel(), mixed, treeNode, tree);

            } else {
                if (element.getType().getBaseType() != null) {
                    //System.out.println("================== ??? " + element.getName() + ": " + element.getType().getBaseType().isSimpleType());

                }
            }
        }

        private void processSequence(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            SchemaParticle[] spc = sp.getParticleChildren();
            for (int i = 0; i < spc.length; i++) {
                processParticle(spc[i], mixed, parent, tree);
            }
        }

        private void processChoice(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            //System.out.println(sp.getName());

        }

        private void processAll(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            //System.out.println(sp.getName());
        }

        private void processWildCard(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            //System.out.println(sp.getName());
        }
    }

}
