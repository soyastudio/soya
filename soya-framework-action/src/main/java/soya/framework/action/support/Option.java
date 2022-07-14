package soya.framework.action.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Option implements Cloneable, Serializable {

    public static final class Builder {

        /** The name of the option */
        private String option;

        /** description of the option */
        private String description;

        /** The long representation of the option */
        private String longOption;

        /** The name of the argument for this option */
        private String argName;

        /** specifies whether this option is required to be present */
        private boolean required;

        /** specifies whether the argument value of this Option is optional */
        private boolean optionalArg;

        /** The number of argument values this option can have */
        private int argCount = UNINITIALIZED;

        /** The type of this Option */
        private Class<?> type = String.class;

        /** The character that is the value separator */
        private char valueSeparator;

        /**
         * Constructs a new {@code Builder} with the minimum required parameters for an {@code Option} instance.
         *
         * @param option short representation of the option
         * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}
         */
        private Builder(final String option) throws IllegalArgumentException {
            option(option);
        }

        /**
         * Sets the display name for the argument value.
         *
         * @param argName the display name for the argument value.
         * @return this builder, to allow method chaining
         */
        public Builder argName(final String argName) {
            this.argName = argName;
            return this;
        }

        /**
         * Constructs an Option with the values declared by this {@link Builder}.
         *
         * @return the new {@link Option}
         * @throws IllegalArgumentException if neither {@code opt} or {@code longOpt} has been set
         */
        public Option build() {
            if (option == null && longOption == null) {
                throw new IllegalArgumentException("Either opt or longOpt must be specified");
            }
            return new Option(this);
        }

        /**
         * Sets the description for this option.
         *
         * @param description the description of the option.
         * @return this builder, to allow method chaining
         */
        public Builder desc(final String description) {
            this.description = description;
            return this;
        }

        /**
         * Indicates that the Option will require an argument.
         *
         * @return this builder, to allow method chaining
         */
        public Builder hasArg() {
            return hasArg(true);
        }

        /**
         * Indicates if the Option has an argument or not.
         *
         * @param hasArg specifies whether the Option takes an argument or not
         * @return this builder, to allow method chaining
         */
        public Builder hasArg(final boolean hasArg) {
            // set to UNINITIALIZED when no arg is specified to be compatible with OptionBuilder
            argCount = hasArg ? 1 : Option.UNINITIALIZED;
            return this;
        }

        /**
         * Indicates that the Option can have unlimited argument values.
         *
         * @return this builder, to allow method chaining
         */
        public Builder hasArgs() {
            argCount = Option.UNLIMITED_VALUES;
            return this;
        }

        /**
         * Sets the long name of the Option.
         *
         * @param longOpt the long name of the Option
         * @return this builder, to allow method chaining
         */
        public Builder longOpt(final String longOpt) {
            this.longOption = longOpt;
            return this;
        }

        /**
         * Sets the number of argument values the Option can take.
         *
         * @param numberOfArgs the number of argument values
         * @return this builder, to allow method chaining
         */
        public Builder numberOfArgs(final int numberOfArgs) {
            this.argCount = numberOfArgs;
            return this;
        }

        /**
         * Sets the name of the Option.
         *
         * @param option the name of the Option
         * @return this builder, to allow method chaining
         * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}
         * @since 1.5.0
         */
        public Builder option(final String option) throws IllegalArgumentException {
            this.option = OptionValidator.validate(option);
            return this;
        }

        /**
         * Sets whether the Option can have an optional argument.
         *
         * @param isOptional specifies whether the Option can have an optional argument.
         * @return this builder, to allow method chaining
         */
        public Builder optionalArg(final boolean isOptional) {
            this.optionalArg = isOptional;
            return this;
        }

        /**
         * Marks this Option as required.
         *
         * @return this builder, to allow method chaining
         */
        public Builder required() {
            return required(true);
        }

        /**
         * Sets whether the Option is mandatory.
         *
         * @param required specifies whether the Option is mandatory
         * @return this builder, to allow method chaining
         */
        public Builder required(final boolean required) {
            this.required = required;
            return this;
        }

        /**
         * Sets the type of the Option.
         *
         * @param type the type of the Option
         * @return this builder, to allow method chaining
         */
        public Builder type(final Class<?> type) {
            this.type = type;
            return this;
        }

        /**
         * The Option will use '=' as a means to separate argument value.
         *
         * @return this builder, to allow method chaining
         */
        public Builder valueSeparator() {
            return valueSeparator('=');
        }

        /**
         * The Option will use {@code sep} as a means to separate argument values.
         * <p>
         * <b>Example:</b>
         * </p>
         *
         * <pre>
         * Option opt = Option.builder("D").hasArgs().valueSeparator('=').build();
         * Options options = new Options();
         * options.addOption(opt);
         * String[] args = {"-Dkey=value"};
         * CommandLineParser parser = new DefaultParser();
         * CommandLine line = parser.parse(options, args);
         * String propertyName = line.getOptionValues("D")[0]; // will be "key"
         * String propertyValue = line.getOptionValues("D")[1]; // will be "value"
         * </pre>
         *
         * @param sep The value separator.
         * @return this builder, to allow method chaining
         */
        public Builder valueSeparator(final char sep) {
            valueSeparator = sep;
            return this;
        }
    }

    public static final int UNINITIALIZED = -1;

    public static final int UNLIMITED_VALUES = -2;

    private static final long serialVersionUID = 1L;

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(final String option) {
        return new Builder(option);
    }

    private final String option;

    private String longOption;

    private String argName;

    private String description;

    private boolean required;

    private boolean optionalArg;

    private int argCount = UNINITIALIZED;

    private Class<?> type = String.class;

    private List<String> values = new ArrayList<>();

    private char valueSeparator;

    private Option(final Builder builder) {
        this.argName = builder.argName;
        this.description = builder.description;
        this.longOption = builder.longOption;
        this.argCount = builder.argCount;
        this.option = builder.option;
        this.optionalArg = builder.optionalArg;
        this.required = builder.required;
        this.type = builder.type;
        this.valueSeparator = builder.valueSeparator;
    }

    public Option(final String option, final boolean hasArg, final String description) throws IllegalArgumentException {
        this(option, null, hasArg, description);
    }

    public Option(final String option, final String description) throws IllegalArgumentException {
        this(option, null, false, description);
    }

    public Option(final String option, final String longOption, final boolean hasArg, final String description) throws IllegalArgumentException {
        // ensure that the option is valid
        this.option = OptionValidator.validate(option);
        this.longOption = longOption;

        // if hasArg is set then the number of arguments is 1
        if (hasArg) {
            this.argCount = 1;
        }

        this.description = description;
    }

    boolean acceptsArg() {
        return (hasArg() || hasArgs() || hasOptionalArg()) && (argCount <= 0 || values.size() < argCount);
    }

    private void add(final String value) {
        if (!acceptsArg()) {
            throw new RuntimeException("Cannot add value, list full.");
        }

        // store value
        values.add(value);
    }

    void addValueForProcessing(final String value) {
        if (argCount == UNINITIALIZED) {
            throw new RuntimeException("NO_ARGS_ALLOWED");
        }
        processValue(value);
    }

    void clearValues() {
        values.clear();
    }

    @Override
    public Object clone() {
        try {
            final Option option = (Option) super.clone();
            option.values = new ArrayList<>(values);
            return option;
        } catch (final CloneNotSupportedException cnse) {
            throw new RuntimeException("A CloneNotSupportedException was thrown: " + cnse.getMessage());
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Option)) {
            return false;
        }
        final Option other = (Option) obj;
        return Objects.equals(longOption, other.longOption) && Objects.equals(option, other.option);
    }

    public String getArgName() {
        return argName;
    }

    public int getArgs() {
        return argCount;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return getKey().charAt(0);
    }

    String getKey() {
        // if 'opt' is null, then it is a 'long' option
        return option == null ? longOption : option;
    }

    public String getLongOpt() {
        return longOption;
    }

    public String getOpt() {
        return option;
    }

    public Object getType() {
        return type;
    }

    public String getValue() {
        return hasNoValues() ? null : values.get(0);
    }

    public String getValue(final int index) throws IndexOutOfBoundsException {
        return hasNoValues() ? null : values.get(index);
    }

    public String getValue(final String defaultValue) {
        final String value = getValue();

        return value != null ? value : defaultValue;
    }

    public String[] getValues() {
        return hasNoValues() ? null : values.toArray(new String[values.size()]);
    }

    public char getValueSeparator() {
        return valueSeparator;
    }

    public List<String> getValuesList() {
        return values;
    }

    public boolean hasArg() {
        return argCount > 0 || argCount == UNLIMITED_VALUES;
    }

    public boolean hasArgName() {
        return argName != null && !argName.isEmpty();
    }

    public boolean hasArgs() {
        return argCount > 1 || argCount == UNLIMITED_VALUES;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longOption, option);
    }

    public boolean hasLongOpt() {
        return longOption != null;
    }

    private boolean hasNoValues() {
        return values.isEmpty();
    }

    public boolean hasOptionalArg() {
        return optionalArg;
    }

    public boolean hasValueSeparator() {
        return valueSeparator > 0;
    }

    public boolean isRequired() {
        return required;
    }

    private void processValue(String value) {
        // this Option has a separator character
        if (hasValueSeparator()) {
            // get the separator character
            final char sep = getValueSeparator();

            // store the index for the value separator
            int index = value.indexOf(sep);

            // while there are more value separators
            while (index != -1) {
                // next value to be added
                if (values.size() == argCount - 1) {
                    break;
                }

                // store
                add(value.substring(0, index));

                // parse
                value = value.substring(index + 1);

                // get new index
                index = value.indexOf(sep);
            }
        }

        // store the actual value or the last value that has been parsed
        add(value);
    }

    boolean requiresArg() {
        if (optionalArg) {
            return false;
        }
        if (argCount == UNLIMITED_VALUES) {
            return values.isEmpty();
        }
        return acceptsArg();
    }

    public void setArgName(final String argName) {
        this.argName = argName;
    }

    public void setArgs(final int num) {
        this.argCount = num;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setLongOpt(final String longOpt) {
        this.longOption = longOpt;
    }

    public void setOptionalArg(final boolean optionalArg) {
        this.optionalArg = optionalArg;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public void setType(final Class<?> type) {
        this.type = type;
    }

    public void setValueSeparator(final char sep) {
        this.valueSeparator = sep;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder().append("[ option: ");

        buf.append(option);

        if (longOption != null) {
            buf.append(" ").append(longOption);
        }

        buf.append(" ");

        if (hasArgs()) {
            buf.append("[ARG...]");
        } else if (hasArg()) {
            buf.append(" [ARG]");
        }

        buf.append(" :: ").append(description);

        if (type != null) {
            buf.append(" :: ").append(type);
        }

        buf.append(" ]");

        return buf.toString();
    }
}
