package soya.framework.tasks.transform.converter;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.xmlbeans.*;
import soya.framework.tasks.apache.xmlbeans.xs.XmlBeansUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class XsdToAvsc {

    public static final String DEFAULT_NAMESPACE = "com.albertsons.esed.cmm";

    private XsdToAvsc() {
    }

    public static void main(String[] args) {
        File xsd = new File("C:\\Users\\qwen002\\IBM\\IIBT10\\workspace\\APPDEV_ESED1_SRC_TRUNK\\esed1_src\\CMM_dev\\BOD\\GetAirMilePoints.xsd");
        System.out.println(fromXmlSchema(xsd).toString(true));
    }

    public static Schema fromXmlSchema(File xsd) {
        try {
            return fromXmlSchema(XmlBeansUtils.getSchemaTypeSystem(xsd));
        } catch (XmlException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Schema fromXmlSchema(SchemaTypeSystem sts) {
        SchemaType schemaType = sts.globalTypes()[0];
        SchemaBuilder.FieldAssembler assembler = SchemaBuilder
                .record(schemaType.getName().getLocalPart()).namespace(DEFAULT_NAMESPACE)
                .fields();

        assemble(schemaType, assembler);

        return (Schema) assembler.endRecord();
    }

    public static Schema fromXmlSchema(SchemaType schemaType) {
        SchemaBuilder.FieldAssembler assembler = SchemaBuilder
                .record(schemaType.getName().getLocalPart()).namespace(DEFAULT_NAMESPACE)
                .fields();

        assemble(schemaType, assembler);

        return (Schema) assembler.endRecord();
    }

    private static SchemaBuilder.FieldAssembler assemble(SchemaType schemaType, SchemaBuilder.FieldAssembler assembler) {
        for (SchemaProperty sp : schemaType.getAttributeProperties()) {
            assembleStringProperty(sp, assembler);
        }

        for (SchemaProperty sp : schemaType.getElementProperties()) {
            SchemaType st = sp.getType();

            if (isSimpleType(st)) {
                Schema.Type pt = BuildInTypeMapping.fromXmlTypeCode(XmlBeansUtils.getXMLBuildInType(sp.getType()).getCode());
                if (sp.getMaxOccurs() == null || sp.getMaxOccurs().intValue() > 1) {
                    if (BigInteger.ZERO.equals(sp.getMinOccurs())) {
                        // Union of Simple Array Type:
                        Schema nested = Schema.createArray(Schema.create(pt));
                        Schema union = SchemaBuilder.unionOf().type(nested).and().nullType().endUnion();
                        assembler.name(sp.getName().getLocalPart()).type(union).noDefault();

                    } else {
                        // Array of Simple Type:
                        assembler.name(sp.getName().getLocalPart()).type(Schema.createArray(Schema.create(pt))).noDefault();
                    }

                } else {
                    assembleSimpleProperty(sp, assembler);
                }
            } else {
                String name = schemaName(sp);

                SchemaBuilder.FieldAssembler sub = SchemaBuilder.record(name).namespace(DEFAULT_NAMESPACE).fields();
                assemble(sp.getType(), sub);

                if (sp.getMaxOccurs() == null || sp.getMaxOccurs().intValue() > 1
                        || SchemaParticle.SEQUENCE == st.getContentModel().getParticleType() && st.getContentModel().getMaxOccurs() == null
                ) {

                    if (BigInteger.ZERO.equals(sp.getMinOccurs())) {
                        //assembler.name(sp.getName().getLocalPart()).type((Schema) sub.endRecord()).noDefault();
                        Schema nested = (Schema) sub.endRecord();
                        Schema arrayType = Schema.createArray(nested);
                        Schema union = SchemaBuilder.unionOf().type(arrayType).and().nullType().endUnion();
                        assembler.name(sp.getName().getLocalPart()).type(union).noDefault();

                    } else {
                        // Array of Complex Type:
                        Schema nested = (Schema) sub.endRecord();
                        Schema arrayType = Schema.createArray(nested);
                        assembler.name(sp.getName().getLocalPart()).type(arrayType).noDefault();
                    }

                } else {
                    if (BigInteger.ZERO.equals(sp.getMinOccurs())) {
                        //assembler.name(sp.getName().getLocalPart()).type((Schema) sub.endRecord()).noDefault();

                        Schema nested = (Schema) sub.endRecord();
                        Schema union = SchemaBuilder.unionOf().type(nested).and().nullType().endUnion();
                        assembler.name(sp.getName().getLocalPart()).type(union).noDefault();

                    } else {
                        assembler.name(sp.getName().getLocalPart()).type((Schema) sub.endRecord()).noDefault();
                    }
                }
            }
        }

        return assembler;
    }

    private static String schemaName(SchemaProperty sp) {
        SchemaType st = sp.getType();
        SchemaType[] schemaTypes = st.getTypeSystem().globalTypes();

        String name = null;
        if(st.isAnonymousType()) {
            name = sp.getName().getLocalPart();
            for (SchemaType type: schemaTypes) {
                if(type.getName().getLocalPart().equals(name)) {
                    name = name + "TYPE";
                }
            }

        } else {
            name = sp.getType().getName().getLocalPart();
        }

        return name;
    }

    private static boolean isSimpleType(SchemaType st) {
        if (st.isSimpleType()) {
            return true;
        } else {
            SchemaType base = st.getBaseType();
            while (base != null) {
                if (base.isSimpleType()) {
                    return true;
                } else {
                    base = base.getBaseType();
                }
            }

            return false;

        }
    }

    private static void assembleSimpleProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {
        Schema.Type pt = BuildInTypeMapping.fromXmlTypeCode(XmlBeansUtils.getXMLBuildInType(sp.getType()).getCode());

        SchemaBuilder.FieldTypeBuilder builder = assembler.name(sp.getName().getLocalPart()).type();

        switch (pt) {
            case BOOLEAN:
                assembleBooleanProperty(sp, assembler);
                break;

            case BYTES:
                assembleBytesProperty(sp, assembler);
                break;

            case INT:
                assembleIntProperty(sp, assembler);
                break;

            case LONG:
                assembleLongProperty(sp, assembler);
                break;

            case FLOAT:
                assembleFloatProperty(sp, assembler);
                break;

            case DOUBLE:
                assembleDoubleProperty(sp, assembler);
                break;

            default:
                assembleStringProperty(sp, assembler);
        }
    }

    private static void assembleStringProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {

        SchemaBuilder.StringDefault stringDefault;
        if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
            stringDefault = assembler.name(sp.getName().getLocalPart()).type().nullable().stringType();

        } else {
            stringDefault = assembler.name(sp.getName().getLocalPart()).type().stringType();

        }

        if (sp.getDefaultText() != null) {
            stringDefault.stringDefault(sp.getDefaultText());

        } else {
            stringDefault.noDefault();
        }
    }

    private static void assembleBooleanProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {

        SchemaBuilder.BooleanDefault booleanDefault;
        if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
            booleanDefault = assembler.name(sp.getName().getLocalPart()).type().nullable().booleanType();
        } else {
            booleanDefault = assembler.name(sp.getName().getLocalPart()).type().booleanType();
        }

        if (sp.getDefaultText() != null) {
            booleanDefault.booleanDefault(Boolean.parseBoolean(sp.getDefaultText()));

        } else {
            booleanDefault.noDefault();
        }
    }

    private static void assembleBytesProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {

        SchemaBuilder.BytesDefault bytesDefault;
        if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
            bytesDefault = assembler.name(sp.getName().getLocalPart()).type().nullable().bytesType();
        } else {
            bytesDefault = assembler.name(sp.getName().getLocalPart()).type().bytesType();
        }

        if (sp.getDefaultText() != null) {
            bytesDefault.bytesDefault(sp.getDefaultText());

        } else {
            bytesDefault.noDefault();
        }
    }

    private static void assembleIntProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {

        if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
            assembler.name(sp.getName().getLocalPart()).type(SchemaBuilder.unionOf().nullType().and().stringType().endUnion()).noDefault();

        } else {
            SchemaBuilder.IntDefault intDefault = assembler.name(sp.getName().getLocalPart()).type().intType();

            if (sp.getDefaultText() != null) {
                intDefault.intDefault(Integer.parseInt(sp.getDefaultText()));

            } else {
                intDefault.noDefault();
            }

        }
    }

    private static void assembleLongProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {

        SchemaBuilder.LongDefault longDefault;
        if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
            longDefault = assembler.name(sp.getName().getLocalPart()).type().nullable().longType();
        } else {
            longDefault = assembler.name(sp.getName().getLocalPart()).type().longType();
        }

        if (sp.getDefaultText() != null) {
            longDefault.longDefault(Long.parseLong(sp.getDefaultText()));

        } else {
            longDefault.noDefault();
        }
    }

    private static void assembleFloatProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {

        SchemaBuilder.FloatDefault floatDefault;
        if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
            floatDefault = assembler.name(sp.getName().getLocalPart()).type().nullable().floatType();
        } else {
            floatDefault = assembler.name(sp.getName().getLocalPart()).type().floatType();
        }

        if (sp.getDefaultText() != null) {
            floatDefault.floatDefault(Float.parseFloat(sp.getDefaultText()));

        } else {
            floatDefault.noDefault();
        }
    }

    private static void assembleDoubleProperty(SchemaProperty sp, SchemaBuilder.FieldAssembler assembler) {

        SchemaBuilder.DoubleDefault doubleDefault;
        if (sp.getMinOccurs() == null || sp.getMinOccurs().intValue() == 0) {
            doubleDefault = assembler.name(sp.getName().getLocalPart()).type().nullable().doubleType();
        } else {
            doubleDefault = assembler.name(sp.getName().getLocalPart()).type().doubleType();
        }

        if (sp.getDefaultText() != null) {
            doubleDefault.doubleDefault(Double.parseDouble(sp.getDefaultText()));

        } else {
            doubleDefault.noDefault();
        }
    }

    public static enum BuildInTypeMapping {
        BOOLEAN(XmlBeansUtils.XMLBuildInType.BOOLEAN, Schema.Type.BOOLEAN),
        BASE_64_BINARY(XmlBeansUtils.XMLBuildInType.BASE_64_BINARY, Schema.Type.BYTES),
        HEX_BINARY(XmlBeansUtils.XMLBuildInType.HEX_BINARY, Schema.Type.BYTES),
        ANY_URI(XmlBeansUtils.XMLBuildInType.ANY_URI, Schema.Type.STRING),
        QNAME(XmlBeansUtils.XMLBuildInType.QNAME, Schema.Type.STRING),
        NOTATION(XmlBeansUtils.XMLBuildInType.NOTATION, Schema.Type.STRING),
        FLOAT(XmlBeansUtils.XMLBuildInType.FLOAT, Schema.Type.FLOAT),
        DOUBLE(XmlBeansUtils.XMLBuildInType.DOUBLE, Schema.Type.DOUBLE),
        DECIMAL(XmlBeansUtils.XMLBuildInType.DECIMAL, Schema.Type.DOUBLE),
        STRING(XmlBeansUtils.XMLBuildInType.STRING, Schema.Type.STRING),
        DURATION(XmlBeansUtils.XMLBuildInType.DURATION, Schema.Type.LONG),
        DATE_TIME(XmlBeansUtils.XMLBuildInType.DATE_TIME, Schema.Type.STRING),
        TIME(XmlBeansUtils.XMLBuildInType.TIME, Schema.Type.STRING),
        DATE(XmlBeansUtils.XMLBuildInType.DATE, Schema.Type.STRING),
        G_YEAR_MONTH(XmlBeansUtils.XMLBuildInType.G_YEAR_MONTH, Schema.Type.STRING),
        G_YEAR(XmlBeansUtils.XMLBuildInType.G_YEAR, Schema.Type.STRING),
        G_MONTH_DAY(XmlBeansUtils.XMLBuildInType.G_MONTH_DAY, Schema.Type.STRING),
        G_DAY(XmlBeansUtils.XMLBuildInType.G_DAY, Schema.Type.STRING),
        G_MONTH(XmlBeansUtils.XMLBuildInType.G_MONTH, Schema.Type.STRING),
        INTEGER(XmlBeansUtils.XMLBuildInType.INTEGER, Schema.Type.LONG),
        LONG(XmlBeansUtils.XMLBuildInType.LONG, Schema.Type.LONG),
        INT(XmlBeansUtils.XMLBuildInType.INT, Schema.Type.INT),
        SHORT(XmlBeansUtils.XMLBuildInType.SHORT, Schema.Type.INT),
        BYTE(XmlBeansUtils.XMLBuildInType.HEX_BINARY, Schema.Type.STRING),
        NON_POSITIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NON_POSITIVE_INTEGER, Schema.Type.LONG),
        NEGATIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NEGATIVE_INTEGER, Schema.Type.LONG),
        NON_NEGATIVE_INTEGER(XmlBeansUtils.XMLBuildInType.NON_NEGATIVE_INTEGER, Schema.Type.LONG),
        POSITIVE_INTEGER(XmlBeansUtils.XMLBuildInType.POSITIVE_INTEGER, Schema.Type.LONG),
        UNSIGNED_LONG(XmlBeansUtils.XMLBuildInType.UNSIGNED_LONG, Schema.Type.LONG),
        UNSIGNED_INT(XmlBeansUtils.XMLBuildInType.UNSIGNED_INT, Schema.Type.LONG),
        UNSIGNED_SHORT(XmlBeansUtils.XMLBuildInType.UNSIGNED_SHORT, Schema.Type.INT),
        UNSIGNED_BYTE(XmlBeansUtils.XMLBuildInType.UNSIGNED_BYTE, Schema.Type.INT),
        NORMALIZED_STRING(XmlBeansUtils.XMLBuildInType.STRING, Schema.Type.STRING),
        TOKEN(XmlBeansUtils.XMLBuildInType.TOKEN, Schema.Type.STRING),
        NAME(XmlBeansUtils.XMLBuildInType.NAME, Schema.Type.STRING),
        NCNAME(XmlBeansUtils.XMLBuildInType.NCNAME, Schema.Type.STRING),
        LANGUAGE(XmlBeansUtils.XMLBuildInType.LANGUAGE, Schema.Type.STRING),
        ID(XmlBeansUtils.XMLBuildInType.ID, Schema.Type.STRING),
        IDREF(XmlBeansUtils.XMLBuildInType.IDREF, Schema.Type.STRING),
        IDREFS(XmlBeansUtils.XMLBuildInType.IDREFS, Schema.Type.STRING),
        ENTITY(XmlBeansUtils.XMLBuildInType.ENTITY, Schema.Type.STRING),
        ENTITIES(XmlBeansUtils.XMLBuildInType.ENTITIES, Schema.Type.STRING),
        NMTOKEN(XmlBeansUtils.XMLBuildInType.NMTOKEN, Schema.Type.STRING),
        NMTOKENS(XmlBeansUtils.XMLBuildInType.NMTOKENS, Schema.Type.STRING);

        private final XmlBeansUtils.XMLBuildInType xmlBuildInType;
        private final Schema.Type avroType;

        private BuildInTypeMapping(XmlBeansUtils.XMLBuildInType xmlBuildInType, Schema.Type avroType) {
            this.xmlBuildInType = xmlBuildInType;
            this.avroType = avroType;
        }

        public static Schema.Type fromXmlTypeCode(int code) {
            switch (code) {
                case SchemaType.BTC_BOOLEAN:
                    return BOOLEAN.avroType;

                case SchemaType.BTC_BASE_64_BINARY:
                    return BASE_64_BINARY.avroType;

                case SchemaType.BTC_HEX_BINARY:
                    return HEX_BINARY.avroType;

                case SchemaType.BTC_ANY_URI:
                    return ANY_URI.avroType;

                case SchemaType.BTC_QNAME:
                    return QNAME.avroType;

                case SchemaType.BTC_NOTATION:
                    return NOTATION.avroType;

                case SchemaType.BTC_FLOAT:
                    return FLOAT.avroType;

                case SchemaType.BTC_DOUBLE:
                    return DOUBLE.avroType;

                case SchemaType.BTC_DECIMAL:
                    return DECIMAL.avroType;

                case SchemaType.BTC_STRING:
                    return STRING.avroType;

                case SchemaType.BTC_DURATION:
                    return DURATION.avroType;

                case SchemaType.BTC_DATE_TIME:
                    return DATE_TIME.avroType;

                case SchemaType.BTC_TIME:
                    return TIME.avroType;

                case SchemaType.BTC_DATE:
                    return DATE.avroType;

                case SchemaType.BTC_G_YEAR_MONTH:
                    return G_YEAR_MONTH.avroType;

                case SchemaType.BTC_G_YEAR:
                    return G_YEAR.avroType;

                case SchemaType.BTC_G_MONTH_DAY:
                    return G_MONTH_DAY.avroType;

                case SchemaType.BTC_G_DAY:
                    return G_DAY.avroType;

                case SchemaType.BTC_G_MONTH:
                    return G_MONTH.avroType;

                case SchemaType.BTC_INTEGER:
                    return INTEGER.avroType;

                case SchemaType.BTC_LONG:
                    return LONG.avroType;

                case SchemaType.BTC_INT:
                    return INT.avroType;

                case SchemaType.BTC_SHORT:
                    return SHORT.avroType;

                case SchemaType.BTC_BYTE:
                    return BYTE.avroType;

                case SchemaType.BTC_NON_POSITIVE_INTEGER:
                    return NON_NEGATIVE_INTEGER.avroType;

                case SchemaType.BTC_NEGATIVE_INTEGER:
                    return NEGATIVE_INTEGER.avroType;

                case SchemaType.BTC_NON_NEGATIVE_INTEGER:
                    return NON_POSITIVE_INTEGER.avroType;

                case SchemaType.BTC_POSITIVE_INTEGER:
                    return POSITIVE_INTEGER.avroType;

                case SchemaType.BTC_UNSIGNED_LONG:
                    return UNSIGNED_LONG.avroType;

                case SchemaType.BTC_UNSIGNED_INT:
                    return UNSIGNED_INT.avroType;

                case SchemaType.BTC_UNSIGNED_SHORT:
                    return UNSIGNED_SHORT.avroType;

                case SchemaType.BTC_UNSIGNED_BYTE:
                    return UNSIGNED_BYTE.avroType;

                case SchemaType.BTC_NORMALIZED_STRING:
                    return NORMALIZED_STRING.avroType;

                case SchemaType.BTC_TOKEN:
                    return TOKEN.avroType;

                case SchemaType.BTC_NAME:
                    return NAME.avroType;

                case SchemaType.BTC_NCNAME:
                    return NCNAME.avroType;

                case SchemaType.BTC_LANGUAGE:
                    return LANGUAGE.avroType;

                case SchemaType.BTC_ID:
                    return ID.avroType;

                case SchemaType.BTC_IDREF:
                    return IDREF.avroType;

                case SchemaType.BTC_IDREFS:
                    return IDREFS.avroType;

                case SchemaType.BTC_ENTITY:
                    return ENTITY.avroType;

                case SchemaType.BTC_ENTITIES:
                    return ENTITIES.avroType;

                case SchemaType.BTC_NMTOKEN:
                    return NMTOKEN.avroType;

                case SchemaType.BTC_NMTOKENS:
                    return NMTOKENS.avroType;

                default:
                    return null;
            }
        }
    }
}