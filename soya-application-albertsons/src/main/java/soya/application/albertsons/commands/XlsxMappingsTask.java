package soya.application.albertsons.commands;

import soya.framework.tasks.apache.poi.XlsxUtils;
import soya.framework.util.CodeBuilder;
import soya.framework.core.Command;
import soya.framework.core.CommandOption;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Command(group = "business-object-analysis", name = "xlsx-mappings", httpMethod = Command.HttpMethod.GET)
public class XlsxMappingsTask extends BusinessObjectTask {

    @CommandOption(option = "f")
    protected String mappingFile = XLSX_MAPPINGS_FILE;

    @CommandOption(option = "x")
    protected String mappingSheet;

    protected Map<String, Mapping> mappings;

    @Override
    protected String execute() throws Exception {

        File xlsx = new File(workDir, XLSX_MAPPINGS_FILE);
        mappings = load(xlsx, null);

        CodeBuilder builder = CodeBuilder.newInstance();
        mappings.entrySet().forEach(e -> {
            builder.append(e.getKey()).append("=").appendLine(e.getValue().toString());
        });

        return builder.toString();
    }

    private static Map<String, Mapping> load(File xlsx, String sheetName) throws IOException {
        String startToken = "#";
        String[] columnNames = {"Target", "DataType", "Cardinality", "Mapping", "Source", "Version"};
        List<Map<String, String>> result = XlsxUtils.extract(xlsx, sheetName, startToken, columnNames);

        Map<String, Mapping> mappings = new LinkedHashMap<>();
        result.forEach(e -> {
            String key = e.get("Target");
            String exp = toExpression(e);

            Mapping mapping = new Mapping(parseExpression(exp));
            mappings.put(key, mapping);
        });

        return mappings;
    }

    private static String toExpression(Map<String, String> value) {
        StringBuilder builder = new StringBuilder();
        builder.append("type(").append(getType(value)).append(")")
                .append("::").append("cardinality(").append(value.containsKey("Cardinality") ? value.get("Cardinality") : "???").append(")");

        if (value.containsKey("Mapping") && value.get("Mapping").trim().length() > 0) {
            String rule = value.get("Mapping");
            if (rule.toUpperCase(Locale.ROOT).contains("DEFAULT")) {
                if (rule.indexOf("'") > 0 && rule.lastIndexOf("'") > rule.indexOf("'")) {
                    String token = rule.substring(rule.indexOf("'"), rule.lastIndexOf("'") + 1);
                    builder.append("::").append("rule(DEFAULT ").append(token).append(")");

                } else {
                    builder.append("rule(???)");
                }

            } else if (rule.toUpperCase(Locale.ROOT).contains("DIRECT")) {
                builder.append("::").append("rule(DIRECT)");

            } else {
                builder.append("::").append("rule(???)");
            }

        }

        if (value.containsKey("Source") && value.get("Source").trim().length() > 0) {
            builder.append("::").append("source(").append(getSource(value)).append(")");
        }

        if (value.containsKey("Version") && value.get("Version").trim().length() > 0) {
            builder.append("::").append("version(").append(value.get("Version").toUpperCase()).append(")");
        }

        return builder.toString();
    }

    private static Function[] parseExpression(String exp) {
        List<Function> list = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(exp, "::");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(new Function(token));
        }

        return list.toArray(new Function[list.size()]);
    }

    private static String getType(Map<String, String> values) {
        String v = values.containsKey("DataType") ? values.get("DataType") : "???";
        v = v.trim();
        if (v.contains("(")) {
            v = v.substring(0, v.indexOf("(")).trim();
        }

        if (v.contains(" ")) {
            v = v.substring(0, v.indexOf(" "));
        }

        return v;
    }

    private static String getSource(Map<String, String> values) {
        StringBuilder builder = new StringBuilder();
        boolean boo = false;
        StringTokenizer tokenizer = new StringTokenizer(values.get("Source"));
        while (tokenizer.hasMoreTokens()) {
            if (boo) {
                builder.append(" ");
            }
            builder.append(tokenizer.nextToken());

            boo = true;
        }

        return builder.toString();
    }
}
