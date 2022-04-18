package soya.application.albertsons.commands;

import com.google.common.base.CaseFormat;
import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.core.Command;

import java.io.File;
import java.util.*;

@Command(group = "business-object-edm", name = "edm-master-mapping",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class EdmMasterMappingCommand extends EdmMappingsCommand {

    private String edmMasterMappingFile = "/EDM/EDM Master Mapping.xlsx";
    private String indexSheet = "All Tables";
    private String mappingSheet = "MasterMapping";

    private String[] propName = new String[]{"bod", "xpath", "section", "element", "tableName", "columnName", "dataType"};
    private int[] columnIndex = {1, 2, 3, 4, 8, 9, 10};

    protected void extract() throws Exception {
        String boName = "Get" + businessObject;
        File file = new File(this.homeDir, edmMasterMappingFile);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheet(mappingSheet);

        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(1) != null) {
                String bod = cellValue(row.getCell(1));
                if (bod != null && bod.equals(boName)) {
                    EdmDataUnit unit = rowData(row, propName, columnIndex);

                    EdmTable table = tables.get(unit.getTableName().toUpperCase());
                    if (table == null) {
                        table = new EdmTable();
                        table.name = unit.getTableName();
                        tables.put(table.name.toUpperCase(), table);
                    }

                    table.columns.put(unit.columnName.toUpperCase(), unit);

                }
            }
        }

        workbook.close();
    }

    protected void annotate() throws Exception {

        String mainTable = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, businessObject);
        String mainTablePK = mainTable + "_ID";

        if(tables.containsKey(mainTable)) {
            EdmTable table = tables.get(mainTable);
            table.entityType = "ROOT";

            String pk = table.getName() + "_Id";
            table.getColumns().forEach(e -> {
                String columnName = e.getColumnName();
                if(e.getColumnName().endsWith("_Id")) {
                    if(columnName.equalsIgnoreCase(pk)) {
                        System.out.println("------------ pk: " + columnName);

                    } else {
                        String token = columnName.substring(0, columnName.length() - 3).toUpperCase();
                        if(tables.containsKey(token)) {
                            System.out.println("------------ fk: " + columnName + " referTo " + tables.get(token).getName());
                        }
                    }

                }
            });


            if(table.columns.containsKey(pk)) {
                System.out.println("===================== " + table.columns.get(pk));
            }
        }

        tables.values().forEach(e -> {
            System.out.print("------ " + e.getName() + ": ");
            if(e.getName().equalsIgnoreCase(mainTable)) {
                System.out.println("ROOT");
                e.entityType = "ROOT";

            } else if(e.columns.containsKey(mainTablePK)) {
                System.out.println("DEPENDENT");
                e.entityType = "DEPENDENT";

            } else {
                System.out.println("REFERENCE");
                e.entityType = "REFERENCE";

            }
        });

    }

    private EdmDataUnit rowData(Row row, String[] propName, int[] columnIndex) {
        if (row == null) {
            return null;
        }

        JsonObject object = new JsonObject();
        for (int i = 0; i < columnIndex.length; i++) {
            Cell cell = row.getCell(columnIndex[i]);
            if (cell != null) {
                object.addProperty(propName[i], cellValue(cell));
            }
        }

        return GSON.fromJson(object, EdmDataUnit.class);
    }

    private String cellValue(Cell cell) {
        try {
            return cell.getStringCellValue().trim();

        } catch (Exception e) {
            return null;
        }
    }
}
