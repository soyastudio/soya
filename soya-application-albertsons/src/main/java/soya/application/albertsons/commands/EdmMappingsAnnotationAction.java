package soya.application.albertsons.commands;

import com.google.gson.JsonParser;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "business-object-edm", name = "edm-mappings-annotation", httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class EdmMappingsAnnotationAction extends EdmMappingsAction {

    @CommandOption(option = "o", dataForProcessing = true)
    protected String override;

    protected EdmDomainContext domainContext;

    @Override
    protected void annotate() throws Exception {
        if (override == null) {
            domainContext = EdmDomainContext.newInstance(businessObject);

        } else {
            domainContext = GSON.fromJson(JsonParser.parseString(override), EdmDomainContext.class);
            String pk = domainContext.getRoot().getPrimaryKey();

            tables.entrySet().forEach(e -> {
                String tableName = e.getKey();
                EdmTable table = e.getValue();

                Entity entity = new Entity();
                entity.tableName = tableName;

                if(table.columns.containsKey(pk)) {
                    if(!domainContext.getDependents().contains(entity)) {
                        domainContext.getDependents().add(entity);
                    }

                }else {
                    domainContext.getReferences().add(entity);
                }

            });

        }
    }
/*

    protected void _annotate() throws Exception {
        String mainTable = root != null ? root.toUpperCase() : CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, businessObject);
        String mainTablePK = mainTable + "_ID";

        if (tables.containsKey(mainTable)) {
            EdmTable table = tables.get(mainTable);
            table.entityType = "ROOT";

            String pk = table.getName() + "_Id";
            table.getColumns().forEach(e -> {
                String columnName = e.getColumnName();
                if (e.getColumnName().endsWith("_Id")) {
                    if (columnName.equalsIgnoreCase(pk)) {
                        System.out.println("------------ pk: " + columnName);

                    } else {
                        String token = columnName.substring(0, columnName.length() - 3).toUpperCase();
                        if (tables.containsKey(token)) {
                            System.out.println("------------ fk: " + columnName + " referTo " + tables.get(token).getName());
                        }
                    }

                }
            });

            if (table.columns.containsKey(pk)) {
                System.out.println("===================== " + table.columns.get(pk));
            }
        }

        tables.values().forEach(e -> {
            System.out.print("------ " + e.getName() + ": ");
            if (e.getName().equalsIgnoreCase(mainTable)) {
                System.out.println("ROOT");
                e.entityType = "ROOT";

            } else if (e.columns.containsKey(mainTablePK)) {
                System.out.println("DEPENDENT");
                e.entityType = "DEPENDENT";

            } else {
                System.out.println("REFERENCE");
                e.entityType = "REFERENCE";

            }
        });

    }
*/

    @Override
    protected String render() {
        return GSON.toJson(domainContext);
    }
}
