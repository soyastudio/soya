package soya.application.albertsons.commands;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.io.BufferedReader;
import java.io.StringReader;

@Command(group = "business-object-edm", name = "edm-mappings-override", httpMethod = Command.HttpMethod.POST)
public class EdmMappingsOverrideAction extends EdmMappingsAction {

    @CommandOption(option = "o", dataForProcessing = true)
    protected String override;

    @Override
    protected void annotate() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(override));
        String line = reader.readLine();
        while (line != null) {

            if (line.length() > 0 && !line.trim().startsWith("#") && line.contains("=")) {
                String key = line.substring(0, line.indexOf("=")).trim();
                String value = line.substring(line.indexOf("=") + 1).trim();

                if (!key.contains(".")) {
                    EdmTable table = tables.get(key);
                    Function[] functions = toFunctions(value);
                    for (Function function : functions) {
                        if (function.getName().equals("entityType")) {
                            table.entityType = function.getParameters()[0];
                        }
                    }

                } else {
                    EdmDataUnit column = columns.get(key);
                    Function[] functions = toFunctions(value);
                    for (Function function : functions) {
                        if (function.getName().equals("dataType")) {
                            column.dataType = function.getParameters()[0];

                        } else if (function.getName().equals("xpath")) {
                            column.xpath = function.getParameters()[0];
                        }

                    }

                }
            }

            line = reader.readLine();
        }
    }
}
