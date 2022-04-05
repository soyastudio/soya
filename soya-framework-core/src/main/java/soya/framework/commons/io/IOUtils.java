package soya.framework.commons.io;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class IOUtils {
    private IOUtils() {
    }

    public static String toString(InputStream inputStream) throws IOException {
        return toString(inputStream, StandardCharsets.UTF_8);
    }

    public static String toString(InputStream inputStream, Charset charset) throws IOException {
        return CharStreams.toString(new InputStreamReader(inputStream, charset));
    }
}
