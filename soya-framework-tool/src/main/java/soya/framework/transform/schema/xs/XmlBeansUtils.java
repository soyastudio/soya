package soya.framework.transform.schema.xs;

import org.apache.xmlbeans.*;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;

public class XmlBeansUtils {
    protected XmlBeansUtils() {
    }

    public static XMLBuildInType getXMLBuildInType(SchemaType type) {

        SchemaType st = type;
        while (st.getBaseType() != null && !st.isBuiltinType()) {
            st = st.getBaseType();
        }

        return XMLBuildInType.fromCode(st.getBuiltinTypeCode());
    }

    public static SchemaTypeSystem getSchemaTypeSystem(String xsd)
            throws XmlException, IOException {

        SchemaTypeSystem sts = null;
        // Parse the XML Schema object first to get XML object
        XmlObject parsedSchema = XmlObject.Factory.parse(xsd,
                new XmlOptions().setLoadLineNumbers().setLoadMessageDigest());
        // We may have more than schemas to validate with
        XmlObject[] schemas = new XmlObject[]{parsedSchema};
        // Compile the XML Schema to create a schema type system
        sts = XmlBeans.compileXsd(schemas, null, new XmlOptions()
                .setErrorListener(null).setCompileDownloadUrls()
                .setCompileNoPvrRule());

        return sts;
    }

    public static SchemaTypeSystem getSchemaTypeSystem(File file)
            throws XmlException, IOException {

        SchemaTypeSystem sts = null;
        // Parse the XML Schema object first to get XML object
        XmlObject parsedSchema = XmlObject.Factory.parse(file,
                new XmlOptions().setLoadLineNumbers().setLoadMessageDigest());
        // We may have more than schemas to validate with
        XmlObject[] schemas = new XmlObject[]{parsedSchema};
        // Compile the XML Schema to create a schema type system
        sts = XmlBeans.compileXsd(schemas, null, new XmlOptions()
                .setErrorListener(null).setCompileDownloadUrls()
                .setCompileNoPvrRule());

        return sts;
    }

    public static enum XMLBuildInType {

        BOOLEAN("xs:boolean", 3, XmlBoolean.class, boolean.class),
        BASE_64_BINARY("xs:base64Binary", 4, XmlBase64Binary.class, byte[].class),
        HEX_BINARY("xs:hexBinary", 5, XmlBase64Binary.class, byte[].class),
        ANY_URI("xs:anyURI", 6, XmlAnyURI.class, String.class),
        QNAME("xs:QName", 7, XmlQName.class, QName.class),
        NOTATION("xs:NOTATION", 8, XmlNOTATION.class, String.class),
        FLOAT("xs:float", 9, XmlFloat.class, float.class),
        DOUBLE("xs:double", 10, XmlDouble.class, double.class),
        DECIMAL("xs:decimal", 11, XmlDecimal.class, BigDecimal.class),
        STRING("xs:string", 12, XmlString.class, String.class),
        DURATION("xs:duration", 13, XmlDuration.class, Duration.class),
        DATE_TIME("xs:dateTime", 14, XmlDateTime.class, Calendar.class),
        TIME("xs:time", 15, XmlTime.class, Calendar.class),
        DATE("xs:date", 16, XmlDate.class, Calendar.class),
        G_YEAR_MONTH("xs:gYearMonth", 17, XmlGYearMonth.class, Calendar.class),
        G_YEAR("xs:gYear", 18, XmlGYear.class, Calendar.class),
        G_MONTH_DAY("xs:gMonthDay", 19, XmlGMonthDay.class, Calendar.class),
        G_DAY("xs:gDay", 20, XmlGDay.class, Calendar.class),
        G_MONTH("xs:gMonth", 21, XmlGMonth.class, Calendar.class),
        INTEGER("xs:integer", 22, XmlInteger.class, BigInteger.class),
        LONG("xs:long", 23, XmlLong.class, long.class),
        INT("xs:int", 24, XmlInt.class, int.class),
        SHORT("xs:short", 25, XmlShort.class, short.class),
        BYTE("xs:byte", 26, XmlByte.class, byte.class),
        NON_POSITIVE_INTEGER("xs:nonPositiveInteger", 27, XmlNonPositiveInteger.class, BigInteger.class),
        NEGATIVE_INTEGER("xs:NegativeInteger", 28, XmlNegativeInteger.class, BigInteger.class),
        NON_NEGATIVE_INTEGER("xs:nonNegativeInteger", 29, XmlNonNegativeInteger.class, BigInteger.class),
        POSITIVE_INTEGER("xs:positiveInteger", 30, XmlPositiveInteger.class, BigInteger.class),
        UNSIGNED_LONG("xs:unsignedLong", 31, XmlUnsignedLong.class, long.class),
        UNSIGNED_INT("xs:unsignedInt", 32, XmlUnsignedInt.class, long.class),
        UNSIGNED_SHORT("xs:unsignedShort", 33, XmlUnsignedShort.class, int.class),
        UNSIGNED_BYTE("xs:unsignedByte", 34, XmlUnsignedByte.class, short.class),
        NORMALIZED_STRING("xs:normalizedString", 35, XmlNormalizedString.class, String.class),
        TOKEN("xs:token", 36, XmlToken.class, String.class),
        NAME("xs:Name", 37, XmlName.class, String.class),
        NCNAME("xs:NCName", 38, XmlNCName.class, String.class),
        LANGUAGE("xs:language", 39, XmlLanguage.class, String.class),
        ID("xs:ID", 40, XmlID.class, String.class),
        IDREF("xs:IDREF", 41, XmlIDREF.class, String.class),
        IDREFS("xs:IDREFS", 42, XmlIDREFS.class, String.class),
        ENTITY("xs:ENTITY", 43, XmlENTITY.class, String.class),
        ENTITIES("xs:ENTITIES", 44, XmlENTITIES.class, List.class),
        NMTOKEN("xs:NMTOKEN", 45, XmlNMTOKEN.class, String.class),
        NMTOKENS("xs:NMTOKENS", 46, XmlNMTOKENS.class, List.class);

        private final String name;
        private final int code;
        private final Class<? extends XmlAnySimpleType> xmlType;
        private final Class<?> javaType;

        private XMLBuildInType(String name, int code, Class<? extends XmlAnySimpleType> xmlType, Class<?> javaType) {
            this.name = name;
            this.code = code;
            this.xmlType = xmlType;
            this.javaType = javaType;
        }

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }

        public Class<? extends XmlAnySimpleType> getXmlType() {
            return xmlType;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public static final XMLBuildInType fromCode(int code) {
            switch (code) {
                case SchemaType.BTC_BOOLEAN:
                    return XMLBuildInType.BOOLEAN;

                case SchemaType.BTC_BASE_64_BINARY:
                    return XMLBuildInType.BASE_64_BINARY;

                case SchemaType.BTC_HEX_BINARY:
                    return XMLBuildInType.HEX_BINARY;

                case SchemaType.BTC_ANY_URI:
                    return XMLBuildInType.ANY_URI;

                case SchemaType.BTC_QNAME:
                    return XMLBuildInType.QNAME;

                case SchemaType.BTC_NOTATION:
                    return XMLBuildInType.NOTATION;

                case SchemaType.BTC_FLOAT:
                    return XMLBuildInType.FLOAT;

                case SchemaType.BTC_DOUBLE:
                    return XMLBuildInType.DOUBLE;

                case SchemaType.BTC_DECIMAL:
                    return XMLBuildInType.DECIMAL;

                case SchemaType.BTC_STRING:
                    return XMLBuildInType.STRING;

                case SchemaType.BTC_DURATION:
                    return XMLBuildInType.DURATION;

                case SchemaType.BTC_DATE_TIME:
                    return XMLBuildInType.DATE_TIME;

                case SchemaType.BTC_TIME:
                    return XMLBuildInType.TIME;

                case SchemaType.BTC_DATE:
                    return XMLBuildInType.DATE;

                case SchemaType.BTC_G_YEAR_MONTH:
                    return XMLBuildInType.G_YEAR_MONTH;

                case SchemaType.BTC_G_YEAR:
                    return XMLBuildInType.G_YEAR;

                case SchemaType.BTC_G_MONTH_DAY:
                    return XMLBuildInType.G_MONTH_DAY;

                case SchemaType.BTC_G_DAY:
                    return XMLBuildInType.G_DAY;

                case SchemaType.BTC_G_MONTH:
                    return XMLBuildInType.G_MONTH;

                case SchemaType.BTC_INTEGER:
                    return XMLBuildInType.INTEGER;

                case SchemaType.BTC_LONG:
                    return XMLBuildInType.LONG;

                case SchemaType.BTC_INT:
                    return XMLBuildInType.INT;

                case SchemaType.BTC_SHORT:
                    return XMLBuildInType.SHORT;

                case SchemaType.BTC_BYTE:
                    return XMLBuildInType.BYTE;

                case SchemaType.BTC_NON_POSITIVE_INTEGER:
                    return XMLBuildInType.NON_NEGATIVE_INTEGER;

                case SchemaType.BTC_NEGATIVE_INTEGER:
                    return XMLBuildInType.NEGATIVE_INTEGER;

                case SchemaType.BTC_NON_NEGATIVE_INTEGER:
                    return XMLBuildInType.NON_POSITIVE_INTEGER;

                case SchemaType.BTC_POSITIVE_INTEGER:
                    return XMLBuildInType.POSITIVE_INTEGER;

                case SchemaType.BTC_UNSIGNED_LONG:
                    return XMLBuildInType.UNSIGNED_LONG;

                case SchemaType.BTC_UNSIGNED_INT:
                    return XMLBuildInType.UNSIGNED_INT;

                case SchemaType.BTC_UNSIGNED_SHORT:
                    return XMLBuildInType.UNSIGNED_SHORT;

                case SchemaType.BTC_UNSIGNED_BYTE:
                    return XMLBuildInType.UNSIGNED_BYTE;

                case SchemaType.BTC_NORMALIZED_STRING:
                    return XMLBuildInType.NORMALIZED_STRING;

                case SchemaType.BTC_TOKEN:
                    return XMLBuildInType.TOKEN;

                case SchemaType.BTC_NAME:
                    return XMLBuildInType.NAME;

                case SchemaType.BTC_NCNAME:
                    return XMLBuildInType.NCNAME;

                case SchemaType.BTC_LANGUAGE:
                    return XMLBuildInType.LANGUAGE;

                case SchemaType.BTC_ID:
                    return XMLBuildInType.ID;

                case SchemaType.BTC_IDREF:
                    return XMLBuildInType.IDREF;

                case SchemaType.BTC_IDREFS:
                    return XMLBuildInType.IDREFS;

                case SchemaType.BTC_ENTITY:
                    return XMLBuildInType.ENTITY;

                case SchemaType.BTC_ENTITIES:
                    return XMLBuildInType.ENTITIES;

                case SchemaType.BTC_NMTOKEN:
                    return XMLBuildInType.NMTOKEN;

                case SchemaType.BTC_NMTOKENS:
                    return XMLBuildInType.NMTOKENS;

                default:
                    return null;
            }
        }
    }
}
