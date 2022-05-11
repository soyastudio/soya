package soya.application.albertsons.commands;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import soya.framework.commons.util.CodeBuilder;
import soya.framework.core.Command;
import soya.framework.core.TaskExecutionContext;
import soya.framework.core.CommandOption;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Command(group = "business-object-edm", name = "edm-bods-creation",
        httpMethod = Command.HttpMethod.POST, httpResponseTypes = Command.MediaType.APPLICATION_JSON)
public class EdmBusinessObjectsGenerationTask extends EdmBusinessObjectsTask {

    private String[] propName = new String[]{"bod", "xpath", "section", "element", "tableName", "columnName", "dataType"};
    private int[] columnIndex = {1, 2, 3, 4, 8, 9, 10};

    @CommandOption(option = "b")
    protected String businessObject;

    protected File cmmDir;

    @Override
    protected void init() {
        super.init();
        this.cmmDir = new File(home, "CMM/BOD");
    }

    @Override
    protected Object process(Sheet sheet) throws Exception {
        Map<String, Map<String, String>> bods = new LinkedHashMap<>();
        for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(1) != null) {
                String bod = cellValue(row.getCell(1));
                if (bod != null && bod.startsWith("Get")) {
                    JsonObject obj = rowData(row, propName, columnIndex);

                    if (obj != null) {
                        String name = obj.get("bod").getAsString().substring(3);
                        Map<String, String> map = bods.get(name);
                        if (map == null) {
                            map = new LinkedHashMap<>();
                            bods.put(name, map);
                        }

                        if (obj.get("tableName") != null && obj.get("columnName") != null) {
                            String key = obj.get("tableName").getAsString() + "." + obj.get("columnName").getAsString();

                            String value = "";
                            if (obj.get("dataType") != null) {
                                value = "dataType(" + obj.get("dataType").getAsString() + ")";
                                if (obj.get("xpath") != null && obj.get("xpath").getAsString().trim().length() > 0) {
                                    value = value + "::xpath(" + obj.get("xpath").getAsString() + ")";
                                }

                            }
                            map.put(key, value);
                        }
                    }

                }
            }
        }

        return bods;
    }

    protected String render(Object result) throws Exception {
        Map<String, Map<String, String>> bods = (Map<String, Map<String, String>>) result;

        if (businessObject != null && bods.containsKey(businessObject)) {
            create(businessObject, bods.get(businessObject));
        } else {
            bods.entrySet().forEach(e -> {
                create(e.getKey(), e.getValue());
            });
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(bods.keySet());
    }

    private JsonObject rowData(Row row, String[] propName, int[] columnIndex) {
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

        return object;
    }

    protected void create(String businessObject, Map<String, String> mappings) {
        File cmm = new File(cmmDir, "Get" + businessObject + ".xsd");
        if(!cmm.exists()) {
            return;
        }

        File dir = new File(edmDir, businessObject);
        if (!dir.exists()) {
            TaskExecutionContext.getInstance().getExecutorService().execute(() -> {
                try {
                    dir.mkdirs();

                    File edmMapping = new File(dir, "edm-mappings.properties");
                    edmMapping.createNewFile();

                    Set<String> tables = new HashSet<>();
                    CodeBuilder builder = CodeBuilder.newInstance();
                    mappings.entrySet().forEach(e -> {
                        String tbl = e.getKey();
                        tbl = tbl.substring(0, tbl.indexOf('.'));

                        if (!tables.contains(tbl)) {
                            if (tables.size() > 0) {
                                builder.appendLine();
                            }

                            builder.append(tbl).append("=").appendLine("entityType(?)");
                            tables.add(tbl);
                        }

                        builder.append(e.getKey()).append("=").appendLine(e.getValue());
                    });

                    builder.appendLine();

                    FileUtils.write(edmMapping, builder.toString(), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
