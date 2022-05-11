package soya.framework.tasks.apache.xmlbeans.xs;

import org.apache.xmlbeans.*;
import soya.framework.kt.KnowledgeBuildException;
import soya.framework.kt.KnowledgeTree;
import soya.framework.kt.Tree;
import soya.framework.kt.TreeNode;
import soya.framework.kt.generic.GenericKnowledgeSystem;
import soya.framework.kt.generic.GenericKnowledgeTree;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class XsKnowledgeSystem extends GenericKnowledgeSystem<SchemaTypeSystem, XsNode> {

    protected XsKnowledgeSystem(Object source, KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) {
        super(source, knowledge);
    }

    public static KnowledgeTree<SchemaTypeSystem, XsNode> create(Object source) throws KnowledgeBuildException {
        return (KnowledgeTree<SchemaTypeSystem, XsNode>) builder(source)
                .knowledgeExtractor(new XmlSchemaExtractor())
                .knowledgeDigester(new XsKnowledgeTreeDigester())
                .create()
                .getKnowledge();
    }

    public static class XmlSchemaExtractor implements KnowledgeExtractor<SchemaTypeSystem> {

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

        @Override
        public SchemaTypeSystem extract(Object source) throws IOException, KnowledgeBuildException {
            try {
                // Parse the XML Schema object first to get XML object
                XmlObject parsedSchema = parse(source, xmlOptions);

                // We may have more than schemas to validate with
                XmlObject[] schemas = new XmlObject[]{parsedSchema};

                // Compile the XML Schema to create a schema type system
                return XmlBeans.compileXsd(schemas, schemaTypeLoader, xmlOptions);

            } catch (XmlException e) {
                throw new KnowledgeBuildException(e);

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
            KnowledgeTree<SchemaTypeSystem, XsNode> tree = null;
            SchemaType sType = schemaTypeSystem.documentTypes()[0];
            if (SchemaType.ELEMENT_CONTENT == sType.getContentType()) {

                SchemaLocalElement element = (SchemaLocalElement) sType.getContentModel();
                QName qName = element.getName();

                tree = GenericKnowledgeTree.newInstance(schemaTypeSystem, qName.getLocalPart(), new XsNode(element));
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
