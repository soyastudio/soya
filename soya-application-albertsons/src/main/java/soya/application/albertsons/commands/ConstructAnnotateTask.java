package soya.application.albertsons.commands;

import soya.framework.util.CodeBuilder;
import soya.framework.commandline.Command;
import soya.framework.commandline.tasks.apache.xmlbeans.xs.XsNode;
import soya.framework.knowledge.KnowledgeTreeNode;

import java.math.BigInteger;

@Command(group = "business-object-development", name = "construct-annotate",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class ConstructAnnotateTask extends XPathMappingsTask {

    @Override
    protected void annotate() {
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
            System.out.println("============== " + source + " -> " + node.getPath());
            mappings.get(node.getPath()).error = "ARRAY_LEVEL";

        } else {
            String token = source;
            if (!token.endsWith("[*]")) {
                token = token.substring(0, token.lastIndexOf("[*]") + 3);
            }

            KnowledgeTreeNode<XsNode> parent = findArrayParent(node);
            if (parent != null) {
                mappings.get(parent.getPath()).arrayMapping(token);

                token = token.substring(0, token.lastIndexOf("[*]"));
                if (token.contains("[*]")) {
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

    /*

        protected void annotateParent(KnowledgeTreeNode<XsNode> node) {
            if (node != null) {
                KnowledgeTreeNode<XsNode> parent = node.getParent();
                while (parent != null) {
                    if (parent.getAnnotation(CONSTRUCTION_NAMESPACE) == null) {
                        Construction construction = createConstruction(parent);
                        parent.annotate(CONSTRUCTION_NAMESPACE, construction);
                        annotateParent(parent);

                    } else {
                        break;
                    }
                }
            }
        }

        protected KnowledgeTreeNode<XsNode> trackArrayParent(String path, KnowledgeTreeNode<XsNode> node) {
            if (arrayMappings.containsKey(path)) {
                return arrayMappings.get(path);

            } else {
                KnowledgeTreeNode<XsNode> parent = node.getParent();
                while (parent != null) {
                    if (!BigInteger.ONE.equals(parent.origin().getMaxOccurs())) {
                        Construction pc = (Construction) parent.getAnnotation(CONSTRUCTION_NAMESPACE);
                        pc.mapArray(path, parent.getPath());
                        arrayMappings.put(path, parent);

                        String token = path.substring(0, path.lastIndexOf("[*]"));
                        if (token.contains("[*]")) {
                            try {
                                token = token.substring(0, token.lastIndexOf("[*]") + 3);
                                KnowledgeTreeNode<XsNode> pa = trackArrayParent(token, parent);
                                if (pa != null) {
                                    Construction papaConstruction = (Construction) pa.getAnnotation(CONSTRUCTION_NAMESPACE);
                                    papaConstruction.mapArray(token, pa.getPath());
                                    papaConstruction.getArray(token).addPath(pc.target);

                                    arrayMappings.put(token, pa);
                                } else {

                                }

                            } catch (Exception e) {

                            }
                        }
                        return parent;
                    }

                    parent = parent.getParent();
                }
            }

            return null;
        }
    */
    public static boolean isValidSource(String src) {
        String token = src.trim();
        if (token.contains(" ") || token.contains("\n")) {
            return false;
        }

        return true;
    }


    @Override
    protected String render() {
        CodeBuilder builder = CodeBuilder.newInstance();
        mappings.entrySet().forEach(e -> {
            String construction = e.getValue().construct();
            Mapping mapping = e.getValue();
            if (!construction.isEmpty()) {
                builder.append(e.getKey()).append("=").append(mapping.construct()).appendLine();
            }
        });

        return builder.toString();
    }

}
