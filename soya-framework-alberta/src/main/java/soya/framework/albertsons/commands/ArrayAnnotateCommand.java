package soya.framework.albertsons.commands;

import soya.framework.commons.cli.Command;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.transform.schema.KnowledgeTreeNode;
import soya.framework.transform.schema.xs.XsNode;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

@Command(group = "bod", name = "array-annotate")
public class ArrayAnnotateCommand extends XPathMappingsCommand {

    protected Map<String, String> arrayMappings = new LinkedHashMap<>();

    @Override
    protected String render() {
        CodeBuilder builder = CodeBuilder.newInstance();
        arrayMappings.entrySet().forEach(e -> {
            builder.append(e.getKey()).append("=").appendLine(e.getValue());
        });
        return builder.toString();
    }

    @Override
    protected void annotate() throws Exception {
        mappings.entrySet().forEach(e -> {
            String path = e.getKey();
            Mapping mapping = e.getValue();

            KnowledgeTreeNode<XsNode> node = tree.get(path);
            if (node == null) {
                System.out.println("Cannot find node with path: " + path);

            } else if (mapping.rule != null) {
                String rule = mapping.rule.trim().toUpperCase();
                String assignment = "";
                if (rule.contains("DEFAULT")) {
                    if (rule.startsWith("DEFAULT TO")) {
                        assignment = rule.substring("DEFAULT TO".length()).trim();

                    } else if (rule.startsWith("DEFAULTTO")) {
                        assignment = rule.substring("DEFAULTTO".length()).trim();

                    } else {
                        assignment = rule.substring("DEFAULT".length()).trim();

                    }

                    if (assignment.startsWith("(") && assignment.endsWith(")")) {
                        assignment = assignment.substring(1, assignment.length() - 1);
                    }
                } else if (rule.contains("DIRECT")) {
                    if (mapping.source != null && mapping.source.trim().length() > 0) {
                        if (isValidSource(mapping.source)) {
                            assignment = "$." + mapping.source.trim().replaceAll("/", ".");
                            if (assignment.contains("[*]")) {
                                annotateArrays(node, assignment);
                            }

                        } else {
                            assignment = "???";
                        }
                    }
                } else {
                    assignment = "???";
                }

                mapping.assign(assignment);

                if (!assignment.isEmpty()) {
                    KnowledgeTreeNode<XsNode> parent = node.getParent();
                    Mapping parentMapping = mappings.get(parent.getPath());
                    while (parent != null && parentMapping.construction == null) {
                        parentMapping.construct(parent.getName() + "_");

                        parent = parent.getParent();
                        if (parent != null) {
                            parentMapping = mappings.get(parent.getPath());

                        }
                    }
                }

            }
        });
    }

    private void annotateArrays(KnowledgeTreeNode<XsNode> node, String source) {
        if (getArrayDepth(source) > getArrayDepth(node)) {
            mappings.get(node.getPath()).assign("???");
            System.out.println("============== " + source + " -> " + node.getPath());

        } else {
            String token = source;
            if (!token.endsWith("[*]")) {
                token = token.substring(0, token.lastIndexOf("[*]") + 3);
            }

            KnowledgeTreeNode<XsNode> parent = findArrayParent(node);
            if (parent != null) {
                arrayMappings.put(token, parent.getPath());
                arrayMappings.put(source, parent.getPath());

                mappings.get(parent.getPath()).arrayMapping(token);

                token = token.substring(0, token.lastIndexOf("[*]"));
                if (token.contains("[*]")) {
                    token = token.substring(0, token.lastIndexOf("[*]") + 3);
                    annotateArrays(parent, token);
                }
            }

        }
    }

    private KnowledgeTreeNode<XsNode> findArrayParent(KnowledgeTreeNode<XsNode> node) {
        if (node == null) {
            return null;
        }

        KnowledgeTreeNode<XsNode> parent = node.getParent();
        while (parent != null && !isArray(parent)) {
            parent = parent.getParent();
        }

        return parent;
    }

    private boolean isArray(KnowledgeTreeNode<XsNode> node) {
        return !BigInteger.ONE.equals(node.origin().getMaxOccurs());
    }

    private int getArrayDepth(String source) {
        int depth = 0;

        String token = source;
        while (token.contains("[*]")) {
            depth++;
            token = token.substring(0, token.lastIndexOf("[*]"));
        }

        return depth;
    }

    private int getArrayDepth(KnowledgeTreeNode<XsNode> node) {
        int depth = 0;
        if (!BigInteger.ONE.equals(node.origin().getMaxOccurs())) {
            depth++;
        }

        KnowledgeTreeNode<XsNode> parent = findArrayParent(node);
        while (parent != null) {
            depth++;
            parent = findArrayParent(parent);
        }

        return depth;
    }

    public static boolean isValidSource(String src) {
        String token = src.trim();
        if (token.contains(" ") || token.contains("\n")) {
            return false;
        }

        return true;
    }

}
