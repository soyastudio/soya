package soya.framework.commons.poi;

import com.google.gson.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import soya.framework.commons.util.CodeBuilder;

import java.io.*;
import java.util.*;

public class MappingService {

    private static String XPATH_SCHEMA_FILE = "xpath-schema.properties";
    private static String XPATH_MAPPING_FILE = "xpath-mapping.properties";
    private static String XPATH_ADJUSTMENT_FILE = "xpath-adjustment.properties";

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, Command> COMMANDS;

    static {
        COMMANDS = new LinkedHashMap<>();
        Class<?>[] classes = MappingService.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (Command.class.isAssignableFrom(c) && !c.isInterface()) {
                String name = c.getSimpleName();
                if (name.endsWith("Command")) {
                    name = name.substring(0, name.lastIndexOf("Command"));
                    try {
                        Command processor = (Command) c.newInstance();
                        COMMANDS.put(name.toUpperCase(), processor);

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static String process(Node node) {
        JsonElement jsonElement = estimate(node);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            Request request = GSON.fromJson(jsonElement, Request.class);
            try {
                return COMMANDS.get(request.command.toUpperCase()).execute(new Session(request));

            } catch (Exception e) {
                return e.getMessage();
            }
        }

        return null;
    }

    private static JsonElement estimate(Node node) {
        if (node.getTextContent() != null) {
            return new JsonPrimitive(node.getTextContent());

        } else if (node.getChildNodes().getLength() > 0) {
            if ("Item".equals(node.getFirstChild().getNodeName())) {
                JsonArray arr = new JsonArray();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    arr.add(estimate(child));
                }

                return arr;

            } else {
                JsonObject obj = new JsonObject();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(child.getNodeName(), estimate(child));
                }

                return obj;
            }
        }

        return null;

    }

    private static XPathMapping parseLine(String line) {
        XPathMapping mapping = null;
        if (line.contains("=")) {
            String target = line.substring(0, line.indexOf("=")).trim();
            mapping = new XPathMapping().target(target);

            String value = line.substring(line.indexOf("=") + 1).trim();
            StringTokenizer tokenizer = new StringTokenizer(value, "::");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int begin = token.indexOf("(");
                int end = token.lastIndexOf(")");
                if (begin > 0 && end > begin) {
                    String name = token.substring(0, begin);
                    String param = token.substring(begin + 1, end);

                    if ("type".equals(name)) {
                        mapping.dataType(param);

                    } else if ("cardinality".equals(name)) {
                        mapping.cardinality(param);

                    } else if ("construct".equals(name)) {

                    } else if ("array".equals(name)) {

                    } else if ("assign".equals(name)) {

                    } else if ("parent".equals(name)) {
                        if (mapping.assignment != null) {
                            mapping.assignment.parent = param;

                        } else if (mapping.construction != null) {

                        }
                    }
                }
            }
        }

        return mapping;
    }

    private static String getJsonType(String type) {
        String result = null;
        String token = type.toLowerCase();

        switch (token) {
            case "date":
            case "time":
            case "datetime":
            case "normalizedstring":
                result = "string";
                break;

            case "decimal":
                result = "double";
                break;

            case "int":
            case "positiveinteger":
            case "negativeinteger":
                result = "integer";
                break;

            case "string":
            case "boolean":
            case "short":
            case "integer":
            case "long":
            case "float":
            case "double":
                result = token;
                break;

            default:
                result = "string";
        }

        return result;
    }

    private static Object getDefaultValue(XPathMapping mapping) {
        Object value = "";
        String type = mapping.dataType;

        if ("boolean".equals(type)) {
            value = Boolean.TRUE;

        } else if ("integer".equals(type)) {
            value = 5;

        } else if ("long".equals(type)) {
            value = 9999;

        } else if ("decimal".equals(type)) {
            value = 1.99;

        } else if ("dateTime".equals(type) || "date".equals(type) || "time".equals(type)) {
            value = "2020-12-28T19:04:06.090Z";

        } else if ("string".equals(type)) {
            StringBuilder builder = new StringBuilder();
            char[] arr = null;
            if (mapping.isAttribute()) {
                arr = mapping.getAttributeName().toCharArray();
            } else {
                arr = mapping.name.toCharArray();
            }

            boolean boo = true;
            for (char c : arr) {
                if (!Character.isLetter(c)) {
                    builder.append(c);
                    boo = false;

                } else if (Character.isUpperCase(c)) {
                    if (!boo) {
                        builder.append("_");
                    }
                    builder.append(c);
                    boo = true;
                } else {
                    builder.append(Character.toUpperCase(c));
                    boo = false;
                }
            }

            value = builder.toString().toLowerCase();

        }

        return value;
    }

    private static Map<String, XPathMapping> fromPropertiesFile(File file) throws IOException {
        Map<String, XPathMapping> mappings = new LinkedHashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("=")) {
                XPathMapping mapping = parseLine(line);
                mappings.put(mapping.target, mapping);
            }
        }

        return mappings;
    }

    private static Map<String, XPathMapping> fromXlsxFile(File file, String sheetName) throws Exception {

        XSSFWorkbook workbook = null;
        Sheet sheet = null;
        try {
            workbook = new XSSFWorkbook(file);
            if (sheetName != null) {
                sheet = workbook.getSheet(sheetName);

            } else {
                Iterator<Sheet> iterator = workbook.sheetIterator();
                while (iterator.hasNext()) {
                    Sheet sh = iterator.next();
                    if (isMappingSheet(sh)) {
                        sheet = sh;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (workbook != null) {
                workbook.close();

            }
        }

        if (sheet == null) {
            throw new IllegalStateException("Cannot locate mapping sheet.");
        }

        Map<String, XPathMapping> mappings = new LinkedHashMap<>();

        int targetIndex = 0;
        int typeIndex = 0;
        int cardinalityIndex = 0;
        int ruleIndex = 0;
        int sourceIndex = 0;
        int versionIndex = 0;

        boolean start = false;

        try {
            Iterator<Row> sheetIterator = sheet.iterator();
            while (sheetIterator.hasNext()) {
                Row currentRow = sheetIterator.next();
                if (start) {
                    Cell targetCell = currentRow.getCell(targetIndex);
                    Cell typeCell = currentRow.getCell(typeIndex);
                    Cell cardinalityCell = currentRow.getCell(cardinalityIndex);
                    Cell ruleCell = currentRow.getCell(ruleIndex);
                    Cell sourceCell = currentRow.getCell(sourceIndex);
                    Cell versionCell = currentRow.getCell(versionIndex);

                    if (!isEmpty(targetCell)) {
                        String xpath = targetCell.getStringCellValue().trim();
                        mappings.put(xpath, new XPathMapping()
                                .target(xpath)
                                .dataType(isEmpty(typeCell) ? "???" : typeCell.getStringCellValue())
                                .cardinality(isEmpty(cardinalityCell) ? "???" : cardinalityCell.getStringCellValue())
                                .rule(isEmpty(ruleCell) ? "" : cellValue(ruleCell))
                                .source(isEmpty(sourceCell) ? "" : cellValue(sourceCell))
                                .version(isEmpty(sourceCell) ? "" : cellValue(versionCell))
                        );
                    }

                } else {
                    int first = currentRow.getFirstCellNum();
                    int last = currentRow.getLastCellNum();

                    boolean isLabelRow = false;
                    for (int i = first; i <= last; i++) {
                        Cell cell = currentRow.getCell(i);
                        if (cell != null && cell.getCellType().equals(CellType.STRING) && "#".equals(cell.getStringCellValue().trim())) {
                            isLabelRow = true;
                            start = true;
                        }

                        if (isLabelRow && cell != null && cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue() != null) {
                            String label = cell.getStringCellValue().trim();
                            if (label != null) {
                                switch (label) {
                                    case "Target":
                                        targetIndex = i;
                                        break;
                                    case "DataType":
                                        typeIndex = i;
                                        break;
                                    case "Cardinality":
                                        cardinalityIndex = i;
                                        break;
                                    case "Mapping":
                                        ruleIndex = i;
                                        break;
                                    case "Source":
                                        sourceIndex = i;
                                        break;
                                    case "Version":
                                        versionIndex = i;
                                        break;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mappings;
    }

    private static boolean isMappingSheet(Sheet sheet) {
        int rowNum = Math.min(10, sheet.getLastRowNum());
        for (int i = sheet.getFirstRowNum(); i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                int colNum = Math.max(5, row.getLastCellNum());
                for (int j = row.getFirstCellNum(); j < colNum; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && CellType.STRING.equals(cell.getCellType())
                            && cell.getStringCellValue() != null
                            && "#".equals(cell.getStringCellValue().trim())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean isEmpty(Cell cell) {
        return cell == null
                || !CellType.STRING.equals(cell.getCellType())
                || cell.getStringCellValue() == null
                || cell.getStringCellValue().trim().length() == 0;
    }

    private static String cellValue(Cell cell) {
        if (cell != null && CellType.STRING.equals(cell.getCellType())) {
            StringBuilder builder = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(cell.getStringCellValue());
            while (tokenizer.hasMoreTokens()) {
                builder.append(" ").append(tokenizer.nextToken().trim());
            }

            return builder.toString().trim();
        }

        return null;
    }

    static class Request {
        private String command;
        private String context;

        private String mappingFile;
        private String mappingSheet;
    }

    static class Session {
        private Request request;

        private File requirementDir;
        private File workDir;

        private File mappingFile;
        private File adjustmentFile;

        private Map<String, XPathMapping> schema;
        private Map<String, XPathMapping> mappings;

        private TreeNode mappingTree;
        private Map<String, Array> arrays = new LinkedHashMap<>();
        private Set<String> vars = new HashSet<>();

        private Session(Request request) throws Exception {
            this.request = request;

            File base = new File(request.context);
            this.requirementDir = new File(base, "requirement");
            this.workDir = new File(base, "work");

            File xpathDataTypeFile = new File(workDir, XPATH_SCHEMA_FILE);
            if (!xpathDataTypeFile.exists()) {
                throw new IllegalStateException("File '" + xpathDataTypeFile.getAbsolutePath() + "' does not exist.");
            }
            this.schema = fromPropertiesFile(xpathDataTypeFile);

            this.mappingFile = new File(requirementDir, request.mappingFile);
            if (!mappingFile.exists()) {
                throw new IllegalStateException("File '" + mappingFile.getAbsolutePath() + "' does not exist.");
            }
            this.mappings = fromXlsxFile(mappingFile, request.mappingSheet);

            Properties adjustments = new Properties();
            File adjFile = new File(workDir, XPATH_ADJUSTMENT_FILE);
            if (adjFile.exists()) {
                adjustments.load(new FileInputStream(adjFile));
            }

            this.mappingTree = new TreeNode(mappings, adjustments);

        }

        private Map<String, XPathMapping> parse(Sheet sheet) throws Exception {
            Properties adjustments = new Properties();
            if (adjustmentFile.exists()) {
                adjustments.load(new FileInputStream(adjustmentFile));
            }

            Map<String, XPathMapping> mappings = new LinkedHashMap<>();

            int targetIndex = 0;
            int typeIndex = 0;
            int cardinalityIndex = 0;
            int ruleIndex = 0;
            int sourceIndex = 0;
            int versionIndex = 0;

            boolean start = false;

            try {
                Iterator<Row> sheetIterator = sheet.iterator();
                while (sheetIterator.hasNext()) {
                    Row currentRow = sheetIterator.next();
                    if (start) {
                        Cell targetCell = currentRow.getCell(targetIndex);
                        Cell typeCell = currentRow.getCell(typeIndex);
                        Cell cardinalityCell = currentRow.getCell(cardinalityIndex);
                        Cell ruleCell = currentRow.getCell(ruleIndex);
                        Cell sourceCell = currentRow.getCell(sourceIndex);
                        Cell versionCell = currentRow.getCell(versionIndex);

                        if (!isEmpty(targetCell)) {
                            String xpath = targetCell.getStringCellValue().trim();
                            String path = getCorrectPath(xpath);
                            if (path != null) {
                                mappings.put(path, new XPathMapping()
                                        .target(path)
                                        .dataType(isEmpty(typeCell) ? "???" : typeCell.getStringCellValue())
                                        .cardinality(isEmpty(cardinalityCell) ? "???" : cardinalityCell.getStringCellValue())
                                        .rule(ruleValue(ruleCell))
                                        .source(sourceValue(sourceCell))
                                        .version(cellValue(versionCell))
                                );

                            } else if (adjustments.containsKey(xpath)) {
                                XPathMapping adj = new XPathMapping()
                                        .target(xpath)
                                        .dataType(isEmpty(typeCell) ? "" : typeCell.getStringCellValue())
                                        .cardinality(isEmpty(cardinalityCell) ? "" : cardinalityCell.getStringCellValue())
                                        .rule(ruleValue(ruleCell))
                                        .source(sourceValue(sourceCell))
                                        .version(cellValue(versionCell));

                                String value = adjustments.getProperty(xpath);

                                boolean ignore = false;
                                StringTokenizer tokenizer = new StringTokenizer(value, "::");
                                while (tokenizer.hasMoreTokens()) {
                                    String token = tokenizer.nextToken();
                                    int begin = token.indexOf("(");
                                    int end = token.lastIndexOf(")");
                                    String func = token.substring(0, begin);
                                    String param = token.substring(begin + 1, end);

                                    if ("ignore".equals(func)) {
                                        ignore = true;
                                        break;

                                    } else if ("path".equals(func)) {
                                        adj.target(param);

                                    }
                                }

                                if (!ignore) {
                                    mappings.put(adj.target, adj);
                                }

                            } else {
                                mappings.put(xpath, new XPathMapping()
                                        .target(xpath)
                                        .dataType(isEmpty(typeCell) ? "" : typeCell.getStringCellValue())
                                        .cardinality(isEmpty(cardinalityCell) ? "" : cardinalityCell.getStringCellValue())
                                        .rule(ruleValue(ruleCell))
                                        .source(sourceValue(sourceCell))
                                        .version(cellValue(versionCell))
                                        .unknown("path")
                                );
                            }

                        }

                    } else {
                        int first = currentRow.getFirstCellNum();
                        int last = currentRow.getLastCellNum();

                        boolean isLabelRow = false;
                        for (int i = first; i <= last; i++) {
                            Cell cell = currentRow.getCell(i);
                            if (cell != null && cell.getCellType().equals(CellType.STRING) && "#".equals(cell.getStringCellValue().trim())) {
                                isLabelRow = true;
                                start = true;
                            }

                            if (isLabelRow && cell != null && cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue() != null) {
                                String label = cell.getStringCellValue().trim();
                                if (label != null) {
                                    switch (label) {
                                        case "Target":
                                            targetIndex = i;
                                            break;
                                        case "DataType":
                                            typeIndex = i;
                                            break;
                                        case "Cardinality":
                                            cardinalityIndex = i;
                                            break;
                                        case "Mapping":
                                            ruleIndex = i;
                                            break;
                                        case "Source":
                                            sourceIndex = i;
                                            break;
                                        case "Version":
                                            versionIndex = i;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return mappings;
        }

        private void construct() {
            vars.clear();
            arrays.clear();
            annotate(mappingTree);
        }

        private void annotate(TreeNode node) {
            if (node.children != null && node.children.size() > 0) {
                node.children.forEach(e -> {
                    annotate(e);
                });
            } else {
                XPathMapping mapping = node.mapping;

                if (mapping.rule != null && mapping.rule.length() > 0) {
                    if (mapping.assignment == null) {
                        mapping.assignment = new Assignment();
                        String rule = mapping.rule.trim().toUpperCase();
                        if (rule.contains("DEFAULT")) {
                            if (mapping.rule.indexOf("'") > 0 && mapping.rule.indexOf("'") < mapping.rule.lastIndexOf("'")) {
                                mapping.assignment.evaluation = mapping.rule.substring(mapping.rule.indexOf("'"), mapping.rule.lastIndexOf("'") + 1);

                            } else {
                                mapping.assignment.evaluation = "???";
                            }

                        } else if (rule.contains("DIRECT") && mapping.source != null) {
                            String src = mapping.source;
                            if (src.contains("[*]")) {
                                String parent = src.substring(0, src.lastIndexOf("[*]")) + "[*]";
                                mapping.assignment.parent = parent;
                                if (arrays.containsKey(parent)) {
                                    mapping.assignment.parent = parent;
                                    Array parentArray = arrays.get(mapping.assignment.parent);
                                    mapping.assignment.evaluation = parentArray.variable + src.substring(parent.length());

                                } else {
                                    mapping.assignment.evaluation = "$." + src;
                                }
                            } else {
                                mapping.assignment.evaluation = "$." + src;
                            }
                        } else {
                            mapping.assignment.evaluation = "???";

                        }
                    }

                    annotateParent(node, vars, arrays);

                }
            }
        }

        private void annotateParent(TreeNode node, Set<String> vars, Map<String, Array> arrays) {
            TreeNode parent = node.parent;
            while (parent != null) {
                XPathMapping parentMapping = parent.mapping;
                if (parentMapping.construction == null) {
                    Construction construction = new Construction();
                    String var = parentMapping.target;
                    if (var.contains("/")) {
                        var = var.substring(var.lastIndexOf("/") + 1);
                    }
                    construction.variable = getVariable(var + "_", vars);
                    if (parentMapping.singleValue()) {
                        construction.type = "complex";

                    } else {
                        construction.type = "array";

                        String path = node.mapping.source;
                        if (path.contains("[*]")) {
                            path = path.substring(0, path.lastIndexOf("[*]"));
                            String parentPath = null;
                            if (path.contains("[*]")) {
                                parentPath = path.substring(0, path.lastIndexOf("[*]")) + "[*]";
                                if (arrays.containsKey(parentPath)) {
                                    Array parentArray = arrays.get(parentPath);
                                }
                            }

                            String base = path;
                            if (base.contains(".")) {
                                base = base.substring(base.lastIndexOf(".") + 1);
                            }
                            base = getVariable(base, vars);

                            path = path + "[*]";
                            if (!arrays.containsKey(path)) {
                                Array array = new Array(parentMapping.target, path);
                                arrays.put(array.sourcePath, array);

                                array.name = base;
                                array.variable = "_" + base;
                                array.parent = parentPath;

                                if (arrays.containsKey(array.parent)) {
                                    Array parentArray = arrays.get(array.parent);
                                    array.evaluation = parentArray.variable + array.sourcePath.substring(array.parent.length());
                                } else {
                                    array.evaluation = "$." + array.sourcePath;
                                }

                                construction.addArray(array);
                            }
                        }
                    }

                    parentMapping.construction = construction;

                    parent = parent.parent;

                } else {
                    break;
                }


            }

        }

        private String getVariable(String base, Set<String> vars) {
            String token = base;
            int count = 0;
            while (vars.contains(token)) {
                count++;
                token = token.endsWith("_") ? (base + count) : (base + "_" + count);
            }
            vars.add(token);

            return token;
        }

        public String getCorrectPath(String xpath) {
            if (schema.containsKey(xpath)) {
                return xpath;

            } else {
                int slash = xpath.lastIndexOf("/");
                String attrPath = xpath.substring(0, slash + 1) + "@" + xpath.substring(slash + 1);
                if (schema.containsKey(attrPath)) {
                    return attrPath;

                } else {
                    for (String path : schema.keySet()) {
                        if (path.equalsIgnoreCase(xpath)) {
                            return path;
                        }
                    }

                    return null;
                }
            }
        }

        private String ruleValue(Cell cell) {
            if (cell != null && cell.getStringCellValue() != null && cell.getStringCellValue().trim().length() > 0) {
                String token = cell.getStringCellValue().trim().toUpperCase();
                if (token.contains("DEFAULT")) {
                    String value = "???";
                    if (token.contains("'")) {
                        int begin = token.indexOf("'");
                        int end = token.lastIndexOf("'");
                        value = cell.getStringCellValue().substring(begin, end + 1);
                    }

                    return "DEFAULT(" + value + ")";

                } else if (token.contains("DIRECT")) {
                    return "DIRECT";

                } else {
                    return "TODO";
                }
            }

            return null;
        }

        private String sourceValue(Cell cell) {

            if (cell != null && cell.getStringCellValue() != null) {
                String token = cell.getStringCellValue().trim();
                if (token.contains(" ")) {
                    return "???";
                } else {
                    return token.replaceAll("/", ".");
                }
            }

            return "";
        }

    }

    static class XPathMapping {

        private String target;
        private String name;
        private int level;

        private String dataType;
        private String constraints;
        private String cardinality;

        private String rule;
        private String source;

        private String version;

        private Construction construction;
        private Assignment assignment;

        private String unknown;

        public XPathMapping target(String target) {
            this.target = target;
            String[] arr = target.split("/");
            this.name = arr[arr.length - 1];
            this.level = arr.length;

            return this;
        }

        public XPathMapping dataType(String dataType) {
            String token = dataType.trim();
            if (token.contains("(")) {
                this.constraints = token.substring(token.indexOf("(") + 1, token.lastIndexOf(")")).trim();
                this.dataType = token.substring(0, token.indexOf("(")).trim();

            } else if (token.contains(" ")) {
                this.dataType = token.substring(0, token.indexOf(" "));

            } else {
                this.dataType = token;
            }

            if (this.dataType.equalsIgnoreCase("complex type") || this.dataType.equalsIgnoreCase("complexType")) {
                this.dataType = "complex";

            } else if (this.dataType.equalsIgnoreCase("dateTimeStamp")) {
                this.dataType = "dateTime";

            } else if ("String".equals(this.dataType)) {
                this.dataType = "string";
            }

            return this;
        }

        public XPathMapping cardinality(String cardinality) {
            this.cardinality = cardinality;
            return this;
        }

        public XPathMapping rule(String rule) {
            this.rule = rule;
            return this;
        }

        public XPathMapping source(String source) {
            this.source = source;
            return this;
        }

        public XPathMapping version(String version) {
            this.version = version;
            return this;
        }

        public XPathMapping unknown(String unknown) {
            this.unknown = unknown;
            return this;
        }

        public String parent() {
            if (target.contains("/")) {
                return target.substring(0, target.lastIndexOf("/"));

            } else {
                return null;
            }
        }

        public boolean isAttribute() {
            return name.startsWith("@");
        }

        public String getAttributeName() {
            if (name.startsWith("@")) {
                return name.substring(1);
            }

            return null;
        }

        public boolean mandatory() {
            return cardinality.startsWith("1-");
        }

        public boolean singleValue() {
            return cardinality.endsWith("-1");
        }

        public static XPathMapping copy(XPathMapping mapping) {
            return new XPathMapping()
                    .target(mapping.target)
                    .dataType(mapping.dataType)
                    .cardinality(mapping.cardinality)
                    .rule(mapping.rule)
                    .source(mapping.source)
                    .version(mapping.version);
        }
    }

    static class Construction {
        private String type;
        private String variable;
        private List<Array> arrays;

        public void addArray(Array array) {
            if (arrays == null) {
                arrays = new ArrayList<>();
            }

            arrays.add(array);
        }
    }

    static class Array {
        private String targetPath;
        private String sourcePath;
        private String parent;
        private String name;
        private String variable;
        private String evaluation;

        public Array(String targetPath, String sourcePath) {
            this.targetPath = targetPath;
            this.sourcePath = sourcePath;
        }
    }

    static class Assignment {
        private String parent;
        private String evaluation;

    }

    static class Function {
        private String name;
        private String[] parameters;

        private Function(String exp) {
            this.name = exp.substring(0, exp.indexOf("("));
            this.parameters = exp.substring(exp.indexOf("(") + 1, exp.lastIndexOf(")")).split(",");
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = parameters[i].trim();
            }
        }
    }

    static class TreeNode {
        private XPathMapping mapping;

        private TreeNode parent;
        private List<TreeNode> children = new ArrayList<>();

        private TreeNode(XPathMapping mapping) {
            this.mapping = XPathMapping.copy(mapping);
        }

        public TreeNode(Map<String, XPathMapping> mappings, Properties adjustments) {

            Map<String, TreeNode> map = new LinkedHashMap<>();
            mappings.values().forEach(e -> {
                if (e.parent() == null) {
                    this.mapping = XPathMapping.copy(e);
                    if (adjustments.containsKey(mapping.target)) {
                        String exp = adjustments.getProperty(mapping.target);
                        adjust(mapping, exp);
                    }
                    map.put(e.target, this);

                } else {
                    TreeNode node = new TreeNode(e);
                    XPathMapping mapping = node.mapping;
                    if (adjustments.containsKey(mapping.target)) {
                        String exp = adjustments.getProperty(mapping.target);
                        adjust(mapping, exp);
                    }


                    String parentPath = mapping.parent();
                    TreeNode parent = map.get(parentPath);

                    node.parent = parent;
                    parent.children.add(node);

                    map.put(mapping.target, node);

                }
            });

        }

        private void adjust(XPathMapping mapping, String expression) {
            String[] arr = expression.split("::");
            for (String exp : arr) {
                Function function = new Function(exp);
                if ("path".equals(function.name) && function.parameters.length == 1) {
                    mapping.target(function.parameters[0]);

                } else if ("type".equals(function.name) && function.parameters.length == 1) {
                    mapping.dataType(function.parameters[0]);

                } else if ("cardinality".equals(function.name) && function.parameters.length == 1) {
                    mapping.cardinality(function.parameters[0]);

                } else if ("rule".equals(function.name) && function.parameters.length == 1) {
                    mapping.rule(function.parameters[0]);

                } else if ("source".equals(function.name) && function.parameters.length == 1) {
                    mapping.source(function.parameters[0]);

                } else if ("version".equals(function.name) && function.parameters.length == 1) {
                    mapping.version(function.parameters[0]);

                } else if ("assign".equals(function.name) && function.parameters.length > 0) {
                    mapping.assignment = new Assignment();
                    mapping.assignment.evaluation = function.parameters[0];
                    if (function.parameters.length > 1) {
                        mapping.assignment.parent = function.parameters[1];
                    }

                } else if ("construct".equals(function.name) && function.parameters.length > 0) {

                }
            }
        }

        public List<TreeNode> getAttributes() {
            List<TreeNode> attributes = new ArrayList<>();
            children.forEach(e -> {
                if (e.mapping.isAttribute()) {
                    attributes.add(e);
                }
            });

            return attributes;
        }
    }

    interface Command {
        String execute(Session session) throws Exception;
    }

    static class MappingCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                builder.append(e.getKey()).append("=").append("type(");
                XPathMapping mapping = e.getValue();
                String type = mapping.dataType != null && mapping.dataType.trim().length() > 0 ? mapping.dataType : "???";

                if (Character.isUpperCase(type.charAt(0))) {
                    type = "" + Character.toLowerCase(type.charAt(0)) + type.substring(1);
                }

                builder.append(type).append(")").append("::").append("cardinality(").append(mapping.cardinality).append(")");

                if (mapping.rule != null && mapping.rule.trim().length() > 0) {
                    builder.append("::").append("rule").append("(").append(mapping.rule).append(")");
                }

                if (mapping.source != null && mapping.source.trim().length() > 0) {
                    builder.append("::").append("source").append("(").append(mapping.source).append(")");
                }

                if (mapping.version != null && mapping.version.trim().length() > 0) {
                    builder.append("::").append("version").append("(").append(mapping.version).append(")");
                }

                builder.append("\n");


            });

            return builder.toString();
        }
    }

    static abstract class TreeBasedCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            session.construct();
            CodeBuilder builder = CodeBuilder.newInstance();

            render(session, builder);

            return builder.toString();
        }

        protected abstract void render(Session session, CodeBuilder builder);
    }

    static class MappingTreeCommand extends TreeBasedCommand {

        @Override
        protected void render(Session session, CodeBuilder builder) {
            printNode(session.mappingTree, builder);
        }

        protected void printNode(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            builder.append(mapping.target).append("=");

            if (mapping.dataType != null && mapping.dataType.trim().length() > 0) {
                builder.append("type").append("(").append(mapping.dataType).append(")");
            }

            if (mapping.cardinality != null && mapping.cardinality.trim().length() > 0) {
                builder.append("::").append("cardinality").append("(").append(mapping.cardinality).append(")");
            }

            if (mapping.rule != null && mapping.rule.trim().length() > 0) {
                builder.append("::").append("rule").append("(").append(mapping.rule).append(")");
            }

            if (mapping.source != null && mapping.source.trim().length() > 0) {
                builder.append("::").append("source").append("(").append(mapping.source).append(")");
            }

            if (mapping.version != null && mapping.version.trim().length() > 0) {
                builder.append("::").append("version").append("(").append(mapping.version).append(")");
            }

            builder.appendLine();

            if (node.children != null && node.children.size() > 0) {
                node.children.forEach(e -> {
                    printNode(e, builder);
                });
            }
        }
    }

    static class MismatchCommand extends TreeBasedCommand {

        @Override
        protected void render(Session session, CodeBuilder builder) {
            Map<String, XPathMapping> schema = session.schema;
            check(session.mappingTree, schema, builder);
        }

        protected void check(TreeNode node, Map<String, XPathMapping> schema, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            if (!schema.containsKey(mapping.target)) {
                builder.append(mapping.target).append("=path(").append(guess(mapping.target, schema)).appendLine(")");

            } else {
                if (mapping.construction != null || mapping.assignment != null) {
                    XPathMapping cmm = schema.get(mapping.target);
                    if (!cmm.dataType.equals(mapping.dataType) || !cmm.cardinality.equals(mapping.cardinality)) {

                        builder.append(mapping.target).append("=").append("MAPPING[")
                                .append("type(").append(mapping.dataType).append(")")
                                .append("::cardinality(").append(mapping.cardinality).append(")")
                                .append("] <-> CMM[")
                                .append("type(").append(cmm.dataType).append(")")
                                .append("::cardinality(").append(cmm.cardinality).append(")")
                                .appendLine("]");
                    }
                }
            }

            if (node.children != null && node.children.size() > 0) {
                node.children.forEach(e -> {
                    check(e, schema, builder);
                });
            }
        }

        protected String guess(String xpath, Map<String, XPathMapping> schema) {
            int slash = xpath.lastIndexOf("/");
            String attrPath = xpath.substring(0, slash + 1) + "@" + xpath.substring(slash + 1);
            if (schema.containsKey(attrPath)) {
                return attrPath;

            } else {
                for (String path : schema.keySet()) {
                    if (path.equalsIgnoreCase(xpath)) {
                        return path;
                    }
                }

                return "???";
            }
        }
    }

    static class AutoAdjustCommand extends MismatchCommand {
        protected void check(TreeNode node, Map<String, XPathMapping> schema, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            if (!schema.containsKey(mapping.target)) {
                builder.append(mapping.target).append("=path(").append(guess(mapping.target, schema)).appendLine(")");

            } else {
                if (mapping.construction != null || mapping.assignment != null) {
                    XPathMapping cmm = schema.get(mapping.target);
                    if (!cmm.dataType.equals(mapping.dataType) || !cmm.cardinality.equals(mapping.cardinality)) {
                        builder.append(mapping.target).append("=")
                                .append("type(").append(cmm.dataType).append(")")
                                .append("::cardinality(").append(cmm.cardinality).append(")")
                                .appendLine();
                    } else if (mapping.assignment != null && "???".equals(mapping.assignment.evaluation)) {
                        builder.append(mapping.target).append("=assign(???)").appendLine();
                    }
                }
            }

            if (node.children != null && node.children.size() > 0) {
                node.children.forEach(e -> {
                    check(e, schema, builder);
                });
            }
        }
    }

    static class ConstructCommand extends TreeBasedCommand {

        @Override
        protected void render(Session session, CodeBuilder builder) {
            printTreeNode(session.mappingTree, builder);
        }

        private void printTreeNode(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            if (mapping.assignment == null && mapping.construction == null) {
                return;
            }

            builder.append(mapping.target).append("=");
            if (mapping.assignment != null) {
                printAssignment(mapping.assignment, builder);

            } else if (mapping.construction != null) {
                printConstruction(mapping.construction, builder);
            }

            builder.appendLine();

            if (node.children != null && node.children.size() > 0) {
                node.children.forEach(e -> {
                    printTreeNode(e, builder);
                });
            }
        }

        private void printAssignment(Assignment assignment, CodeBuilder builder) {
            builder.append("assign(").append(assignment.evaluation).append(")");
        }

        private void printConstruction(Construction construction, CodeBuilder builder) {
            builder.append("construct()");
        }
    }

    static class OutputXmlCommand extends TreeBasedCommand {

        @Override
        protected void render(Session session, CodeBuilder builder) {
            printTreeNode(session.mappingTree, builder);
        }

        private void printTreeNode(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            if (mapping.assignment == null && mapping.construction == null) {
                return;
            }

            if (!mapping.isAttribute() && mapping.cardinality.startsWith("0-")) {
                builder.appendLine("<!--Optional:-->", mapping.level);
            }

            if (mapping.assignment != null && !mapping.isAttribute()) {
                builder.append("<Abs:", mapping.level)
                        .append(mapping.name)
                        .append(">")
                        .append(getDefaultValue(mapping).toString())
                        .append("</Abs:")
                        .append(mapping.name).appendLine(">");

            } else if (mapping.construction != null) {
                if (mapping.level == 1) {
                    builder.append("<", mapping.level)
                            .append(mapping.name)
                            .append(" xmlns:Abs=\"http://collab.safeway.com/it/architecture/info/default.aspx\"");
                } else if (mapping.level < 3) {
                    builder.append("<", mapping.level);
                    builder.append(mapping.name);
                } else {
                    builder.append("<Abs:", mapping.level);
                    builder.append(mapping.name);
                }

                node.getAttributes().forEach(e -> {
                    XPathMapping attr = e.mapping;
                    builder.append(" ").append(attr.getAttributeName()).append("=\"").append(getDefaultValue(attr).toString()).append("\"");
                });
                builder.appendLine(">");

                node.children.forEach(e -> {
                    printTreeNode(e, builder);
                });

                if (mapping.level < 3) {
                    builder.append("</", mapping.level);
                } else {
                    builder.append("</Abs:", mapping.level);
                }
                builder.append(mapping.name).appendLine(">");
            }
        }

        private void printAssignment(Assignment assignment, CodeBuilder builder) {
            builder.append("assign(").append(assignment.evaluation).append(")");
        }

        private void printConstruction(Construction construction, CodeBuilder builder) {
            builder.append("construct()");
        }
    }

    static class EsqlCommand extends TreeBasedCommand {
        private String brokerSchema = "com.abs.ocrp.AirMilePoints";
        private String moduleName = "ESED_AirMilePoints_Transformer_Compute";
        private String version = "1.0.0";

        private String xmlDocRoot = "xmlDocRoot";
        private String inputRootNode = "_inputRootNode";

        private int indent;

        @Override
        protected void render(Session session, CodeBuilder builder) {
            this.xmlDocRoot = session.mappingTree.mapping.name + "_";

            this.indent = 0;

            builder.append("BROKER SCHEMA ").appendLine(brokerSchema).appendLine();
            builder.append("CREATE COMPUTE MODULE ").appendLine(moduleName).appendLine();

            indent++;
            builder.appendLine("-- Declare UDPs", indent);
            builder.append("DECLARE VERSION_ID EXTERNAL CHARACTER ", indent)
                    .append("'").append(version).append("'")
                    .appendLine(";");
            builder.appendLine("DECLARE SYSTEM_ENVIRONMENT_CODE EXTERNAL CHARACTER 'PROD';", indent);
            builder.appendLine();

            builder.appendLine("-- Declare Namespace", indent);
            builder.appendLine("DECLARE Abs NAMESPACE 'https://collab.safeway.com/it/architecture/info/default.aspx';", indent);
            builder.appendLine();

            builder.appendLine("CREATE FUNCTION Main() RETURNS BOOLEAN", indent);

            builder.appendLine("BEGIN", indent);

            indent++;
            builder.appendLine("-- Declare Input Message Root", indent);
            builder.append("DECLARE ", indent)
                    .append(inputRootNode)
                    .append(" REFERENCE TO InputRoot.JSON.Data")
                    .appendLine(";");

            builder.appendLine();

            builder.appendLine("-- Declare Output Message Root", indent);
            builder.appendLine("CREATE LASTCHILD OF OutputRoot DOMAIN 'XMLNSC';", indent);
            builder.appendLine();

            builder.append("DECLARE ", indent)
                    .append(xmlDocRoot)
                    .append(" REFERENCE TO ")
                    .append("OutputRoot.XMLNSC")
                    .appendLine(";");

            builder.append("CREATE LASTCHILD OF OutputRoot.XMLNSC AS ", indent)
                    .append(xmlDocRoot)
                    .append(" TYPE XMLNSC.Folder NAME ")
                    .append("'").append(session.mappingTree.mapping.name).append("'")
                    .appendLine(";");

            builder.append("SET OutputRoot.XMLNSC.", indent)
                    .append(session.mappingTree.mapping.name)
                    .append(".(XMLNSC.NamespaceDecl)xmlns:Abs=Abs")
                    .appendLine(";");

            builder.appendLine();

            TreeNode node = session.mappingTree;
            if (node.children != null && node.children.size() > 0) {
                node.children.forEach(e -> {
                    printTreeNode(e, builder);
                });
            }

            indent--;

            builder.appendLine("END;", indent);

            indent--;
            builder.append("END MODULE;");
        }

        private void printTreeNode(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            if (mapping.assignment == null && mapping.construction == null) {
                return;
            }

            indent++;

            builder.appendLine("-- " + mapping.target, indent);
            if (mapping.assignment != null) {
                printAssignment(node, builder);

            } else if (mapping.construction != null) {
                printConstruction(node, builder);
            }

            builder.appendLine();

            if (node.children != null && node.children.size() > 0) {
                node.children.forEach(e -> {
                    printTreeNode(e, builder);
                });
            }

            indent--;
        }

        private void printConstruction(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            XPathMapping parent = node.parent == null ? null : node.parent.mapping;

            String ref = parent == null ? xmlDocRoot : parent.name + "_";
            String var = mapping.name + "_";
            String name = mapping.level > 2 ? "Abs:" + mapping.name : mapping.name;

            builder.append("DECLARE ", indent)
                    .append(var)
                    .append(" REFERENCE TO ")
                    .append(ref)
                    .appendLine(";");

            builder.append("CREATE LASTCHILD OF ", indent)
                    .append(ref)
                    .append(" AS ")
                    .append(var)
                    .append(" TYPE XMLNSC.Folder NAME ")
                    .append("'").append(name).append("'")
                    .appendLine(";");
        }

        private void printAssignment(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            XPathMapping parent = node.parent.mapping;
            builder.append("SET ", indent).append(parent.name + "_.");

            if (mapping.isAttribute()) {
                builder.append("(XMLNSC.Attribute)").append(mapping.getAttributeName());
            } else {
                builder.append("(XMLNSC.Field)Abs:").append(mapping.name);
            }

            builder.append(" = ").append(getEvaluation(mapping.assignment)).appendLine(";");
        }

        private String getEvaluation(Assignment assignment) {
            String token = assignment.evaluation;

            if (token.startsWith("$.")) {
                token = inputRootNode + "." + token.substring(2);
            }
            return token;
        }
    }


    static class UnknownPathsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                if (!session.schema.containsKey(e.getKey())) {
                    builder.append(e.getKey()).append("=path(");
                    String path = session.getCorrectPath(e.getKey());
                    if (path == null) {
                        path = "???";
                    }
                    builder.append(path).append(")").append("\n");
                }
            });
            return builder.toString();
        }
    }

    static class UnknownMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            StringBuilder builder = new StringBuilder();
            session.mappings.entrySet().forEach(e -> {
                XPathMapping mapping = e.getValue();
                if (mapping.unknown != null) {
                    builder.append(e.getKey()).append("=").append("type(");
                    String type = mapping.dataType != null && mapping.dataType.trim().length() > 0 ? mapping.dataType : "???";

                    if (Character.isUpperCase(type.charAt(0))) {
                        type = "" + Character.toLowerCase(type.charAt(0)) + type.substring(1);
                    }

                    builder.append(type).append(")").append("::").append("cardinality(").append(mapping.cardinality).append(")");

                    if (mapping.rule != null) {
                        builder.append("::");
                        if ("DEFAULT".equals(mapping.rule)) {
                            builder.append(mapping.rule);

                        } else if ("DIRECT".equals(mapping.rule)) {
                            builder.append("DIRECT(").append(mapping.source).append(")");

                        } else {
                            builder.append("TODO()");
                        }

                        if (mapping.version != null) {
                            builder.append("::").append("version(").append(mapping.version).append(")");
                        }
                    }

                    builder.append("::").append("unknown(").append(mapping.unknown).append(")");

                    builder.append("\n");
                }

            });

            return builder.toString();
        }
    }

    static class XPathJsonTypeMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            StringBuilder builder = new StringBuilder();

            Map<String, XPathMapping> schema = session.schema;
            session.mappings.entrySet().forEach(e -> {

                XPathMapping mapping = e.getValue();
                String cardinality = schema.containsKey(e.getKey()) ? schema.get(e.getKey()).cardinality : mapping.cardinality;

                if (mapping.construction != null && !cardinality.endsWith("-1")) {
                    builder.append(mapping.target).append("=").append("array").append("\n");


                } else if (mapping.assignment != null) {
                    String type = getJsonType(mapping.dataType);
                    if (!cardinality.endsWith("-1")) {
                        type = type + "_array";
                    }

                    if (!"string".equals(type)) {
                        builder.append(mapping.target).append("=").append(type).append("\n");
                    }

                }
            });

            return builder.toString();
        }
    }

    static class JsonTypeMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            StringBuilder builder = new StringBuilder();

            Map<String, XPathMapping> schema = session.schema;
            session.mappings.entrySet().forEach(e -> {

                XPathMapping mapping = e.getValue();
                String cardinality = schema.containsKey(e.getKey()) ? schema.get(e.getKey()).cardinality : mapping.cardinality;
                String type = schema.containsKey(e.getKey()) ? schema.get(e.getKey()).dataType : mapping.dataType;

                if (mapping.construction != null && !cardinality.endsWith("-1")) {
                    builder.append("array(").append(mapping.target).append(")").append(";");


                } else if (mapping.assignment != null) {
                    type = getJsonType(type);
                    if (!cardinality.endsWith("-1")) {
                        type = type + "_array";
                    }

                    if (!"string".equals(type)) {
                        builder.append(type).append("(").append(mapping.target).append(")").append(";");
                    }

                }
            });

            return builder.toString();
        }
    }

    static class ArrayMappingsCommand implements Command {

        @Override
        public String execute(Session session) throws Exception {
            session.construct();
            return GSON.toJson(session.arrays.values());
        }
    }

    static class TransformCommand extends TreeBasedCommand {

        @Override
        protected void render(Session session, CodeBuilder builder) {
            printNode(session.mappingTree, builder);
        }

        protected void printNode(TreeNode node, CodeBuilder builder) {
            XPathMapping mapping = node.mapping;
            if (mapping.construction != null) {

                builder.append("", mapping.level - 1);
                builder.append("<");
                builder.append(node.mapping.name);
                node.children.forEach(c -> {
                    if (c.mapping.name.startsWith("@")) {
                        String attrName = c.mapping.name.substring(1);
                        builder.append(" ").append(attrName).append(" =\"").append(attrName).append("\"");
                    }
                });

                builder.append(">");
                builder.appendLine();
                node.children.forEach(c -> {
                    if (!c.mapping.name.startsWith("@")) {
                        printNode(c, builder);
                    }
                });
                builder.append("", mapping.level - 1);
                builder.append("</").append(node.mapping.name).append(">");

            } else if (mapping.assignment != null) {
                if (mapping.cardinality.startsWith("0-")) {
                    builder.appendLine("<!-- Optional -->", mapping.level - 1);
                }
                builder.append("", mapping.level - 1)
                        .append("<")
                        .append(mapping.name)
                        .append(">")
                        .append(getDefaultValue(mapping).toString());
                builder.append("</").append(mapping.name).append(">");
            }
        }
    }

}
