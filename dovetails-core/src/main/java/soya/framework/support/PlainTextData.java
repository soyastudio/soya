package soya.framework.support;

import soya.framework.DataObject;
import soya.framework.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class PlainTextData implements DataObject {

    private String contents;
    private PlainTextDataBuilder builder;

    protected PlainTextData(String contents) {
        this.contents = contents;
    }

    @Override
    public String getAsString() {
        return contents;
    }

    public static PlainTextDataBuilder builder() {
        return new PlainTextDataBuilder();
    }

    public static class PlainTextDataBuilder {
        private PlainTextDataBuilder() {
        }

        public PlainTextData fromResource(String path, ClassLoader classLoader) throws IOException {
            InputStream is = classLoader.getResourceAsStream(path);
            String contents = IOUtils.toString(is);

            return new PlainTextData(contents);
        }

        public PlainTextData fromString(String contents) throws IOException {
            return new PlainTextData(contents);
        }
    }
}
