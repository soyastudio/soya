package soya.application.albertsons.commands;

import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import soya.framework.core.Command;
import soya.framework.core.Task;
import soya.framework.core.TaskCallable;
import soya.framework.core.CommandOption;

import java.io.File;
import java.util.*;

@Command(group = "business-object-edm", name = "edm-bod-list",
        httpMethod = Command.HttpMethod.GET, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class EdmBusinessObjectsTask extends Task<String> {

    @CommandOption(option = "h", required = true,
            paramType = CommandOption.ParamType.ReferenceParam, referenceKey = "workspace.home")
    protected String home;


    private String edmMasterMappingFile = "EDM Master Mapping.xlsx";
    private String indexSheet = "All Tables";
    private String mappingSheet = "MasterMapping";

    protected File homeDir;
    protected File edmDir;
    protected File masterMappingFile;

    @Override
    public String execute() throws Exception {
        init();

        XSSFWorkbook workbook = new XSSFWorkbook(masterMappingFile);
        Sheet sheet = workbook.getSheet(mappingSheet);

        Object result = process(sheet);

        workbook.close();

        return render(result);
    }

    protected void init() {
        this.homeDir = new File(home);
        this.edmDir = new File(home, "EDM");
        this.masterMappingFile = new File(edmDir, edmMasterMappingFile);
    }

    protected Object process(Sheet sheet) throws Exception {
        Set<String> set = new LinkedHashSet<>();
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(1) != null) {
                String bod = cellValue(row.getCell(1));
                if (bod != null && bod.startsWith("Get")) {
                    set.add(bod.substring(3));

                }
            }
        }

        List<String> list = new ArrayList<>(set);
        Collections.sort(list);

        return list;
    }

    protected String render(Object result) throws Exception {
        return new GsonBuilder().setPrettyPrinting().create().toJson(result);
    }

    protected String cellValue(Cell cell) {
        try {
            return cell.getStringCellValue().trim();

        } catch (Exception e) {
            return null;
        }
    }
}
