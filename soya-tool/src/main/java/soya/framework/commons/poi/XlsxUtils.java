package soya.framework.commons.poi;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class XlsxUtils {

    public static final String DEFAULT_START_TOKEN = "#";

    public static List<Map<String, String>> extract(File file, String sheetName, String startToken, String[] columnNames) throws IOException {
        List<Map<String, String>> list = new ArrayList<>();

        String token = startToken != null && startToken.trim().length() > 0 ? startToken : DEFAULT_START_TOKEN;

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
                    if (isMappingSheet(sh, token)) {
                        sheet = sh;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            if (workbook != null) {
                workbook.close();

            }
        }

        if (sheet == null) {
            throw new IllegalStateException("Cannot locate mapping sheet.");
        }

        Map<String, MappingService.XPathMapping> mappings = new LinkedHashMap<>();

        int[] columnIndex = new int[columnNames.length];
        boolean start = false;

        try {
            Iterator<Row> sheetIterator = sheet.iterator();
            while (sheetIterator.hasNext()) {
                Row currentRow = sheetIterator.next();
                if (start) {

                    Map<String, String> line = new LinkedHashMap<>();
                    for (int k = 0; k < columnNames.length; k++) {
                        if (columnIndex[k] == 0) {
                            throw new IllegalStateException("");
                        }

                        Cell cell = currentRow.getCell(columnIndex[k]);
                        if (cell != null) {
                            String value = "";
                            if (CellType.STRING.equals(cell.getCellType())) {
                                value = cell.getStringCellValue();

                            } else if (CellType.NUMERIC.equals(cell.getCellType())) {
                                value = "" + cell.getNumericCellValue();

                            }

                            while (value.contains("\n")) {
                                value = value.replace("\n", " ");
                            }

                            XSSFCellStyle style = (XSSFCellStyle) currentRow.getCell(columnIndex[0]).getCellStyle();
                            if (style != null && style.getFont().getStrikeout()) {
                                if (k == 0) {
                                    value = "# " + value;
                                }
                            }


                            line.put(columnNames[k], value);
                        }
                    }
                    list.add(line);

                } else {
                    int first = currentRow.getFirstCellNum();
                    int last = currentRow.getLastCellNum();

                    boolean isLabelRow = false;
                    for (int i = first; i <= last; i++) {
                        Cell cell = currentRow.getCell(i);
                        if (cell != null && cell.getCellType().equals(CellType.STRING) && token.equals(cell.getStringCellValue().trim())) {
                            isLabelRow = true;
                            start = true;
                        }

                        if (isLabelRow && cell != null && cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue() != null) {
                            String label = cell.getStringCellValue().trim();
                            if (label != null) {
                                int index = find(label, columnNames);
                                if (index >= 0) {
                                    columnIndex[index] = i;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<DynaBean> extract(File file, String sheetName, String startToken, DynaClass type) throws IOException {
        List<DynaBean> list = new ArrayList<>();

        DynaProperty[] properties = type.getDynaProperties();
        int[] columnIndex = new int[properties.length];

        XSSFWorkbook workbook = null;
        Sheet sheet = null;
        String token = startToken != null && startToken.trim().length() > 0 ? startToken : DEFAULT_START_TOKEN;
        try {
            workbook = new XSSFWorkbook(file);
            if (sheetName != null) {
                sheet = workbook.getSheet(sheetName);

            } else {
                Iterator<Sheet> iterator = workbook.sheetIterator();
                while (iterator.hasNext()) {
                    Sheet sh = iterator.next();
                    if (isMappingSheet(sh, token)) {
                        sheet = sh;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            if (workbook != null) {
                workbook.close();

            }
        }

        if (sheet == null) {
            throw new IllegalStateException("Cannot locate mapping sheet.");
        }

        Map<String, MappingService.XPathMapping> mappings = new LinkedHashMap<>();
        boolean start = false;

        try {
            Iterator<Row> sheetIterator = sheet.iterator();
            while (sheetIterator.hasNext()) {
                Row currentRow = sheetIterator.next();
                if (start) {
                    DynaBean bean = type.newInstance();
                    for (int k = 0; k < properties.length; k++) {
                        if (columnIndex[k] == 0) {
                            throw new IllegalStateException("");
                        }

                        Cell cell = currentRow.getCell(columnIndex[k]);

                        bean.set(properties[k].getName(), cell.getStringCellValue());

                    }

                    list.add(bean);

                } else {
                    int first = currentRow.getFirstCellNum();
                    int last = currentRow.getLastCellNum();

                    boolean isLabelRow = false;
                    for (int i = first; i <= last; i++) {
                        Cell cell = currentRow.getCell(i);
                        if (cell != null && cell.getCellType().equals(CellType.STRING) && token.equals(cell.getStringCellValue().trim())) {
                            isLabelRow = true;
                            start = true;
                        }

                        if (isLabelRow && cell != null && cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue() != null) {
                            String label = cell.getStringCellValue().trim();
                            if (label != null) {

                                int index = find(label, properties);
                                if (index >= 0) {
                                    columnIndex[index] = i;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private static boolean isMappingSheet(Sheet sheet, String token) {
        int rowNum = Math.min(10, sheet.getLastRowNum());
        for (int i = sheet.getFirstRowNum(); i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                int colNum = Math.max(5, row.getLastCellNum());
                for (int j = row.getFirstCellNum(); j < colNum; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && CellType.STRING.equals(cell.getCellType())
                            && cell.getStringCellValue() != null
                            && token.equals(cell.getStringCellValue().trim())) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    private static int find(String label, String[] columnNames) {
        for (int i = 0; i < columnNames.length; i++) {
            if (label.equals(columnNames[i])) {
                return i;
            }
        }

        return -1;
    }

    private static int find(String label, DynaProperty[] properties) {
        for (int i = 0; i < properties.length; i++) {
            if (label.equals(properties[i].getName())) {
                return i;
            }
        }

        return -1;
    }
}
