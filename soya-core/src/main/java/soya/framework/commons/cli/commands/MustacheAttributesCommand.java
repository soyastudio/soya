package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "mustache-attribute", uri = "resource://mustache-attribute")
public class MustacheAttributesCommand extends ResourceCommand {
    private final String regex = "\\{\\{([A-Za-z_][A-Za-z0-9_.]*)}}";
    private final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

    @Override
    public String call() throws Exception {
        Set<String> set = new HashSet<>();
        Matcher matcher = pattern.matcher(contents());
        while (matcher.find()) {
            String token = matcher.group();
            set.add(token);
        }

        List<String> list = new ArrayList<>(set);
        Collections.sort(list);

        list.forEach(e -> {
            System.out.println(e);
        });

        return null;
    }


}
