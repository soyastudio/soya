package soya.framework.core.oas.swagger;

import java.util.ArrayList;
import java.util.List;

public class SwaggerApiDefinition {

    private transient Swagger swagger;
    private List<OperationDefinition> operations = new ArrayList<>();

    public SwaggerApiDefinition(Swagger swagger) {
        this.swagger = swagger;
        swagger.getPaths().entrySet().forEach(e -> {
            String path = e.getKey();
            Swagger.PathItemObject item = e.getValue();

            if (item.getOperation() != null) {
                operations.add(new OperationDefinition(path, "get", item.getOperation()));
            }

            if (item.postOperation() != null) {
                operations.add(new OperationDefinition(path, "post", item.postOperation()));
            }

            if (item.deleteOperation() != null) {
                operations.add(new OperationDefinition(path, "delete", item.deleteOperation()));
            }

            if (item.putOperation() != null) {
                operations.add(new OperationDefinition(path, "put", item.putOperation()));
            }

            if (item.patchOperation() != null) {
                operations.add(new OperationDefinition(path, "patch", item.patchOperation()));
            }

            if (item.headOperation() != null) {
                operations.add(new OperationDefinition(path, "head", item.headOperation()));
            }

            if (item.optionsOperation() != null) {
                operations.add(new OperationDefinition(path, "options", item.optionsOperation()));
            }
        });
    }

    public List<OperationDefinition> getOperations() {
        return operations;
    }

    public static class OperationDefinition {
        private String path;
        private String method;
        private Swagger.OperationObject operation;

        public OperationDefinition(String path, String method, Swagger.OperationObject operation) {
            this.path = path;
            this.method = method;
            this.operation = operation;

        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }

        public Swagger.OperationObject getOperation() {
            return operation;
        }
    }

}
