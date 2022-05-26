package soya.framework.commandline.oas.swagger;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class Swagger {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public enum HttpMethod {
        get, put, post, delete, patch, options, head
    }

    private String swagger = "2.0";
    private InfoObject info;
    private String host;
    private String basePath;
    private String[] schemas;
    private String[] consumes;
    private String[] produces;
    private Map<String, PathItemObject> paths = new LinkedHashMap<>();

    private List<ParameterObject> parameters;
    private ResponsesDefinitionsObject responses;
    private SecurityDefinitionsObject securityDefinitions;
    private SecurityRequirementObject[] security;

    private TagObject[] tags;

    public String getSwagger() {
        return swagger;
    }

    public InfoObject getInfo() {
        return info;
    }

    public String getHost() {
        return host;
    }

    public String getBasePath() {
        return basePath;
    }

    public String[] getSchemas() {
        return schemas;
    }

    public String[] getConsumes() {
        return consumes;
    }

    public String[] getProduces() {
        return produces;
    }

    public Map<String, PathItemObject> getPaths() {
        return paths;
    }

    public List<ParameterObject> getParameters() {
        return parameters;
    }

    public ResponsesDefinitionsObject getResponses() {
        return responses;
    }

    public SecurityDefinitionsObject getSecurityDefinitions() {
        return securityDefinitions;
    }

    public SecurityRequirementObject[] getSecurity() {
        return security;
    }

    public TagObject[] getTags() {
        return tags;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public String toYaml() {
        return null;
    }

    public static Swagger fromJson(String json) {
        return GSON.fromJson(json, Swagger.class);
    }

    public static Swagger fromJson(InputStream inputStream) {
        return GSON.fromJson(new InputStreamReader(inputStream), Swagger.class);
    }

    public static Swagger fromJson(Reader reader) {
        return GSON.fromJson(reader, Swagger.class);
    }

    public static SwaggerBuilder builder() {
        return new SwaggerBuilder();
    }

    public static class SwaggerBuilder {
        private String swagger = "2.0";
        private InfoObject info = new InfoObject();
        private String host;
        private String basePath;
        private Set<String> schemas = new LinkedHashSet<>();
        private Set<String> consumes = new LinkedHashSet<>();
        private Set<String> produces = new LinkedHashSet<>();

        private Map<String, PathItemObject> paths = new LinkedHashMap<>();
        private ParameterObject parameters;
        private ResponsesDefinitionsObject responses;
        private SecurityDefinitionsObject securityDefinitions;
        private SecurityRequirementObject[] security;
        private List<TagObject> tags = new ArrayList<>();

        private SwaggerBuilder() {
        }

        public SwaggerBuilder title(String title) {
            this.info.title = title;
            return this;
        }

        public SwaggerBuilder description(String description) {
            this.info.description = description;
            return this;
        }

        public SwaggerBuilder termsOfService(String termsOfService) {
            this.info.termsOfService = termsOfService;
            return this;
        }

        public SwaggerBuilder contactName(String name) {
            this.info.contact.name = name;
            return this;
        }

        public SwaggerBuilder contactUrl(String url) {
            this.info.contact.url = url;
            return this;
        }

        public SwaggerBuilder contactEmail(String email) {
            this.info.contact.email = email;
            return this;
        }

        public SwaggerBuilder licenseName(String licenseName) {
            this.info.licence.name = licenseName;
            return this;
        }

        public SwaggerBuilder licenseUrl(String licenseUrl) {
            this.info.licence.url = licenseUrl;
            return this;
        }

        public SwaggerBuilder host(String host) {
            this.host = host;
            return this;
        }

        public SwaggerBuilder basePath(String basePath) {
            this.basePath = basePath;
            return this;
        }

        public SwaggerBuilder addSchema(String schema) {
            this.schemas.add(schema);
            return this;
        }

        public SwaggerBuilder addConsume(String... consume) {
            if (consume != null) {
                for (String c : consume) {
                    this.consumes.add(c);
                }
            }
            return this;
        }

        public SwaggerBuilder addProduce(String... produce) {
            if (produce != null) {
                for (String p : produce) {
                    this.produces.add(p);
                }
            }
            return this;
        }

        public PathBuilder pathBuilder(String path, String method, String operationId) {
            return new PathBuilder(this, path, HttpMethod.valueOf(method.toLowerCase()), operationId);
        }

        public PathBuilder get(String path, String operationId) {
            return new PathBuilder(this, path, HttpMethod.get, operationId);
        }

        public PathBuilder post(String path, String operationId) {
            return new PathBuilder(this, path, HttpMethod.post, operationId);
        }

        public PathBuilder put(String path, String operationId) {
            return new PathBuilder(this, path, HttpMethod.put, operationId);
        }

        public PathBuilder delete(String path, String operationId) {
            return new PathBuilder(this, path, HttpMethod.delete, operationId);
        }

        public PathBuilder options(String path, String operationId) {
            return new PathBuilder(this, path, HttpMethod.options, operationId);
        }

        public PathBuilder head(String path, String operationId) {
            return new PathBuilder(this, path, HttpMethod.head, operationId);
        }

        public PathBuilder patch(String path, String operationId) {
            return new PathBuilder(this, path, HttpMethod.patch, operationId);
        }

        public SwaggerBuilder addTag(TagObject tagObject) {
            tags.add(tagObject);
            return this;
        }

        public Swagger build() {
            Swagger api = new Swagger();

            api.swagger = this.swagger;
            api.info = this.info;
            api.host = this.host;
            api.basePath = this.basePath;

            api.schemas = this.schemas.toArray(new String[this.schemas.size()]);

            if(!consumes.isEmpty()){
                api.consumes = this.consumes.toArray(new String[consumes.size()]);
            }

            if(!produces.isEmpty()) {
                api.produces = this.produces.toArray(new String[produces.size()]);
            }

            api.paths = this.paths;

            if(!tags.isEmpty()) {
                api.tags = tags.toArray(new TagObject[tags.size()]);
            }

            return api;
        }
    }

    public static class PathBuilder {
        private SwaggerBuilder owner;
        private String path;

        private HttpMethod httpMethod;
        private OperationObject operation;

        protected List<ParameterObject> parameterObjects = new ArrayList<>();

        private PathBuilder(SwaggerBuilder owner, String path, HttpMethod httpMethod, String operationId) {
            this.owner = owner;
            this.path = path;
            this.httpMethod = httpMethod;

            this.operation = new OperationObject(operationId);
        }

        public SimpleParameterBuilder parameterBuilder(String name, String in, String description) {
            return new SimpleParameterBuilder(this, name, in, description);
        }

        public BodyParameterBuilder bodyParameterBuilder(String name, String description) {
            return new BodyParameterBuilder(this, name, description);
        }

        public PathBuilder addTag(String name) {
            operation.tags.add(name);
            return this;
        }

        public PathBuilder consumes(String... consume) {
            if(consume != null) {
                for(String c: consume) {
                    operation.consumes.add(c);
                }
            }
            return this;
        }

        public PathBuilder produces(String... produces) {
            if(produces != null) {
                for(String p: produces) {
                    operation.produces.add(p);
                }
            }
            return this;
        }

        public SwaggerBuilder build() {
            PathItemObject pathItem = owner.paths.get(path);
            if (pathItem == null) {
                pathItem = new PathItemObject();

                owner.paths.put(path, pathItem);
            }

            List<ParameterObject> pathParams = new ArrayList<>();
            if(pathItem.parameters != null) {
                pathParams.addAll(Arrays.asList(pathItem.parameters));
            }

            for (ParameterObject e : parameterObjects) {
                if (e.forPath) {
                    pathParams.add(e);
                } else {
                    operation.parameters.add(e);
                }
            }
            if(!pathParams.isEmpty()) {
                pathItem.parameters = pathParams.toArray(new ParameterObject[pathParams.size()]);
            }

            if (HttpMethod.get.equals(httpMethod)) {
                pathItem.get = operation;

            } else if (HttpMethod.post.equals(httpMethod)) {
                pathItem.post = operation;

            } else if (HttpMethod.put.equals(httpMethod)) {
                pathItem.put = operation;

            } else if (HttpMethod.delete.equals(httpMethod)) {
                pathItem.delete = operation;

            } else if (HttpMethod.patch.equals(httpMethod)) {
                pathItem.patch = operation;

            } else if (HttpMethod.head.equals(httpMethod)) {
                pathItem.head = operation;

            } else if (HttpMethod.options.equals(httpMethod)) {
                pathItem.options = operation;

            }

            return owner;
        }
    }

    static abstract class ParameterBuilder {
        private Swagger.PathBuilder owner;

        protected Swagger.ParameterObject parameterObject;

        protected ParameterBuilder(Swagger.PathBuilder owner, String name, String in, String description) {
            this.owner = owner;
            this.parameterObject = new Swagger.ParameterObject(name, in, description);
        }

        public final Swagger.PathBuilder build() {
            owner.parameterObjects.add(parameterObject);
            return owner;
        }
    }

    public static class SimpleParameterBuilder extends ParameterBuilder {

        protected SimpleParameterBuilder(Swagger.PathBuilder owner, String name, String in, String description) {
            super(owner, name, in, description);
            if("query".equals(in)) {
                parameterObject.required = true;
            }
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

    public static class BodyParameterBuilder extends ParameterBuilder {

        protected BodyParameterBuilder(Swagger.PathBuilder owner, String name, String description) {
            super(owner, name, "body", description);
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

    public static class InfoObject {
        private String title;
        private String description;
        private String termsOfService;
        private ContactObject contact = new ContactObject();
        private LicenceObject licence = new LicenceObject();
        private String version;

    }

    public static class ContactObject {
        private String name;
        private String url;
        private String email;

    }

    public static class LicenceObject {
        private String name;
        private String url;

    }

    public static class PathItemObject {

        private OperationObject get;
        private OperationObject put;
        private OperationObject post;
        private OperationObject delete;
        private OperationObject options;
        private OperationObject head;
        private OperationObject patch;

        private ParameterObject[] parameters;

        public OperationObject getOperation() {
            return get;
        }

        public OperationObject putOperation() {
            return put;
        }

        public OperationObject postOperation() {
            return post;
        }

        public OperationObject deleteOperation() {
            return delete;
        }

        public OperationObject optionsOperation() {
            return options;
        }

        public OperationObject headOperation() {
            return head;
        }

        public OperationObject patchOperation() {
            return patch;
        }

        public ParameterObject[] getParameters() {
            return parameters;
        }
    }

    public static class OperationObject {
        private Set<String> tags = new LinkedHashSet<>();
        private String summary;
        private String description;
        private String operationId;
        private Set<String> consumes = new LinkedHashSet<>();
        private Set<String> produces = new LinkedHashSet<>();

        private List<ParameterObject> parameters = new ArrayList<>();

        private Map<String, ResponsesDefinitionsObject> responses = new LinkedHashMap<>();

        OperationObject(String operationId) {
            this.operationId = operationId;
        }

        public Set<String> getTags() {
            return tags;
        }

        public String getSummary() {
            return summary;
        }

        public String getDescription() {
            return description;
        }

        public String getOperationId() {
            return operationId;
        }

        public Set<String> getConsumes() {
            return consumes;
        }

        public Set<String> getProduces() {
            return produces;
        }

        public List<ParameterObject> getParameters() {
            return parameters;
        }
    }

    public static class ParameterObject {
        protected transient boolean forPath;

        private final String name;
        private final String in;

        protected String description;
        protected boolean required;

        protected String type;
        protected String format;
        protected boolean allowEmptyValue;
        protected ItemsObject items;
        protected String collectionFormat;
        // protected String defaultValue;
        protected Double maximum;
        protected Boolean exclusiveMaximum;
        protected Double minimum;
        protected Boolean getExclusiveMinimum;
        protected Integer maxLength;
        protected Integer minLength;
        protected String pattern;
        protected Integer maxItems;
        protected Integer minItems;
        protected Boolean uniqueItems;
        // protected String[] enum;
        protected Double multipleOf;

        public ParameterObject(String name, String in, String description) {
            this.name = name;
            this.in = in;
            this.description = description;
        }


    }

    public static class ItemsObject {
        private String type;
    }

    public static class ResponsesDefinitionsObject {

    }

    public static class SecurityDefinitionsObject {

    }

    public static class SecurityRequirementObject {

    }

    public static class TagObject {
        private String name;
        private String description;

        private TagObject() {
        }

        public static TagObject instance() {
            return new TagObject();
        }

        public TagObject name(String name) {
            this.name = name;
            return this;
        }

        public TagObject description(String description) {
            this.description = description;
            return this;
        }

    }

    public static class ExternalDocumentObject {

    }

    private static String commandName(String operationId) {
        String token = operationId;
        int index = operationId.lastIndexOf("-");
        if (index > 0) {
            token = token.substring(index + 1);
        }

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, token);
    }


}
