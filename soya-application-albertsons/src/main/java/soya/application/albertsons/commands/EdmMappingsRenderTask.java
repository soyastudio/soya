package soya.application.albertsons.commands;

import soya.framework.util.CodeBuilder;
import soya.framework.commandline.Command;
import soya.framework.commandline.CommandOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "business-object-edm", name = "edm-mappings", httpMethod = Command.HttpMethod.GET)
public class EdmMappingsRenderTask extends EdmMappingsTask {

    @CommandOption(option = "f")
    protected String renderOption;


    @Override
    protected String render() {
        if(renderOption == null) {
            return super.render();
        }

        CodeBuilder builder = CodeBuilder.newInstance();
        if("table".equalsIgnoreCase(renderOption)) {
            List<String> tableNames = new ArrayList<>(tables.keySet());
            Collections.sort(tableNames);
            tableNames.forEach(e -> {
                EdmTable table = tables.get(e);
                builder.append(table.getName()).append("=")
                        .append("entityType(")
                        .append(table.getEntityType() != null ? table.getEntityType() : "?")
                        .append(")")
                        .appendLine();

            });

        } else {

            /*if("PK".equalsIgnoreCase(columnType)) {
                List<String> tableNames = new ArrayList<>(tables.keySet());
                Collections.sort(tableNames);
                tableNames.forEach(e -> {
                    EdmTable table = tables.get(e);
                    printPrimaryKey(table, builder);

                });

            } else if("FK".equalsIgnoreCase(columnType)) {

            }*/

        }

        return builder.toString();
    }

    protected void printPrimaryKey(EdmTable table, CodeBuilder builder) {
        builder.append("# table: ").appendLine(table.getName());
        table.getColumns().forEach(c -> {
            if(c.getColumnName().endsWith("_Id")) {
                builder.append(table.getName()).append(".").append(c.getColumnName()).append("=")
                        .append("columnType(").append(c.getDataType()).append(")");
                if (c.getXpath() != null && c.getXpath().trim().length() > 0) {
                    builder.append("::xpath(").append(c.getXpath()).append(")");
                }

                builder.appendLine();
            }
        });
        builder.appendLine();
    }
}
