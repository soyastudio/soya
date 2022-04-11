package soya.application.albertsons.commands;

import soya.framework.core.Command;
import soya.framework.core.CommandOption;
import soya.framework.core.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

@Command(group = "business-object-development", name = "mappings-override", httpResponseTypes = Command.MediaType.TEXT_PLAIN)
public class MappingsOverrideCommand extends MappingsRenderCommand {

    @CommandOption(option = "o", dataForProcessing = true)
    protected String override;

    @Override
    protected String execute() throws Exception {
        if(override != null) {
            String contents = Resources.getResourceAsString(override);
            try {
                BufferedReader reader = new BufferedReader(new StringReader(contents));
                String line = reader.readLine();
                while (line != null) {
                    if (line.length() > 0 && !line.trim().startsWith("#") && line.contains("=")) {
                        String key = line.substring(0, line.indexOf("=")).trim();
                        String value = line.substring(line.indexOf("=") + 1).trim();

                        Mapping mapping = mappings.get(key);
                        if (mapping != null) {
                            Function[] functions = toFunctions(value);
                            for (Function function : functions) {
                                String name = function.getName();
                                String parameter = function.getParameters()[0];
                                if ("type".equals(name)) {
                                    mapping.type = parameter;
                                } else if ("cardinality".equals(name)) {
                                    mapping.cardinality = parameter;
                                }
                            }
                        }
                    }

                    line = reader.readLine();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return super.execute();
    }
}
