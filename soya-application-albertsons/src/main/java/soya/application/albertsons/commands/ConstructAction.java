package soya.application.albertsons.commands;

import com.google.common.base.CaseFormat;
import soya.framework.action.CommandOption;
import soya.framework.action.actions.apache.xmlbeans.xs.XsNode;
import soya.framework.knowledge.KnowledgeTreeNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class ConstructAction extends XPathMappingsAction {

    public static final String ASSIGNMENT_NAMESPACE = "ASSIGN";
    public static final String CONSTRUCTION_NAMESPACE = "CONSTRUCT";

    @CommandOption(option = "c")
    protected String construction = "xpath-construct.properties";

    protected Map<String, Array> arrayMap = new LinkedHashMap<>();

    @Override
    protected void annotate() {
        File file = new File(workDir, construction);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    if (line.length() > 0 && !line.trim().startsWith("#") && line.contains("=")) {
                        String key = line.substring(0, line.indexOf("=")).trim();
                        String value = line.substring(line.indexOf("=") + 1).trim();

                        Mapping mapping = mappings.get(key);
                        if (mapping != null) {

                            Function[] functions = toFunctions(value);
                            for (Function function : functions) {
                                String name = function.getName();
                                String parameter = function.getParameters()[0];
                                if ("assign".equals(name)) {
                                    mapping.assign(parameter);
                                } else if ("CONSTRUCT".equals(name)) {
                                    mapping.construct(parameter);

                                } else if ("ARRAY".equals(name)) {
                                    mapping.arrayMapping(parameter);
                                }
                            }
                        }

                    }

                    line = reader.readLine();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // construct tree:
        mappings.entrySet().forEach(e -> {
            String path = e.getKey();
            Mapping mapping = e.getValue();
            KnowledgeTreeNode<XsNode> node = tree.get(path);
            if(node != null) {
                if (mapping.assignments.size() > 0) {
                    Assignment assignment = new Assignment(path, mapping);
                    node.annotate(ASSIGNMENT_NAMESPACE, assignment);

                    mapping.assignments.forEach(s -> {
                        if (s.contains("[*]")) {
                            Array array = getParent(s);
                            if (array != null) {
                                array.addChild(path, s);
                            }
                        } else {
                            List<Array> includedArrays = findIncludedArrays(path);
                            if(includedArrays != null) {
                                includedArrays.forEach(inc -> {
                                    inc.addChild(path, s);
                                });
                            }
                        }
                    });

                } else if (mapping.construction != null) {
                    Construction construction = new Construction(path);
                    mapping.arrays.forEach(a -> {
                        Array array = new Array(a, path);
                        array.setParent(getParent(a));
                        arrayMap.put(a, array);

                        construction.addArray(array);
                    });
                    node.annotate(CONSTRUCTION_NAMESPACE, construction);
                }
            }
        });
    }

    private Array getParent(String source) {
        String token = source;
        if (token.endsWith("[*]")) {
            token = token.substring(0, token.length() - 3);
        }

        if (token.contains("[*]")) {
            token = token.substring(0, token.lastIndexOf("[*]") + 3);
            return arrayMap.get(token);
        }

        return null;
    }

    private List<Array> findIncludedArrays(String xpath) {
        List<Array> results = new ArrayList<>();
        String token = xpath;
        while (token.contains("/")) {
            for(Array array: arrayMap.values()) {
                if(array.targetPath.equals(token)) {
                    results.add(array);
                }
            }

            if(results.size() > 0) {
                return results;
            }

            token = token.substring(0, token.lastIndexOf("/"));
        }

        return null;
    }

    static class Construction {

        private final String target;
        private String variable;
        private List<Array> arrayList = new ArrayList<>();

        public Construction(String target) {
            this.target = target;
            this.variable = target.substring(target.lastIndexOf("/") + 1) + "_";
        }

        public String getVariable() {
            return variable;
        }

        public void addArray(Array array) {
            if(arrayList.size() > 0) {
                array.addIndex(arrayList.size());
            }
            this.arrayList.add(array);
        }

        public List<Array> arrays() {
            return arrayList;
        }

    }

    static class Assignment {

        private String target;
        private String value;

        private String uri;
        private String variable;
        private boolean inArray;
        private String arrayMapping;

        private String assign;

        public Assignment(String target, Mapping mapping) {
            this.target = target;
            String rule = mapping.rule;
            if(mapping.rule == null && mapping.source != null) {
                rule = "DIRECT";
            }
            rule = rule.toUpperCase(Locale.ROOT);
            
            String[] assignments = mapping.assignments.toArray(new String[mapping.assignments.size()]);
            if (assignments.length == 1) {
                this.assign = assignments[0];

            } else if (assignments.length > 1) {
                this.assign = "???";

            } else if (rule.contains("DEFAULT")) {
                String value = mapping.rule.trim();
                if (rule.startsWith("DEFAULT TO")) {
                    value = value.substring("DEFAULT TO".length());

                } else if (rule.startsWith("DEFAULTTO")) {
                    value = value.substring("DEFAULTTO".length());

                } else if (rule.startsWith("DEFAULT")) {
                    value = value.substring("DEFAULT".length());
                }

                value = value.trim();

                if (value.startsWith("(")) {
                    value = value.substring(1);
                }

                if (value.endsWith(")")) {
                    value = value.substring(0, value.length() - 1);
                }

                if (value.contains(" ") || value.contains("\n")) {
                    this.assign = "???";

                } else {
                    this.assign = value;
                }

            } else if (rule.contains("DIRECT") && mapping.source != null && mapping.source.trim().length() > 0) {
                this.value = mapping.source.trim();
                if (!isValid(value)) {
                    this.assign = "???";

                } else if (value.contains("[*]")) {
                    inArray = true;
                    int index = value.lastIndexOf("[*]");

                    String var = value.substring(0, index);
                    if (var.contains("/")) {
                        var = var.substring(var.lastIndexOf("/") + 1);
                    }
                    this.variable = "_" + var;
                    this.uri = value.substring(0, index + 3);

                    this.assign = variable + "." + value.substring(index + 4).replaceAll("/", ".");

                } else {
                    this.assign = "$." + value.replaceAll("/", ".");

                }

            } else {
                this.value = "???";
            }
        }

        public String getTarget() {
            return target;
        }

        public boolean isInArray() {
            return inArray;
        }

        public String getUri() {
            return uri;
        }

        public String getVariable() {
            return variable;
        }

        public String getValue() {
            return value;
        }

        public String getAssign() {
            return assign == null ? "???" : assign;
        }

        public String getArrayMapping() {
            return arrayMapping;
        }

        public void setArrayMapping(String arrayMapping) {
            this.arrayMapping = arrayMapping;
        }

        public boolean isValid(String src) {
            String token = src.trim();
            if (token.contains("\n") || token.contains(" ")) {
                return false;
            }

            return true;
        }
    }

    static class Array {

        private final String sourcePath;
        private final String targetPath;

        private String name;
        private String variable;

        private transient Array parent;
        private Map<String, String[]> children = new LinkedHashMap<>();

        public Array(String sourcePath, String targetPath) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;

            String baseName = targetPath.substring(targetPath.lastIndexOf("/") + 1);
            this.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, baseName) + "_LOOP";
            this.variable = "_" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, baseName) + "_item";

        }

        public String getSourcePath() {
            return sourcePath;
        }

        public String getTargetPath() {
            return targetPath;
        }

        public String getName() {
            return name;
        }

        public String getVariable() {
            return variable;
        }

        public Array getParent() {
            return parent;
        }

        public Map<String, String[]> getChildren() {
            return children;
        }

        public void addIndex(int index) {
            this.name = name + index;
            this.variable = variable + index;
        }

        public String getEvaluation() {
            String token = sourcePath;
            if (parent != null) {
                token = parent.variable + token.substring(parent.sourcePath.length());
            }

            if (token.startsWith("$.")) {
                token = "_inputRootNode" + token.substring(1);
            }

            token = token.replace("[*]", ".Item");

            return token;
        }

        public List<String> paths() {
            return new ArrayList<>(children.keySet());
        }

        public void setParent(Array parent) {
            if (parent != null) {
                this.parent = parent;
                parent.addChild(targetPath, sourcePath);
            }
        }

        public void addChild(String path, String src) {
            String root = targetPath;
            if (path.startsWith(root + "/")) {
                String token = path.substring(root.length() + 1);

                StringTokenizer tokenizer = new StringTokenizer(token, "/");
                while (tokenizer.hasMoreTokens()) {
                    root = root + "/" + tokenizer.nextToken();
                    if (!children.containsKey(root)) {
                        if (root.equals(path)) {
                            children.put(root, new String[]{src});
                        } else {
                            children.put(root, new String[0]);

                        }
                    } else {
                        String[] value = children.get(root);
                        String[] dest = new String[value.length + 1];
                        System.arraycopy(value, 0, dest, 0, value.length);
                        dest[value.length] = src;
                        children.put(root, dest);
                    }
                }

            }

        }
    }

}
