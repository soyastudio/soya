package soya.application.albertsons.commands;

import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.core.Command;

import java.io.File;

@Command(group = "business-object-edm", name = "edm-master-mapping",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class EdmMasterMappingCommand extends EdmMappingsCommand {

    private String edmMasterMappingFile = "/EDM/EDM Master Mapping.xlsx";
    private String indexSheet = "All Tables";
    private String mappingSheet = "MasterMapping";

    private String[] propName = new String[]{"bod", "xpath", "section", "element", "tableName", "columnName", "dataType"};
    private int[] columnIndex = {1, 2, 3, 4, 8, 9, 10};

    protected void extract() throws Exception {

        File file = new File(this.homeDir, edmMasterMappingFile);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheet(mappingSheet);

        String boName = "Get" + businessObject;
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
