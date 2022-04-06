package soya.framework.dispatch.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Swagger {

    private String swagger = "2.0";
    private InfoObject info;
    private String host;
    private String basePath;
    private String[] schemas;
    private String[] consumes;
    private String[] produces;

    private PathsObject paths;
    private ParametersDefinitionsObject parameters;
    private ResponsesDefinitionsObject responses;
    private SecurityDefinitionsObject securityDefinitions;
    private SecurityRequirementObject[] security;
    private TagObject[] tags;

    public String toJson() {
        return null;
    }

    public String toYaml() {
        return null;
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
        private ParametersDefinitionsObject parameters;
        private ResponsesDefinitionsObject responses;
        private SecurityDefinitionsObject securityDefinitions;
        private SecurityRequirementObject[] security;
        private TagObject[] tags;

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

        public SwaggerBuilder addConsume(String consume) {
            this.consumes.add(consume);
            return this;
        }

        public SwaggerBuilder addProduce(String produce) {
            this.produces.add(produce);
            return this;
        }

        public SwaggerBuilder addPath(PathBuilder pathBuilder) {

            return this;
        }

        public PathBuilder get(String path, String operationId) {
            return new PathBuilder(this, path, "get", operationId);
        }

        public PathBuilder post(String path, String operationId) {
            return new PathBuilder(this, path, "post", operationId);
        }

        public PathBuilder put(String path, String operationId) {
            return new PathBuilder(this, path, "put", operationId);
        }

        public PathBuilder delete(String path, String operationId) {
            return new PathBuilder(this, path, "delete", operationId);
        }

        public PathBuilder options(String path, String operationId) {
            return new PathBuilder(this, path, "options", operationId);
        }

        public PathBuilder head(String path, String operationId) {
            return new PathBuilder(this, path, "head", operationId);
        }

        public PathBuilder patch(String path, String operationId) {
            return new PathBuilder(this, path, "patch", operationId);
        }

        public SwaggerBuilder addPath() {
            return this;
        }

        public Swagger create() {
            Swagger swagger = new Swagger();
            swagger.swagger = this.swagger;
            swagger.info = this.info;
            swagger.host = this.host;
            swagger.basePath = this.basePath;
            swagger.schemas = this.schemas.toArray(new String[this.schemas.size()]);
            swagger.consumes = this.consumes.toArray(new String[this.consumes.size()]);
            swagger.produces = this.produces.toArray(new String[this.produces.size()]);

            return swagger;
        }
    }

    public static class PathBuilder {
        private SwaggerBuilder swaggerBuilder;
        private String path;
        private String httpMethod;
        private String operationId;

        private PathBuilder(SwaggerBuilder swaggerBuilder, String path, String httpMethod, String operationId) {
            this.swaggerBuilder = swaggerBuilder;
            this.path = path;
            this.httpMethod = httpMethod;
            this.operationId = operationId;
        }
    }

    static class InfoObject {
        private String title;
        private String description;
        private String termsOfService;
        private ContactObject contact = new ContactObject();
        private LicenceObject licence = new LicenceObject();
        private String version;

    }

    static class ContactObject {
        private String name;
        private String url;
        private String email;

    }

    static class LicenceObject {
        private String name;
        private String url;

    }

    static class PathsObject {

    }

    static class PathItemObject {
        private OperationObject get;
        private OperationObject put;
        private OperationObject post;
        private OperationObject delete;
        private OperationObject options;
        private OperationObject head;
        private OperationObject patch;

    }

    static class OperationObject {
        private String[] tags;
        private String summary;
        private String description;
        private String operationId;
        private String[] consumes;
        private String[] produces;

    }

    static class DefinitionsObject {

    }

    static class ParametersDefinitionsObject {

    }

    static class ResponsesDefinitionsObject {

    }

    static class SecurityDefinitionsObject {

    }

    static class SecurityRequirementObject {

    }

    static class TagObject {

    }

    static class ExternalDocumentObject {

    }
}
