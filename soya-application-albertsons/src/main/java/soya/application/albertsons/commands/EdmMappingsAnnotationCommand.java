package soya.application.albertsons.commands;

import com.google.common.base.CaseFormat;
import org.apache.commons.beanutils.BeanComparator;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.core.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "business-object-edm", name = "edm-mappings-annotation", httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class EdmMappingsAnnotationCommand extends EdmMasterMappingCommand {

    @CommandOption(option = "o", dataForProcessing = true)
    protected String override;

    @Override
    protected void annotate() throws Exception {
        if (override != null) {
            String contents = Resources.getResourceAsString(override);
            try {
                BufferedReader reader = new BufferedReader(new StringReader(contents));
                String line = reader.readLine();
                while (line != null) {
                    if (line.length() > 0 && !line.trim().startsWith("#") && line.contains("=")) {
                        String key = line.substring(0, line.indexOf("=")).trim();
                        String value = line.substring(line.indexOf("=") + 1).trim();


                    }

                    line = reader.readLine();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected String render() {
        CodeBuilder builder = CodeBuilder.newInstance();

        builder.appendLine("package soya.application.albertsons.domains;").appendLine();

        builder.appendLine("import soya.framework.erm.*;").appendLine();

        builder.append("@DomainContext(name = \"").append(businessObject).appendLine("\")");
        builder.append("public interface ").append(businessObject).appendLine("Domain {").appendLine();

        List<EdmTable> list = new ArrayList<>(tables.values());
        Collections.sort(list, new BeanComparator<>("name"));
        list.forEach(e -> {
            printTable(e, builder);

        });

        builder.appendLine("}");


        return builder.toString();
    }

    private void printTable(EdmTable table, CodeBuilder builder) {
        String className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table.getName());
        builder.append("@Entity(", 1)
                .append("tableName = \"").append(table.getName()).append("\"")
                .appendLine(")");
        builder.append("class ", 1).append(className).appendLine(" {").appendLine();

        table.getColumns().forEach(e -> {
            printColumn(e, builder);
        });

        builder.appendLine("}", 1).appendLine();
    }

    private void printColumn(EdmDataUnit column, CodeBuilder builder) {
        String columnName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column.getColumnName());
        builder.append("private ", 2).append("String ").append(columnName).appendLine(";").appendLine();
    }

}
