package soya.application.albertsons.commands;

import soya.framework.action.Command;
import soya.framework.action.actions.apache.xmlbeans.XsUtils;
import soya.framework.action.actions.apache.xmlbeans.xs.XsNode;

@Command(group = "business-object-development", name = "mappings-validation", httpMethod = Command.HttpMethod.GET)
public class MappingsValidationAction extends XPathMappingsAction {

    @Override
    protected String render() {
        StringBuilder builder = new StringBuilder();
        mappings.entrySet().forEach(e -> {
            String path = e.getKey().trim();
            Mapping mapping = e.getValue();
            if (!path.startsWith("#")) {
                if (!tree.contains(path)) {
                    builder.append(path).append("=unknown()").append("\n");

                } else {
                    XsNode node = tree.get(path).origin();
                    String result = validate(mapping, node);
                    if (result != null) {
                        builder.append(path).append("=").append(result).append("\n");

                    }
                }

            }
        });

        return builder.toString();
    }

    private String validate(Mapping mapping, XsNode node) {

        String type = XsUtils.type(node);
        String cardinality = XsUtils.cardinality(node);

        if (mapping.type.equalsIgnoreCase(type) && mapping.cardinality.equalsIgnoreCase(cardinality)) {
            return null;

        } else {
            StringBuilder builder = new StringBuilder();
            if (!mapping.type.equalsIgnoreCase(type)) {
                builder.append("type(").append(mapping.type).append(" -> ").append(type).append(")::");
            }

            if (!mapping.cardinality.equals(cardinality)) {
                builder.append("cardinality(").append(mapping.cardinality).append(" -> ").append(cardinality).append(")");
            }

            String result = builder.toString();
            if (result.endsWith("::")) {
                result = result.substring(0, result.lastIndexOf("::"));
            }

            return result;

        }
    }
}
