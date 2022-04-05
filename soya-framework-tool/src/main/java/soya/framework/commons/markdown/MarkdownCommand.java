package soya.framework.commons.markdown;

import org.commonmark.node.Node;
import org.commonmark.node.Visitor;
import org.commonmark.parser.Parser;
import soya.framework.commons.cli.CommandCallable;
import soya.framework.commons.cli.CommandOption;

import java.io.FileReader;

public abstract class MarkdownCommand implements CommandCallable {

    public static final String SOURCE_TYPE_TEXT = "text";
    public static final String SOURCE_TYPE_FILE = "file";
    public static final String SOURCE_TYPE_URL = "url";

    @CommandOption(option = "s", longOption = "source")
    protected String source;

    @CommandOption(option = "c", longOption = "contents")
    protected String sourceType = SOURCE_TYPE_FILE;

    @CommandOption(option = "t", longOption = "target")
    protected String target;

    @Override
    public String call() throws Exception {
        Parser parser = Parser.builder().build();
        Node document = null;
        if(SOURCE_TYPE_TEXT.equalsIgnoreCase(sourceType)) {
            document = parser.parse(source);

        } else if(SOURCE_TYPE_FILE.equalsIgnoreCase(sourceType)) {
            document = parser.parseReader(new FileReader(source));

        } else if(SOURCE_TYPE_URL.equalsIgnoreCase(sourceType)) {

        }

        Visitor visitor = visitor();
        document.accept(visitor);

        return process(visitor);
    }

    protected abstract Visitor visitor();

    protected abstract String process(Visitor visitor) throws Exception;
}
