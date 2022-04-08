package soya.framework.oas.swagger;

public interface ParameterBuilder {

    abstract class AbstractParameterBuilder {
        private Swagger.PathBuilder owner;

        protected Swagger.ParameterObject parameterObject;

        protected AbstractParameterBuilder(Swagger.PathBuilder owner, String name, String in, String description, boolean required) {
            this.owner = owner;
            this.parameterObject = new Swagger.ParameterObject(in, name, description, required);
        }

        public final Swagger.PathBuilder build() {
            owner.parameterObjects.add(parameterObject);
            return owner;
        }
    }

    static SimpleParameterBuilder pathParameter(Swagger.PathBuilder owner, String name, String description) {
        SimpleParameterBuilder builder = new SimpleParameterBuilder(owner, name, "path", description, true);
        return builder;
    }

    static SimpleParameterBuilder queryParameter(Swagger.PathBuilder owner, String name, String description) {
        return new SimpleParameterBuilder(owner, name, "query", description, false);
    }

    static SimpleParameterBuilder headerParameter(Swagger.PathBuilder owner, String name, String description) {
        return new SimpleParameterBuilder(owner, name, "header", description, false);
    }

    class SimpleParameterBuilder extends AbstractParameterBuilder {

        protected SimpleParameterBuilder(Swagger.PathBuilder owner, String name, String in, String description, boolean required) {
            super(owner, name, in, description, required);
        }

        // For path level
        public SimpleParameterBuilder forPath() {
            parameterObject.forPath = true;
            return this;
        }

        public SimpleParameterBuilder required() {
            parameterObject.required = true;
            return this;
        }

        public SimpleParameterBuilder description(String description) {
            parameterObject.description = description;
            return this;
        }

        public SimpleParameterBuilder setType(String type) {
            parameterObject.type = type;
            return this;
        }

        public SimpleParameterBuilder setFormat(String format) {
            parameterObject.format = format;
            return this;
        }

        public SimpleParameterBuilder setAllowEmptyValue(boolean allowEmptyValue) {
            parameterObject.allowEmptyValue = allowEmptyValue;
            return this;
        }

        public SimpleParameterBuilder setItems(Swagger.ItemsObject items) {
            parameterObject.items = items;
            return this;
        }

        public SimpleParameterBuilder setCollectionFormat(String collectionFormat) {
            parameterObject.collectionFormat = collectionFormat;
            return this;
        }

        public SimpleParameterBuilder setMaximum(Double maximum) {
            parameterObject.maximum = maximum;
            return this;
        }

        public SimpleParameterBuilder setExclusiveMaximum(Boolean exclusiveMaximum) {
            parameterObject.exclusiveMaximum = exclusiveMaximum;
            return this;
        }

        public SimpleParameterBuilder setMinimum(Double minimum) {
            parameterObject.minimum = minimum;
            return this;
        }

        public SimpleParameterBuilder setGetExclusiveMinimum(Boolean getExclusiveMinimum) {
            parameterObject.getExclusiveMinimum = getExclusiveMinimum;
            return this;
        }

        public SimpleParameterBuilder setMaxLength(Integer maxLength) {
            parameterObject.maxLength = maxLength;
            return this;
        }

        public SimpleParameterBuilder setMinLength(Integer minLength) {
            parameterObject.minLength = minLength;
            return this;
        }

        public SimpleParameterBuilder setPattern(String pattern) {
            parameterObject.pattern = pattern;
            return this;
        }

        public SimpleParameterBuilder setMaxItems(Integer maxItems) {
            parameterObject.maxItems = maxItems;
            return this;
        }

        public SimpleParameterBuilder setMinItems(Integer minItems) {
            parameterObject.minItems = minItems;
            return this;
        }

        public SimpleParameterBuilder setUniqueItems(Boolean uniqueItems) {
            parameterObject.uniqueItems = uniqueItems;
            return this;
        }

        public SimpleParameterBuilder setMultipleOf(Double multipleOf) {
            parameterObject.multipleOf = multipleOf;
            return this;
        }
    }

    class BodyParameterBuilder extends AbstractParameterBuilder {

        protected BodyParameterBuilder(Swagger.PathBuilder owner, String name, String in, String description, boolean required) {
            super(owner, name, in, description, required);
        }

        // For path level
        public BodyParameterBuilder forPath() {
            parameterObject.forPath = true;
            return this;
        }

        public BodyParameterBuilder required() {
            parameterObject.required = true;
            return this;
        }

        public BodyParameterBuilder description(String description) {
            parameterObject.description = description;
            return this;
        }
    }
}
