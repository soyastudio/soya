package soya.framework.transform.schema.avro;

import com.google.common.base.CaseFormat;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.*;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class SampleAvroGenerator {

    private final Map<Schema, List<Object>> optionsCache = new HashMap<>();
    private final Map<Schema, Iterator<Object>> iteratorCache = new IdentityHashMap<>();

    public static final String ARG_PROPERTIES_PROP = "arg.properties";

    public static final String LENGTH_PROP = "length";
    public static final String LENGTH_PROP_MIN = "min";
    public static final String LENGTH_PROP_MAX = "max";

    public static final String REGEX_PROP = "regex";

    public static final String PREFIX_PROP = "prefix";

    public static final String SUFFIX_PROP = "suffix";

    public static final String OPTIONS_PROP = "options";

    public static final String OPTIONS_PROP_FILE = "file";

    public static final String OPTIONS_PROP_ENCODING = "encoding";

    public static final String KEYS_PROP = "keys";

    public static final String RANGE_PROP = "range";

    public static final String RANGE_PROP_MIN = "min";

    public static final String RANGE_PROP_MAX = "max";

    public static final String ODDS_PROP = "odds";

    public static final String ITERATION_PROP = "iteration";

    public static final String ITERATION_PROP_START = "start";

    public static final String ITERATION_PROP_RESTART = "restart";

    public static final String ITERATION_PROP_STEP = "step";

    public static final String ITERATION_PROP_INITIAL = "initial";

    static final String DECIMAL_LOGICAL_TYPE_NAME = "decimal";

    private final Schema topLevelSchema;
    private final Random random;
    private final long generation;

    public SampleAvroGenerator(Schema topLevelSchema, Random random, long generation) {
        this.topLevelSchema = topLevelSchema;
        this.random = random;
        this.generation = generation;
    }

    public static class Builder {

        private Schema topLevelSchema;
        private Random random;
        private long generation;
        private Schema.Parser parser;

        public Builder() {
            parser = new Schema.Parser();
            random = new Random();
            generation = 0L;
        }

        public Builder schema(Schema schema) {
            topLevelSchema = schema;
            return this;
        }

        public Builder schemaFile(File schemaFile) throws IOException {
            topLevelSchema = parser.parse(schemaFile);
            return this;
        }

        public Builder schemaStream(InputStream schemaStream) throws IOException {
            topLevelSchema = parser.parse(schemaStream);
            return this;
        }

        public Builder schemaString(String schemaString) {
            topLevelSchema = parser.parse(schemaString);
            return this;
        }

        public Builder random(Random random) {
            this.random = random;
            return this;
        }

        public Builder generation(long generation) {
            this.generation = generation;
            return this;
        }

        public SampleAvroGenerator build() {
            return new SampleAvroGenerator(topLevelSchema, random, generation);
        }
    }

    public Schema schema() {
        return topLevelSchema;
    }

    public Object generate() {
        return generateObject(topLevelSchema);
    }

    private Object generateObject(Schema schema) {
        Map propertiesProp = getProperties(schema).orElse(Collections.emptyMap());
        if (propertiesProp.containsKey(OPTIONS_PROP)) {
            return generateOption(schema, propertiesProp);
        }
        if (propertiesProp.containsKey(ITERATION_PROP)) {
            return generateIteration(schema, propertiesProp);
        }

        switch (schema.getType()) {
            case ARRAY:
                return generateArray(schema, propertiesProp);
            case BOOLEAN:
                return generateBoolean(propertiesProp);
            case BYTES:
                return generateBytes(schema, propertiesProp);
            case DOUBLE:
                return generateDouble(propertiesProp);
            case ENUM:
                return generateEnumSymbol(schema);
            case FIXED:
                return generateFixed(schema);
            case FLOAT:
                return generateFloat(propertiesProp);
            case INT:
                return generateInt(propertiesProp);
            case LONG:
                return generateLong(propertiesProp);
            case MAP:
                return generateMap(schema, propertiesProp);
            case NULL:
                return generateNull();
            case RECORD:
                return generateRecord(schema);
            case STRING:
                return generateString(schema, propertiesProp);
            case UNION:
                return generateUnion(schema);
            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private Optional<Map> getProperties(Schema schema) {
        Object propertiesProp = schema.getObjectProp(ARG_PROPERTIES_PROP);
        if (propertiesProp == null) {
            return Optional.empty();
        } else if (propertiesProp instanceof Map) {
            return Optional.of((Map) propertiesProp);
        } else {
            throw new RuntimeException(String.format(
                    "%s property must be given as object, was %s instead",
                    ARG_PROPERTIES_PROP,
                    propertiesProp.getClass().getName()
            ));
        }
    }

    private <L extends LogicalType> L getLogicalType(Schema schema, String logicalTypeName, Class<L> logicalTypeClass) {
        return Optional.ofNullable(schema.getLogicalType())
                .filter(logicalType -> Objects.equals(logicalTypeName, logicalType.getName()))
                .map(logicalTypeClass::cast)
                .orElse(null);
    }

    private LogicalTypes.Decimal getDecimalLogicalType(Schema schema) {
        return getLogicalType(schema, DECIMAL_LOGICAL_TYPE_NAME, LogicalTypes.Decimal.class);
    }

    private void enforceMutualExclusion(Map propertiesProp, String includedProp, String... excludedProps) {
        for (String excludedProp : excludedProps) {
            if (propertiesProp.containsKey(excludedProp)) {
                throw new RuntimeException(String.format(
                        "Cannot specify %s prop when %s prop is given",
                        excludedProp,
                        includedProp
                ));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object wrapOption(Schema schema, Object option) {
        if (schema.getType() == Schema.Type.BYTES && option instanceof String) {
            option = ByteBuffer.wrap(((String) option).getBytes(Charset.defaultCharset()));
        } else if (schema.getType() == Schema.Type.FLOAT && option instanceof Double) {
            option = ((Double) option).floatValue();
        } else if (schema.getType() == Schema.Type.LONG && option instanceof Integer) {
            option = ((Integer) option).longValue();
        } else if (schema.getType() == Schema.Type.ARRAY && option instanceof Collection) {
            option = new GenericData.Array(schema, (Collection) option);
        } else if (schema.getType() == Schema.Type.ENUM && option instanceof String) {
            option = new GenericData.EnumSymbol(schema, (String) option);
        } else if (schema.getType() == Schema.Type.FIXED && option instanceof String) {
            option =
                    new GenericData.Fixed(schema, ((String) option).getBytes(Charset.defaultCharset()));
        } else if (schema.getType() == Schema.Type.RECORD && option instanceof Map) {
            Map optionMap = (Map) option;
            GenericRecordBuilder optionBuilder = new GenericRecordBuilder(schema);
            for (Schema.Field field : schema.getFields()) {
                if (optionMap.containsKey(field.name())) {
                    optionBuilder.set(field, optionMap.get(field.name()));
                }
            }
            option = optionBuilder.build();
        }
        return option;
    }

    @SuppressWarnings("unchecked")
    private List<Object> parseOptions(Schema schema, Map propertiesProp) {
        enforceMutualExclusion(
                propertiesProp, OPTIONS_PROP,
                LENGTH_PROP, REGEX_PROP, ITERATION_PROP, RANGE_PROP
        );

        Object optionsProp = propertiesProp.get(OPTIONS_PROP);
        if (optionsProp instanceof Collection) {
            Collection optionsList = (Collection) optionsProp;
            if (optionsList.isEmpty()) {
                throw new RuntimeException(String.format(
                        "%s property cannot be empty",
                        OPTIONS_PROP
                ));
            }
            List<Object> options = new ArrayList<>();
            for (Object option : optionsList) {
                option = wrapOption(schema, option);
                if (!GenericData.get().validate(schema, option)) {
                    throw new RuntimeException(String.format(
                            "Invalid option for %s schema: type %s, value '%s'",
                            schema.getType().getName(),
                            option.getClass().getName(),
                            option
                    ));
                }
                options.add(option);
            }
            return options;
        } else if (optionsProp instanceof Map) {
            Map optionsProps = (Map) optionsProp;
            Object optionsFile = optionsProps.get(OPTIONS_PROP_FILE);
            if (optionsFile == null) {
                throw new RuntimeException(String.format(
                        "%s property must contain '%s' field when given as object",
                        OPTIONS_PROP,
                        OPTIONS_PROP_FILE
                ));
            }
            if (!(optionsFile instanceof String)) {
                throw new RuntimeException(String.format(
                        "'%s' field of %s property must be given as string, was %s instead",
                        OPTIONS_PROP_FILE,
                        OPTIONS_PROP,
                        optionsFile.getClass().getName()
                ));
            }
            Object optionsEncoding = optionsProps.get(OPTIONS_PROP_ENCODING);
            if (optionsEncoding == null) {
                throw new RuntimeException(String.format(
                        "%s property must contain '%s' field when given as object",
                        OPTIONS_PROP,
                        OPTIONS_PROP_FILE
                ));
            }
            if (!(optionsEncoding instanceof String)) {
                throw new RuntimeException(String.format(
                        "'%s' field of %s property must be given as string, was %s instead",
                        OPTIONS_PROP_ENCODING,
                        OPTIONS_PROP,
                        optionsEncoding.getClass().getName()
                ));
            }
            try (InputStream optionsStream = new FileInputStream((String) optionsFile)) {
                DatumReader<Object> optionReader = new GenericDatumReader(schema);
                Decoder decoder;
                if ("binary".equals(optionsEncoding)) {
                    decoder = DecoderFactory.get().binaryDecoder(optionsStream, null);
                } else if ("json".equals(optionsEncoding)) {
                    decoder = DecoderFactory.get().jsonDecoder(schema, optionsStream);
                } else {
                    throw new RuntimeException(String.format(
                            "'%s' field of %s property only supports two formats: 'binary' and 'json'",
                            OPTIONS_PROP_ENCODING,
                            OPTIONS_PROP
                    ));
                }
                List<Object> options = new ArrayList<>();
                Object option = optionReader.read(null, decoder);
                while (option != null) {
                    option = wrapOption(schema, option);
                    if (!GenericData.get().validate(schema, option)) {
                        throw new RuntimeException(String.format(
                                "Invalid option for %s schema: type %s, value '%s'",
                                schema.getType().getName(),
                                option.getClass().getName(),
                                option
                        ));
                    }
                    options.add(option);
                    try {
                        option = optionReader.read(null, decoder);
                    } catch (EOFException eofe) {
                        break;
                    }
                }
                return options;
            } catch (FileNotFoundException fnfe) {
                throw new RuntimeException(
                        String.format(
                                "Unable to locate options file '%s'",
                                optionsFile
                        ),
                        fnfe
                );
            } catch (IOException ioe) {
                throw new RuntimeException(
                        String.format(
                                "Unable to read options file '%s'",
                                optionsFile
                        ),
                        ioe
                );
            }
        } else {
            throw new RuntimeException(String.format(
                    "%s prop must be an array or an object, was %s instead",
                    OPTIONS_PROP,
                    optionsProp.getClass().getName()
            ));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T generateOption(Schema schema, Map propertiesProp) {
        if (!optionsCache.containsKey(schema)) {
            optionsCache.put(schema, parseOptions(schema, propertiesProp));
        }
        List<Object> options = optionsCache.get(schema);
        return (T) options.get(random.nextInt(options.size()));
    }

    private Iterator<Object> getBooleanIterator(Map iterationProps) {
        Object startProp = iterationProps.get(ITERATION_PROP_START);
        if (startProp == null) {
            throw new RuntimeException(String.format(
                    "%s property must contain %s field",
                    ITERATION_PROP,
                    ITERATION_PROP_START
            ));
        }
        if (!(startProp instanceof Boolean)) {
            throw new RuntimeException(String.format(
                    "%s field of %s property for a boolean schema must be a boolean, was %s instead",
                    ITERATION_PROP_START,
                    ITERATION_PROP,
                    startProp.getClass().getName()
            ));
        }
        if (iterationProps.containsKey(ITERATION_PROP_RESTART)) {
            throw new RuntimeException(String.format(
                    "%s property cannot contain %s field for a boolean schema",
                    ITERATION_PROP,
                    ITERATION_PROP_RESTART
            ));
        }
        if (iterationProps.containsKey(ITERATION_PROP_STEP)) {
            throw new RuntimeException(String.format(
                    "%s property cannot contain %s field for a boolean schema",
                    ITERATION_PROP,
                    ITERATION_PROP_STEP
            ));
        }

        // If an odd number of records have been generated previously, then the boolean will have
        // changed state effectively once, and so the start state should be inverted.
        return new BooleanIterator((generation % 2 == 1) ^ ((Boolean) startProp));
    }

    private Iterator<Object> getIntegralIterator(
            Long iterationStartField,
            Long iterationRestartField,
            Long iterationStepField,
            Long iterationInitialField,
            IntegralIterator.Type type) {

        if (iterationStartField == null) {
            throw new RuntimeException(String.format(
                    "%s property must contain %s field",
                    ITERATION_PROP,
                    ITERATION_PROP_START
            ));
        }

        long iterationStart = iterationStartField;
        long iterationRestart;
        long iterationStep;

        long restartHighDefault;
        long restartLowDefault;
        switch (type) {
            case INTEGER:
                restartHighDefault = Integer.MAX_VALUE;
                restartLowDefault = Integer.MIN_VALUE;
                break;
            case LONG:
                restartHighDefault = Long.MAX_VALUE;
                restartLowDefault = Long.MIN_VALUE;
                break;
            default:
                throw new RuntimeException(String.format(
                        "Unexpected IntegralIterator type: %s",
                        type
                ));
        }

        if (iterationRestartField == null && iterationStepField == null) {
            iterationRestart = restartHighDefault;
            iterationStep = 1;
        } else if (iterationRestartField == null) {
            iterationStep = iterationStepField;
            if (iterationStep > 0) {
                iterationRestart = restartHighDefault;
            } else if (iterationStep < 0) {
                iterationRestart = -1 * restartLowDefault;
            } else {
                throw new RuntimeException(String.format(
                        "%s field of %s property cannot be zero",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP
                ));
            }
        } else if (iterationStepField == null) {
            iterationRestart = iterationRestartField;
            if (iterationRestart > iterationStart) {
                iterationStep = 1;
            } else if (iterationRestart < iterationStart) {
                iterationStep = -1;
            } else {
                throw new RuntimeException(String.format(
                        "%s and %s fields of %s property cannot be equal",
                        ITERATION_PROP_START,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP
                ));
            }
        } else {
            iterationRestart = iterationRestartField;
            iterationStep = iterationStepField;
            if (iterationStep == 0) {
                throw new RuntimeException(String.format(
                        "%s field of %s property cannot be zero",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP
                ));
            }
            if (iterationStart == iterationRestart) {
                throw new RuntimeException(String.format(
                        "%s and %s fields of %s property cannot be equal",
                        ITERATION_PROP_START,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP
                ));
            }
            if (iterationRestart > iterationStart && iterationStep < 0) {
                throw new RuntimeException(String.format(
                        "%s field of %s property must be positive when %s field is greater than %s field",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP_START
                ));
            }
            if (iterationRestart < iterationStart && iterationStep > 0) {
                throw new RuntimeException(String.format(
                        "%s field of %s property must be negative when %s field is less than %s field",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP_START
                ));
            }
        }

        long iterationInitial = iterationStart;
        if (iterationInitialField != null) {
            iterationInitial = iterationInitialField;
        }

        return new IntegralIterator(
                iterationStart,
                iterationRestart,
                iterationStep,
                iterationInitial,
                generation,
                type
        );
    }

    private Iterator<Object> getDecimalIterator(
            Double iterationStartField,
            Double iterationRestartField,
            Double iterationStepField,
            Double iterationInitialField,
            DecimalIterator.Type type) {

        if (iterationStartField == null) {
            throw new RuntimeException(String.format(
                    "%s property must contain %s field",
                    ITERATION_PROP,
                    ITERATION_PROP_START
            ));
        }

        double iterationStart = iterationStartField;
        double iterationRestart;
        double iterationStep;

        double restartHighDefault;
        double restartLowDefault;
        switch (type) {
            case FLOAT:
                restartHighDefault = Float.MAX_VALUE;
                restartLowDefault = -1 * Float.MAX_VALUE;
                break;
            case DOUBLE:
                restartHighDefault = Double.MAX_VALUE;
                restartLowDefault = -1 * Double.MAX_VALUE;
                break;
            default:
                throw new RuntimeException(String.format(
                        "Unexpected DecimalIterator type: %s",
                        type
                ));
        }

        if (iterationRestartField == null && iterationStepField == null) {
            iterationRestart = restartHighDefault;
            iterationStep = 1;
        } else if (iterationRestartField == null) {
            iterationStep = iterationStepField;
            if (iterationStep > 0) {
                iterationRestart = restartHighDefault;
            } else if (iterationStep < 0) {
                iterationRestart = -1 * restartLowDefault;
            } else {
                throw new RuntimeException(String.format(
                        "%s field of %s property cannot be zero",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP
                ));
            }
        } else if (iterationStepField == null) {
            iterationRestart = iterationRestartField;
            if (iterationRestart > iterationStart) {
                iterationStep = 1;
            } else if (iterationRestart < iterationStart) {
                iterationStep = -1;
            } else {
                throw new RuntimeException(String.format(
                        "%s and %s fields of %s property cannot be equal",
                        ITERATION_PROP_START,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP
                ));
            }
        } else {
            iterationRestart = iterationRestartField;
            iterationStep = iterationStepField;
            if (iterationStep == 0) {
                throw new RuntimeException(String.format(
                        "%s field of %s property cannot be zero",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP
                ));
            }
            if (iterationStart == iterationRestart) {
                throw new RuntimeException(String.format(
                        "%s and %s fields of %s property cannot be equal",
                        ITERATION_PROP_START,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP
                ));
            }
            if (iterationRestart > iterationStart && iterationStep < 0) {
                throw new RuntimeException(String.format(
                        "%s field of %s property must be positive when %s field is greater than %s field",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP_START
                ));
            }
            if (iterationRestart < iterationStart && iterationStep > 0) {
                throw new RuntimeException(String.format(
                        "%s field of %s property must be negative when %s field is less than %s field",
                        ITERATION_PROP_STEP,
                        ITERATION_PROP,
                        ITERATION_PROP_RESTART,
                        ITERATION_PROP_START
                ));
            }
        }

        double iterationInitial = iterationStart;
        if (iterationInitialField != null) {
            iterationInitial = iterationInitialField;
        }

        return new DecimalIterator(
                iterationStart,
                iterationRestart,
                iterationStep,
                iterationInitial,
                generation,
                type
        );
    }

    private Iterator<Object> parseIterations(Schema schema, Map propertiesProp) {
        enforceMutualExclusion(
                propertiesProp, ITERATION_PROP,
                LENGTH_PROP, REGEX_PROP, OPTIONS_PROP, RANGE_PROP
        );

        Object iterationProp = propertiesProp.get(ITERATION_PROP);
        if (!(iterationProp instanceof Map)) {
            throw new RuntimeException(String.format(
                    "%s prop must be an object, was %s instead",
                    ITERATION_PROP,
                    iterationProp.getClass().getName()
            ));
        }

        Map iterationProps = (Map) iterationProp;
        switch (schema.getType()) {
            case BOOLEAN:
                return getBooleanIterator(iterationProps);
            case INT:
                return getIntegerIterator(iterationProps);
            case LONG:
                return getLongIterator(iterationProps);
            case FLOAT:
                return getFloatIterator(iterationProps);
            case DOUBLE:
                return getDoubleIterator(iterationProps);
            case STRING:
                return createStringIterator(getIntegerIterator(iterationProps), propertiesProp);
            default:
                throw new UnsupportedOperationException(String.format(
                        "%s property can only be specified on numeric, boolean or string schemas, "
                                + "not %s schema",
                        ITERATION_PROP,
                        schema.getType().toString()
                ));
        }
    }

    private Iterator<Object> getDoubleIterator(final Map iterationProps) {
        Double iterationStartField = getDecimalNumberField(
                ITERATION_PROP,
                ITERATION_PROP_START,
                iterationProps
        );
        Double iterationRestartField = getDecimalNumberField(
                ITERATION_PROP,
                ITERATION_PROP_RESTART,
                iterationProps
        );
        Double iterationStepField = getDecimalNumberField(
                ITERATION_PROP,
                ITERATION_PROP_STEP,
                iterationProps
        );
        Double iterationInitialField = getDecimalNumberField(
                ITERATION_PROP,
                ITERATION_PROP_INITIAL,
                iterationProps
        );
        return getDecimalIterator(
                iterationStartField,
                iterationRestartField,
                iterationStepField,
                iterationInitialField,
                DecimalIterator.Type.DOUBLE
        );
    }

    private Iterator<Object> getFloatIterator(final Map iterationProps) {
        Float iterationStartField = getFloatNumberField(
                ITERATION_PROP,
                ITERATION_PROP_START,
                iterationProps
        );
        Float iterationRestartField = getFloatNumberField(
                ITERATION_PROP,
                ITERATION_PROP_RESTART,
                iterationProps
        );
        Float iterationStepField = getFloatNumberField(
                ITERATION_PROP,
                ITERATION_PROP_STEP,
                iterationProps
        );
        Float iterationInitialField = getFloatNumberField(
                ITERATION_PROP,
                ITERATION_PROP_INITIAL,
                iterationProps
        );
        return getDecimalIterator(
                iterationStartField != null ? iterationStartField.doubleValue() : null,
                iterationRestartField != null ? iterationRestartField.doubleValue() : null,
                iterationStepField != null ? iterationStepField.doubleValue() : null,
                iterationInitialField != null ? iterationInitialField.doubleValue() : null,
                DecimalIterator.Type.FLOAT
        );
    }

    private Iterator<Object> getLongIterator(final Map iterationProps) {
        Long iterationStartField = getIntegralNumberField(
                ITERATION_PROP,
                ITERATION_PROP_START,
                iterationProps
        );
        Long iterationRestartField = getIntegralNumberField(
                ITERATION_PROP,
                ITERATION_PROP_RESTART,
                iterationProps
        );
        Long iterationStepField = getIntegralNumberField(
                ITERATION_PROP,
                ITERATION_PROP_STEP,
                iterationProps
        );
        Long iterationInitialField = getIntegralNumberField(
                ITERATION_PROP,
                ITERATION_PROP_INITIAL,
                iterationProps
        );
        return getIntegralIterator(
                iterationStartField,
                iterationRestartField,
                iterationStepField,
                iterationInitialField,
                IntegralIterator.Type.LONG
        );
    }

    private Iterator<Object> createStringIterator(Iterator<Object> inner, Map propertiesProp) {
        return new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return inner.hasNext();
            }

            @Override
            public Object next() {
                return prefixAndSuffixString(inner.next().toString(), propertiesProp);
            }
        };
    }

    private Iterator<Object> getIntegerIterator(Map iterationProps) {
        Integer iterationStartField = getIntegerNumberField(
                ITERATION_PROP,
                ITERATION_PROP_START,
                iterationProps
        );
        Integer iterationRestartField = getIntegerNumberField(
                ITERATION_PROP,
                ITERATION_PROP_RESTART,
                iterationProps
        );
        Integer iterationStepField = getIntegerNumberField(
                ITERATION_PROP,
                ITERATION_PROP_STEP,
                iterationProps
        );
        Integer iterationInitialField = getIntegerNumberField(
                ITERATION_PROP,
                ITERATION_PROP_INITIAL,
                iterationProps
        );
        return getIntegralIterator(
                iterationStartField != null ? iterationStartField.longValue() : null,
                iterationRestartField != null ? iterationRestartField.longValue() : null,
                iterationStepField != null ? iterationStepField.longValue() : null,
                iterationInitialField != null ? iterationInitialField.longValue() : null,
                IntegralIterator.Type.INTEGER
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T generateIteration(Schema schema, Map propertiesProp) {
        if (!iteratorCache.containsKey(schema)) {
            iteratorCache.put(schema, parseIterations(schema, propertiesProp));
        }
        return (T) iteratorCache.get(schema).next();
    }

    private Collection<Object> generateArray(Schema schema, Map propertiesProp) {
        //int length = getLengthBounds(propertiesProp).random();
        int length = 1;
        Collection<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(generateObject(schema.getElementType()));
        }
        return result;
    }

    private Boolean generateBoolean(Map propertiesProp) {
        Double odds = getDecimalNumberField(ARG_PROPERTIES_PROP, ODDS_PROP, propertiesProp);
        if (odds == null) {
            return random.nextBoolean();
        } else {
            if (odds < 0.0 || odds > 1.0) {
                throw new RuntimeException(String.format(
                        "%s property must be in the range [0.0, 1.0]",
                        ODDS_PROP
                ));
            }
            return random.nextDouble() < odds;
        }
    }

    private ByteBuffer generateBytes(Schema schema, Map propertiesProp) {
        LogicalTypes.Decimal decimalLogicalType = getDecimalLogicalType(schema);
        byte[] bytes;
        if (decimalLogicalType != null) {
            bytes = generateDecimal(decimalLogicalType);
        } else {
            bytes = new byte[getLengthBounds(propertiesProp.get(LENGTH_PROP)).random()];
            random.nextBytes(bytes);
        }
        return ByteBuffer.wrap(bytes);
    }

    private Double generateDouble(Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Double rangeMinField = getDecimalNumberField(RANGE_PROP, RANGE_PROP_MIN, rangeProps);
                Double rangeMaxField = getDecimalNumberField(RANGE_PROP, RANGE_PROP_MAX, rangeProps);
                double rangeMin = rangeMinField != null ? rangeMinField : -1 * Double.MAX_VALUE;
                double rangeMax = rangeMaxField != null ? rangeMaxField : Double.MAX_VALUE;
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + (random.nextDouble() * (rangeMax - rangeMin));
            } else {
                throw new RuntimeException(String.format(
                        "%s property must be an object",
                        RANGE_PROP
                ));
            }
        }
        return random.nextDouble();
    }

    private GenericEnumSymbol generateEnumSymbol(Schema schema) {
        List<String> enums = schema.getEnumSymbols();
        return new
                GenericData.EnumSymbol(schema, enums.get(random.nextInt(enums.size())));
    }

    private GenericFixed generateFixed(Schema schema) {
        LogicalTypes.Decimal decimalLogicalType = getDecimalLogicalType(schema);
        byte[] bytes;
        if (decimalLogicalType != null) {
            bytes = generateDecimal(decimalLogicalType);
        } else {
            bytes = new byte[schema.getFixedSize()];
            random.nextBytes(bytes);
        }
        return new GenericData.Fixed(schema, bytes);
    }

    /*
      According to the Avro 1.9.1 spec (http://avro.apache.org/docs/1.9.1/spec.html#Decimal):

      "The decimal logical type represents an arbitrary-precision signed decimal number of the form
    unscaled × 10-scale."

      "A decimal logical type annotates Avro bytes or fixed types. The byte array must contain the
    two's-complement representation of the unscaled integer value in big-endian byte order. The scale
    is fixed, and is specified using an attribute."


      We generate a random decimal here by starting with a value of zero, then repeatedly multiplying
    by 10^15 (15 is the minimum number of significant digits in a double), and adding a new random
    value in the range [0, 10^15) generated using the Random object for this generator. This is done
    until the precision of the current value is equal to or greater than the precision of the logical
    type. At this point, any extra digits (of there should be at most 14) are rounded off from the
    value, a sign is randomly selected, it is converted to big-endian two's-complement representation,
    and returned.
     */
    private byte[] generateDecimal(LogicalTypes.Decimal decimalLogicalType) {
        BigInteger bigInteger = BigInteger.ZERO;
        final long maxIncrementExclusive = 1_000_000_000_000_000L;
        int precision;
        for (precision = 0; precision < decimalLogicalType.getPrecision(); precision += 15) {
            bigInteger = bigInteger.multiply(BigInteger.valueOf(maxIncrementExclusive));
            long increment = (long) (random.nextDouble() * maxIncrementExclusive);
            bigInteger = bigInteger.add(BigInteger.valueOf(increment));
        }
        bigInteger = bigInteger.divide(
                BigInteger.TEN.pow(precision - decimalLogicalType.getPrecision())
        );
        if (random.nextBoolean()) {
            bigInteger = bigInteger.negate();
        }
        return bigInteger.toByteArray();
    }

    private Float generateFloat(Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Float rangeMinField = getFloatNumberField(
                        RANGE_PROP,
                        RANGE_PROP_MIN,
                        rangeProps
                );
                Float rangeMaxField = getFloatNumberField(
                        RANGE_PROP,
                        RANGE_PROP_MAX,
                        rangeProps
                );
                float rangeMin = Optional.ofNullable(rangeMinField).orElse(-1 * Float.MAX_VALUE);
                float rangeMax = Optional.ofNullable(rangeMaxField).orElse(Float.MAX_VALUE);
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + (random.nextFloat() * (rangeMax - rangeMin));
            }
        }
        return random.nextFloat();
    }

    private Integer generateInt(Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Integer rangeMinField = getIntegerNumberField(RANGE_PROP, RANGE_PROP_MIN, rangeProps);
                Integer rangeMaxField = getIntegerNumberField(RANGE_PROP, RANGE_PROP_MAX, rangeProps);
                int rangeMin = Optional.ofNullable(rangeMinField).orElse(Integer.MIN_VALUE);
                int rangeMax = Optional.ofNullable(rangeMaxField).orElse(Integer.MAX_VALUE);
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + ((int) (random.nextDouble() * (rangeMax - rangeMin)));
            }
        }
        return random.nextInt();
    }

    private Long generateLong(Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Long rangeMinField = getIntegralNumberField(RANGE_PROP, RANGE_PROP_MIN, rangeProps);
                Long rangeMaxField = getIntegralNumberField(RANGE_PROP, RANGE_PROP_MAX, rangeProps);
                long rangeMin = Optional.ofNullable(rangeMinField).orElse(Long.MIN_VALUE);
                long rangeMax = Optional.ofNullable(rangeMaxField).orElse(Long.MAX_VALUE);
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + (((long) (random.nextDouble() * (rangeMax - rangeMin))));
            }
        }
        return random.nextLong();
    }

    private Map<String, Object> generateMap(Schema schema, Map propertiesProp) {
        Map<String, Object> result = new HashMap<>();
        int length = getLengthBounds(propertiesProp).random();
        Object keyProp = propertiesProp.get(KEYS_PROP);
        if (keyProp == null) {
            for (int i = 0; i < length; i++) {
                result.put(generateRandomString(1), generateObject(schema.getValueType()));
            }
        } else if (keyProp instanceof Map) {
            Map keyPropMap = (Map) keyProp;
            if (keyPropMap.containsKey(OPTIONS_PROP)) {
                if (!optionsCache.containsKey(schema)) {
                    optionsCache.put(schema, parseOptions(Schema.create(Schema.Type.STRING), keyPropMap));
                }
                for (int i = 0; i < length; i++) {
                    result.put(generateOption(schema, keyPropMap), generateObject(schema.getValueType()));
                }
            } else {
                for (int i = 0; i < length; i++) {
                    result.put(
                            generateString(schema, keyPropMap),
                            generateObject(schema.getValueType())
                    );
                }
            }
        } else {
            throw new RuntimeException(String.format(
                    "%s prop must be an object",
                    KEYS_PROP
            ));
        }
        return result;
    }

    private Object generateNull() {
        return null;
    }

    private GenericRecord generateRecord(Schema schema) {
        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        for (Schema.Field field : schema.getFields()) {
            builder.set(field, generateObject(field.schema(), field.name()));
        }
        return builder.build();
    }

    private String generateRandomString(int length) {
        /*byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) random.nextInt(128);
        }
        return new String(bytes, StandardCharsets.US_ASCII);*/

        return "string";
    }

    private String generateString(Schema schema, Map propertiesProp) {
        /*Object regexProp = propertiesProp.get(REGEX_PROP);
        String result = generateRandomString(getLengthBounds(propertiesProp).random());
        return prefixAndSuffixString(result, propertiesProp);*/

        return "string";
    }

    private String prefixAndSuffixString(String result, Map propertiesProp) {
        Object prefixProp = propertiesProp.get(PREFIX_PROP);
        if (prefixProp != null && !(prefixProp instanceof String)) {
            throw new RuntimeException(String.format("%s property must be a string", PREFIX_PROP));
        }
        String prefix = prefixProp != null ? (String) prefixProp : "";

        Object suffixProp = propertiesProp.get(SUFFIX_PROP);
        if (suffixProp != null && !(suffixProp instanceof String)) {
            throw new RuntimeException(String.format("%s property must be a string", SUFFIX_PROP));
        }
        String suffix = suffixProp != null ? (String) suffixProp : "";

        return prefix + result + suffix;
    }

    private Object generateUnion1(Schema schema) {
        List<Schema> schemas = schema.getTypes();
        //return generateObject(schemas.get(random.nextInt(schemas.size())));
        for(Schema sc: schemas) {
            if(Schema.Type.NULL.equals(sc.getType())) {
                return generateNull();
            }
        }

        return generateObject(schemas.get(0));
    }

    private Object generateUnion(Schema schema) {
        List<Schema> schemas = schema.getTypes();
        //return generateObject(schemas.get(random.nextInt(schemas.size())));
        for(Schema sc: schemas) {
            if(!Schema.Type.NULL.equals(sc.getType())) {
                return generateObject(sc);
            }
        }

        return generateObject(schemas.get(0));
    }

    private LengthBounds getLengthBounds(Map propertiesProp) {
        return getLengthBounds(propertiesProp.get(LENGTH_PROP));
    }

    private LengthBounds getLengthBounds(Object lengthProp) {
        if (lengthProp == null) {
            return new LengthBounds();
        } else if (lengthProp instanceof Integer) {
            Integer length = (Integer) lengthProp;
            if (length < 0) {
                throw new RuntimeException(String.format(
                        "when given as integral number, %s property cannot be negative",
                        LENGTH_PROP
                ));
            }
            return new LengthBounds(length);
        } else if (lengthProp instanceof Map) {
            Map lengthProps = (Map) lengthProp;
            Integer minLength = getIntegerNumberField(LENGTH_PROP, LENGTH_PROP_MIN, lengthProps);
            Integer maxLength = getIntegerNumberField(LENGTH_PROP, LENGTH_PROP_MAX, lengthProps);
            if (minLength == null && maxLength == null) {
                throw new RuntimeException(String.format(
                        "%s property must contain at least one of '%s' or '%s' fields when given as object",
                        LENGTH_PROP,
                        LENGTH_PROP_MIN,
                        LENGTH_PROP_MAX
                ));
            }
            minLength = minLength != null ? minLength : 0;
            maxLength = maxLength != null ? maxLength : Integer.MAX_VALUE;
            if (minLength < 0) {
                throw new RuntimeException(String.format(
                        "%s field of %s property cannot be negative",
                        LENGTH_PROP_MIN,
                        LENGTH_PROP
                ));
            }
            if (maxLength <= minLength) {
                throw new RuntimeException(String.format(
                        "%s field must be strictly greater than %s field for %s property",
                        LENGTH_PROP_MAX,
                        LENGTH_PROP_MIN,
                        LENGTH_PROP
                ));
            }
            return new LengthBounds(minLength, maxLength);
        } else {
            throw new RuntimeException(String.format(
                    "%s property must either be an integral number or an object, was %s instead",
                    LENGTH_PROP,
                    lengthProp.getClass().getName()
            ));
        }
    }

    private Integer getIntegerNumberField(String property, String field, Map propsMap) {
        Long result = getIntegralNumberField(property, field, propsMap);
        if (result != null && (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE)) {
            throw new RuntimeException(String.format(
                    "'%s' field of %s property must be a valid int for int schemas",
                    field,
                    property
            ));
        }
        return result != null ? result.intValue() : null;
    }

    private Long getIntegralNumberField(String property, String field, Map propsMap) {
        Object result = propsMap.get(field);
        if (result == null || result instanceof Long) {
            return (Long) result;
        } else if (result instanceof Integer) {
            return ((Integer) result).longValue();
        } else {
            throw new RuntimeException(String.format(
                    "'%s' field of %s property must be an integral number, was %s instead",
                    field,
                    property,
                    result.getClass().getName()
            ));
        }
    }

    private Float getFloatNumberField(String property, String field, Map propsMap) {
        Double result = getDecimalNumberField(property, field, propsMap);
        if (result != null && (result > Float.MAX_VALUE || result < -1 * Float.MAX_VALUE)) {
            throw new RuntimeException(String.format(
                    "'%s' field of %s property must be a valid float for float schemas",
                    field,
                    property
            ));
        }
        return result != null ? result.floatValue() : null;
    }

    private Double getDecimalNumberField(String property, String field, Map propsMap) {
        Object result = propsMap.get(field);
        if (result == null || result instanceof Double) {
            return (Double) result;
        } else if (result instanceof Float) {
            return ((Float) result).doubleValue();
        } else if (result instanceof Integer) {
            return ((Integer) result).doubleValue();
        } else if (result instanceof Long) {
            return ((Long) result).doubleValue();
        } else {
            throw new RuntimeException(String.format(
                    "'%s' field of %s property must be a number, was %s instead",
                    field,
                    property,
                    result.getClass().getName()
            ));
        }
    }

    private class LengthBounds {
        public static final int DEFAULT_MIN = 8;
        public static final int DEFAULT_MAX = 16;

        private final int min;
        private final int max;

        public LengthBounds(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public LengthBounds(int exact) {
            this(exact, exact + 1);
        }

        public LengthBounds() {
            this(DEFAULT_MIN, DEFAULT_MAX);
        }

        public int random() {
            return min + random.nextInt(max - min);
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
        }
    }

    private static class IntegralIterator implements Iterator<Object> {
        public enum Type {
            INTEGER, LONG
        }

        private final BigInteger start;
        private final BigInteger restart;
        private final BigInteger step;
        private final Type type;
        private BigInteger current;

        public IntegralIterator(long start, long restart, long step, long initial, long count, Type type) {
            this.start = BigInteger.valueOf(start);
            this.restart = BigInteger.valueOf(restart);
            this.step = BigInteger.valueOf(step);
            this.type = type;
            current = BigInteger.valueOf(initial).subtract(this.start);
            if (count > 0) {
                // This is essentially the following expression when ignoring negative values:
                // current = (count * step) % (restart - start)
                // except BigInteger::mod only operates on positive numbers, so remove and re-add the sign after the modulo.
                current = BigInteger.valueOf(count)
                        .multiply(this.step)
                        .add(current)
                        .abs()
                        .mod(this.restart.subtract(this.start).abs())
                        .multiply(this.step.divide(this.step.abs()));
            }
        }

        @Override
        public Object next() {
            BigInteger result = current.add(start);
            current = current
                    .add(step)
                    .abs()
                    .mod(restart.subtract(start).abs())
                    .multiply(step.divide(step.abs()));

            switch (type) {
                case INTEGER:
                    return result.intValue();
                case LONG:
                    return result.longValue();
                default:
                    throw new RuntimeException(String.format("Unexpected Type: %s", type));
            }
        }

        @Override
        public boolean hasNext() {
            return true;
        }
    }

    private static class DecimalIterator implements Iterator<Object> {
        public enum Type {
            FLOAT, DOUBLE
        }

        private final BigDecimal start;
        private final BigDecimal restart;
        private final BigDecimal modulo;
        private final BigDecimal step;
        private final Type type;
        private BigDecimal current;

        public DecimalIterator(double start, double restart, double step, double initial, long count, Type type) {
            this.start = BigDecimal.valueOf(start);
            this.restart = BigDecimal.valueOf(restart);
            this.modulo = this.restart.subtract(this.start);
            this.step = BigDecimal.valueOf(step);
            this.type = type;
            current = BigDecimal.valueOf(initial).subtract(this.start);
            if (count > 0) {
                current = BigDecimal.valueOf(count)
                        .multiply(this.step)
                        .add(current)
                        .remainder(this.modulo);
            }
        }

        @Override
        public Object next() {
            BigDecimal result = current.add(start);
            current = current
                    .add(step)
                    .remainder(modulo);
            switch (type) {
                case FLOAT:
                    return result.floatValue();
                case DOUBLE:
                    return result.doubleValue();
                default:
                    throw new RuntimeException(String.format("Unexpected Type: %s", type));
            }
        }

        @Override
        public boolean hasNext() {
            return true;
        }
    }

    private static class BooleanIterator implements Iterator<Object> {
        private boolean current;

        public BooleanIterator(boolean start) {
            current = start;
        }

        @Override
        public Boolean next() {
            boolean result = current;
            current = !current;
            return result;
        }

        @Override
        public boolean hasNext() {
            return true;
        }
    }

    //
    private Object generateObject(Schema schema, String fieldName) {

        Map propertiesProp = getProperties(schema).orElse(Collections.emptyMap());
        if (propertiesProp.containsKey(OPTIONS_PROP)) {
            return generateOption(schema, propertiesProp);
        }
        if (propertiesProp.containsKey(ITERATION_PROP)) {
            return generateIteration(schema, propertiesProp);
        }

        switch (schema.getType()) {
            case ARRAY:
                return generateArray(schema, fieldName, propertiesProp);
            case BOOLEAN:
                return generateBoolean(propertiesProp);
            case BYTES:
                return generateBytes(schema, propertiesProp);
            case DOUBLE:
                return generateDouble(fieldName, propertiesProp);
            case ENUM:
                return generateEnumSymbol(schema);
            case FIXED:
                return generateFixed(schema);
            case FLOAT:
                return generateFloat(fieldName, propertiesProp);
            case INT:
                return generateInt(fieldName, propertiesProp);
            case LONG:
                return generateLong(fieldName, propertiesProp);
            case MAP:
                return generateMap(schema, propertiesProp);
            case NULL:
                return generateNull();
            case RECORD:
                return generateRecord(schema);
            case STRING:
                return generateString(schema, fieldName, propertiesProp);
            case UNION:
                return generateUnion(schema, fieldName);
            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private Collection<Object> generateArray(Schema schema, String fieldName, Map propertiesProp) {
        //int length = getLengthBounds(propertiesProp).random();
        int length = 1;
        Collection<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(generateObject(schema.getElementType(), fieldName));
        }
        return result;
    }

    private Object generateUnion(Schema schema, String fieldName) {
        List<Schema> schemas = schema.getTypes();
        //return generateObject(schemas.get(random.nextInt(schemas.size())));
        for(Schema sc: schemas) {
            if(!Schema.Type.NULL.equals(sc.getType())) {
                return generateObject(sc, fieldName);
            }
        }

        return generateObject(schemas.get(0));
    }

    private Double generateDouble(String fieldName, Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Double rangeMinField = getDecimalNumberField(RANGE_PROP, RANGE_PROP_MIN, rangeProps);
                Double rangeMaxField = getDecimalNumberField(RANGE_PROP, RANGE_PROP_MAX, rangeProps);
                double rangeMin = rangeMinField != null ? rangeMinField : -1 * Double.MAX_VALUE;
                double rangeMax = rangeMaxField != null ? rangeMaxField : Double.MAX_VALUE;
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + (random.nextDouble() * (rangeMax - rangeMin));
            } else {
                throw new RuntimeException(String.format(
                        "%s property must be an object",
                        RANGE_PROP
                ));
            }
        }

        return 99.99;
    }

    private Float generateFloat(String fieldName, Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Float rangeMinField = getFloatNumberField(
                        RANGE_PROP,
                        RANGE_PROP_MIN,
                        rangeProps
                );
                Float rangeMaxField = getFloatNumberField(
                        RANGE_PROP,
                        RANGE_PROP_MAX,
                        rangeProps
                );
                float rangeMin = Optional.ofNullable(rangeMinField).orElse(-1 * Float.MAX_VALUE);
                float rangeMax = Optional.ofNullable(rangeMaxField).orElse(Float.MAX_VALUE);
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + (random.nextFloat() * (rangeMax - rangeMin));
            }
        }
        return 19.99f;
    }

    private Integer generateInt(String fieldName, Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Integer rangeMinField = getIntegerNumberField(RANGE_PROP, RANGE_PROP_MIN, rangeProps);
                Integer rangeMaxField = getIntegerNumberField(RANGE_PROP, RANGE_PROP_MAX, rangeProps);
                int rangeMin = Optional.ofNullable(rangeMinField).orElse(Integer.MIN_VALUE);
                int rangeMax = Optional.ofNullable(rangeMaxField).orElse(Integer.MAX_VALUE);
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + ((int) (random.nextDouble() * (rangeMax - rangeMin)));
            }
        }
        return 987654321;
    }

    private Long generateLong(String fieldName, Map propertiesProp) {
        Object rangeProp = propertiesProp.get(RANGE_PROP);
        if (rangeProp != null) {
            if (rangeProp instanceof Map) {
                Map rangeProps = (Map) rangeProp;
                Long rangeMinField = getIntegralNumberField(RANGE_PROP, RANGE_PROP_MIN, rangeProps);
                Long rangeMaxField = getIntegralNumberField(RANGE_PROP, RANGE_PROP_MAX, rangeProps);
                long rangeMin = Optional.ofNullable(rangeMinField).orElse(Long.MIN_VALUE);
                long rangeMax = Optional.ofNullable(rangeMaxField).orElse(Long.MAX_VALUE);
                if (rangeMin >= rangeMax) {
                    throw new RuntimeException(String.format(
                            "'%s' field must be strictly less than '%s' field in %s property",
                            RANGE_PROP_MIN,
                            RANGE_PROP_MAX,
                            RANGE_PROP
                    ));
                }
                return rangeMin + (((long) (random.nextDouble() * (rangeMax - rangeMin))));
            }
        }
        return 987654321l;
    }

    private String generateString(Schema schema, String fieldName, Map propertiesProp) {
        if(fieldName == null) {
            return "string";
        }

        String value = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName);
        if(value.endsWith("_I_D")) {
            value = value.replace("_I_D", "_ID");
        }

        if(value.endsWith("_T_S")) {
            value = value.replace("_T_S", "_TS");
        }

        if(value.endsWith("_IND")) {
            value = "Y";
        }

        if(value.endsWith("_DT_TM") || value.endsWith("_DT") || value.endsWith("_TM") || value.endsWith("_TS")) {
            value = "2021-03-16T12:21:47.403Z";
        }

        return value;

    }
}
