package soya.application.albertsons.commands;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.commands.apache.poi.XlsxUtils;
import soya.framework.core.Command;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Command(group = "business-object-analysis", name = "edm-master-mapping", httpMethod = Command.HttpMethod.GET)
public class EdmMasterMapping extends BusinessObjectCommand {
    private String edmMasterMappingFile = "/EDM/EDM Master Mapping.xlsx";
    private String indexSheet = "All Tables";
    private String mappingSheet = "MasterMapping";

    @Override
    protected String execute() throws Exception {
        File file = new File(this.homeDir, edmMasterMappingFile);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheet(mappingSheet);

        return "lastRowNum: " + sheet.getLastRowNum();
    }

    private static List<Map<String, String>> load(File xlsx, String sheetName) throws IOException {
        String startToken = "#";
        String[] columnNames = {"Target", "DataType", "Cardinality", "Mapping", "Source", "Version"};
        List<Map<String, String>> result = XlsxUtils.extract(xlsx, sheetName, startToken, columnNames);
        return result;
    }
}
