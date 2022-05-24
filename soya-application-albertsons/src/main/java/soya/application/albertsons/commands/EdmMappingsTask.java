package soya.application.albertsons.commands;

import com.google.common.base.CaseFormat;
import soya.framework.util.CodeBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public abstract class EdmMappingsTask extends BusinessObjectTask {

    private String edmMappingFile = "edm-mappings.properties";

    protected Map<String, EdmTable> tables = new HashMap<>();
    protected Map<String, EdmDataUnit> columns = new HashMap<>();

    @Override
    protected String execute() throws Exception {
        extract();
        annotate();
        return render();
    }

    protected void extract() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(workDir, edmMappingFile)));
        String line = reader.readLine();
        while (line != null) {
            if (line.length() > 0 && !line.trim().startsWith("#") && line.contains("=")) {
                String key = line.substring(0, line.indexOf("=")).trim();
                String value = line.substring(line.indexOf("=") + 1).trim();

                if (!key.contains(".")) {
                    EdmTable table = new EdmTable();
                    table.name = key;
                    Function[] functions = toFunctions(value);
                    for (Function function : functions) {
                        if (function.getName().equals("entityType")) {
                            table.entityType = function.getParameters()[0];
                        }
                    }

                    if (!tables.containsKey(table)) {
                        tables.put(key.toUpperCase(), table);
                    }

                } else {
                    String tbl = key.substring(0, key.indexOf('.')).toUpperCase();
                    String cln = key.substring(key.indexOf('.') + 1).trim();

                    EdmTable table = tables.get(tbl);
                    if (table == null) {
                        throw new NullPointerException("Table not found: " + tbl);
                    }

                    EdmDataUnit column = new EdmDataUnit();
                    column.columnName = cln;
                    Function[] functions = toFunctions(value);
                    for (Function function : functions) {
                        if (function.getName().equals("dataType")) {
                            column.dataType = function.getParameters()[0];

                        } else if (function.getName().equals("xpath")) {
                            column.xpath = function.getParameters()[0];
                        }

                    }

                    table.columns.put(column.columnName.toUpperCase(), column);
                    columns.put(key, column);
                }
            }

            line = reader.readLine();
        }
    }

    protected void annotate() throws Exception {

    }

    protected String render() {
        CodeBuilder builder = CodeBuilder.newInstance();
        List<String> tableNames = new ArrayList<>(tables.keySet());
        Collections.sort(tableNames);
        tableNames.forEach(e -> {
            EdmTable table = tables.get(e);
            printEdmTable(table, builder);

        });

        return builder.toString();
    }

    protected void printEdmTable(EdmTable table, CodeBuilder builder) {
        builder.append(table.getName()).append("=")
                .append("entityType(")
                .append(table.getEntityType() != null ? table.getEntityType() : "?")
                .append(")")
                .appendLine();

        table.getColumns().forEach(c -> {
            builder.append(table.getName()).append(".").append(c.getColumnName()).append("=")
                    .append("dataType(").append(c.getDataType()).append(")");
            if (c.getXpath() != null && c.getXpath().trim().length() > 0) {
                builder.append("::xpath(").append(c.getXpath()).append(")");
            }

            builder.appendLine();

        });
        builder.appendLine();
    }


    public static class EdmTable {
        String name;
        String entityType;

        Map<String, EdmDataUnit> columns = new LinkedHashMap<>();

        public String getName() {
            return name;
        }

        public String getEntityType() {
            return entityType;
        }

        public List<EdmDataUnit> getColumns() {
            return new ArrayList<>(columns.values());
        }
    }

    public static class EdmDataUnit implements Comparable<EdmDataUnit> {
        String bod;
        String tableName;
        String columnName;
        String dataType;
        String xpath;

        public String getBod() {
            return bod;
        }

        public String getTableName() {
            return tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public String getXpath() {
            return xpath;
        }

        @Override
        public int compareTo(EdmDataUnit o) {
            int result = this.tableName.compareTo(o.tableName);
            if (result == 0) {
                result = this.columnName.compareTo(o.columnName);
            }
            return result;
        }
    }

    public static class EdmDomainContext {

        String name;
        Entity root = new Entity();

        Set<Entity> dependents = new LinkedHashSet<>();
        Set<Entity> references = new LinkedHashSet<>();

        public EdmDomainContext(String name) {
            this.name = name;
            this.root = new Entity();
            root.tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
            root.primaryKey = root.tableName + "_ID";
        }

        public String getName() {
            return name;
        }

        public Entity getRoot() {
            return root;
        }

        public Set<Entity> getDependents() {
            return dependents;
        }

        public Set<Entity> getReferences() {
            return references;
        }

        public static EdmDomainContext newInstance(String name) {
            return new EdmDomainContext(name);
        }
    }

    static class Entity {
        String tableName;
        String primaryKey;

        public String getTableName() {
            return tableName;
        }

        public String getPrimaryKey() {
            return primaryKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entity)) return false;
            Entity entity = (Entity) o;
            return Objects.equals(tableName, entity.tableName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tableName);
        }
    }
}
